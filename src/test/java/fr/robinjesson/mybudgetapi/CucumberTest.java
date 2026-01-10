package fr.robinjesson.mybudgetapi;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty",
        tags = "not @ignore",
        features = "src/test/resources/fr/robinjesson/mybudgetapi/features", // Path to your feature files
        glue = {
                "fr.robinjesson.mybudgetapi", // include root package so AzertyApplicationSteps is discovered
                "fr.robinjesson.mybudgetapi.features", // Finds your custom steps definitions in this package
                "com.decathlon.tzatziki.steps" // Finds Tzatziki steps definitions
        })
public class CucumberTest {
}