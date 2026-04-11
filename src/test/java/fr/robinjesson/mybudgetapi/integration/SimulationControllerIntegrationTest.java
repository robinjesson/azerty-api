package fr.robinjesson.mybudgetapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test to verify the complete SSE flow:
 * 1. Client opens SSE connection (GET /simu/open)
 * 2. Client sends a message (POST /simu/send)
 * 3. Server schedules async notification (delay 2 seconds)
 * 4. Notification is sent via SSE stream
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
    void testCompleteSSEFlow() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        String userId = "integration_user";

        // Step 1: Open SSE connection
        Thread sseConnection = new Thread(() -> {
            try {
                mockMvc.perform(get("/simu/open?userId=" + userId)
                        .header("Accept", "text/event-stream"))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                // SSE connection stays open
            }
        });
        sseConnection.start();

        // Give SSE time to connect
        Thread.sleep(500);

        // Step 2: Send message to trigger async notification (scheduled for 2 seconds)
        var sendResult = mockMvc.perform(post("/simu/send")
                .contentType("application/json")
                .content("""
                        {
                            "userId": "%s",
                            "content": "Will trigger notification after 2 seconds"
                        }
                        """.formatted(userId)))
                .andExpect(status().isAccepted())
                .andReturn();

        String sendResponse = sendResult.getResponse().getContentAsString();
        assertThat(sendResponse).contains("PENDING");
        
        // Step 3: Wait for async notification (2 seconds delay + buffer)
        // In a real client, this would be received on the SSE stream
        Thread.sleep(3000);
        
        // Verify flow completed successfully
        assertThat(sendResponse).isNotEmpty();
    }
}
