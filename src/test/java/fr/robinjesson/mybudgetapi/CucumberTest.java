package fr.robinjesson.mybudgetapi;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = "pretty",
		tags = "not @ignore",
		features = "classpath:features",
		glue = {
				"fr.robinjesson.mybudgetapi.steps",
				"com.decathlon.tzatziki.steps"
		})
public class CucumberTest {
}