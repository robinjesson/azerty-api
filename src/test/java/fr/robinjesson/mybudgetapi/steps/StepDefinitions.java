package fr.robinjesson.mybudgetapi.steps;

import com.decathlon.tzatziki.steps.HttpSteps;
import com.decathlon.tzatziki.utils.Patterns;
import fr.robinjesson.mybudgetapi.businesses.JwtBusiness;
import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.security.Consts;
import io.cucumber.java.en.Given;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class StepDefinitions {

    private final HttpSteps httpSteps;
    private final JwtBusiness jwtBusiness;
    private final PasswordEncoder passwordEncoder;

    @Given("a user named " + Patterns.VARIABLE + "$")
    public void a_user_with_uid(final String uid){
        a_user_with_uid_and_password(uid, "password");
    }

    @Given("a user named " + Patterns.VARIABLE + " with password " + Patterns.VARIABLE + "$")
    public void a_user_with_uid_and_password(final String uid, final String password){
        final String token = jwtBusiness.generateToken(UserEntity.builder().uid(uid).password(passwordEncoder.encode(password)).build());
        httpSteps.addHeader(uid, HttpHeaders.COOKIE, Consts.COOKIE_NAME + "=" + token);
    }
}
