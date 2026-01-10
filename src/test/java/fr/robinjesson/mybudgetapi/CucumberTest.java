package fr.robinjesson.mybudgetapi;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.SpringFactory;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber-reports/index.html"},
		tags = "not @ignore",
		features = "src/test/resources/fr/robinjesson/mybudgetapi/features",
		objectFactory = SpringFactory.class,
		glue = {
				"fr.robinjesson.mybudgetapi",
				"com.decathlon.tzatziki.steps",
				"com.decathlon.tzatziki.steps.http"
		})
public class CucumberTest {
}