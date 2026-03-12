package fr.robinjesson.mybudgetapi.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String cookieScheme = "cookieAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MyBudget API")
                        .version("v0")
                        .description("""
                                Personal budget management REST API.
                                Follows Richardson Maturity Model Level 3 (HATEOAS).
                                
                                **Versioning policy:** The URL prefix reflects the major version (e.g. `/v0`, `/v1`).
                                A new version is created only for breaking changes.
                                Previous versions are maintained in parallel during a transition period.
                                """)
                        .contact(new Contact()
                                .name("Robin Jesson")
                                .url("https://github.com/robinjesson/my-budget-api")))
                .addSecurityItem(new SecurityRequirement().addList(cookieScheme))
                .components(new Components()
                        .addSecuritySchemes(cookieScheme, new SecurityScheme()
                                .name("auth_token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("JWT token stored in an HTTP-only cookie")));
    }
}
