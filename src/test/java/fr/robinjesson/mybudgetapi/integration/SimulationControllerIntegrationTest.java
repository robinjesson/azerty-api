package fr.robinjesson.mybudgetapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test to verify the complete SSE flow:
 * 1. Client opens SSE connection (GET /simu/open?userId=X) - connection stays open
 * 2. Client sends multiple messages (POST /simu/send)
 * 3. Server schedules async notifications (delay 2 seconds each)
 * 4. Notifications arrive via SSE stream to the open connection
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = SimulationControllerIntegrationTest.Initializer.class)
class SimulationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine").withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));

    static class Initializer implements org.springframework.context.ApplicationContextInitializer<org.springframework.context.ConfigurableApplicationContext> {
        @Override
        public void initialize(org.springframework.context.ConfigurableApplicationContext applicationContext) {
            postgres.start();
            org.springframework.boot.test.util.TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b",
                    "jpa.hibernate.ddl-auto=create-drop"
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @Test
    void testSSEConnectionEstablished() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Verify that SSE endpoint opens a connection
        var result = mockMvc.perform(get("/simu/open?userId=test_user")
                .header("Accept", "text/event-stream"))
                .andExpect(status().isOk())
                .andReturn();

        String contentType = result.getResponse().getContentType();
        assertThat(contentType).contains("text/event-stream");
    }

    @Test
    void testPostSendReturns202WithPendingStatus() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        var result = mockMvc.perform(post("/simu/send")
                .contentType("application/json")
                .content("""
                        {
                            "userId": "test_user",
                            "content": "Integration test message"
                        }
                        """))
                .andExpect(status().isAccepted())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("PENDING");
        assertThat(responseBody).contains("msgId");
    }

    @Test
    void testMultipleSendsWithSSENotificationFlow() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        String userId = "sse_flow_test";
        List<String> capturedNotifications = new ArrayList<>();
        CountDownLatch allNotificationsReceived = new CountDownLatch(3);

        // Step 1: Open SSE connection in background thread and listen for events
        Thread sseListenerThread = new Thread(() -> {
            try {
                var mvcResult = mockMvc.perform(get("/simu/open?userId=" + userId)
                        .header("Accept", "text/event-stream"))
                        .andExpect(status().isOk())
                        .andReturn();
                
                // Poll SSE response for arriving events
                // Each message has 2 second delay, so we need to wait longer
                // Message 1: ~2s, Message 2: ~2.1s, Message 3: ~2.2s
                // + buffer for capture = ~3-4 seconds minimum
                for (int attempt = 0; attempt < 150; attempt++) { // 150 * 100ms = 15 seconds
                    if (capturedNotifications.size() >= 3) {
                        // All notifications captured, stop early
                        break;
                    }
                    
                    byte[] content = mvcResult.getResponse().getContentAsByteArray();
                    String sseData = new String(content, StandardCharsets.UTF_8);
                    
                    if (!sseData.isEmpty()) {
                        // Extract SSE data: events
                        Pattern dataPattern = Pattern.compile("data:([^\n]*)");
                        Matcher matcher = dataPattern.matcher(sseData);
                        
                        while (matcher.find()) {
                            String event = matcher.group(1).trim();
                            if (!event.isEmpty() && !capturedNotifications.contains(event)) {
                                capturedNotifications.add(event);
                                allNotificationsReceived.countDown();
                            }
                        }
                    }
                    
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sseListenerThread.start();

        // Give SSE connection time to establish
        Thread.sleep(500);

        // Step 2: Send 3 messages via POST /simu/send
        int messagesSent = 0;
        for (int i = 1; i <= 3; i++) {
            var sendResult = mockMvc.perform(post("/simu/send")
                    .contentType("application/json")
                    .content("""
                            {
                                "userId": "%s",
                                "content": "Test message %d"
                            }
                            """.formatted(userId, i)))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String responseBody = sendResult.getResponse().getContentAsString();
            assertThat(responseBody).contains("PENDING");
            
            // Extract and verify msgId format
            Pattern msgIdPattern = Pattern.compile("\"msgId\":\"([^\"]+)\"");
            Matcher matcher = msgIdPattern.matcher(responseBody);
            assertThat(matcher.find()).isTrue();
            
            messagesSent++;
            Thread.sleep(100);
        }

        // Step 3: Verify all messages were sent
        assertThat(messagesSent).isEqualTo(3);
        
        // Step 4: Wait for all 3 notifications to be captured
        // Timeout: 15 seconds (3 messages * 2s delay + 9s buffer for polling)
        boolean notificationsReceived = allNotificationsReceived.await(15, TimeUnit.SECONDS);
        assertThat(notificationsReceived)
                .as("Should receive signal for all 3 notifications before timeout")
                .isTrue();
        
        // Ensure listener thread completes
        sseListenerThread.join(2000);
        
        // Step 5: Verify that we received exactly 3 notifications via SSE stream
        assertThat(capturedNotifications)
                .as("Should receive exactly 3 SSE notifications (one per message sent)")
                .hasSize(3);
    }
}



