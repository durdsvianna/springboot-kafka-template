package com.exemplo.clientecrm.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.exemplo.clientecrm.integration.steps",
        plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberRunnerTest {
} 