package fr.robinjesson.azertyapi;

import com.decathlon.tzatziki.steps.HttpSteps;
import com.decathlon.tzatziki.utils.Patterns;
import fr.robinjesson.azertyapi.businesses.JwtBusiness;
import fr.robinjesson.azertyapi.entities.UserEntity;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class StepDefinitions {

    private final HttpSteps httpSteps;
    private final JwtBusiness jwtBusiness;
    private final PasswordEncoder passwordEncoder;

    @Given("a user named " + Patterns.VARIABLE + "$")
    public void a_user_with_uid(final String uid){
        String token = jwtBusiness.generateToken(UserEntity.builder().uid(uid).password(passwordEncoder.encode("password")).build());
        httpSteps.addHeader(uid, "Authorization", String.format("Bearer %s", token));
    }
}
