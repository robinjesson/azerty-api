package fr.robinjesson.azertyapi;

import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.groovy.util.Maps;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = AzertyApplication.class)
@ContextConfiguration(initializers = AzertyApplicationSteps.Initializer.class)
public class AzertyApplicationSteps {

    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:latest").withTmpFs(Maps.of("/var/lib/postgresql/data", "rw"));

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgres.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "security.jwt.secret-key=JWTKEY",
                    "jpa.hibernate.ddl-auto=create"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
