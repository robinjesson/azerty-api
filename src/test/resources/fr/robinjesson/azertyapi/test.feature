Feature: a polite spring boot service

  Scenario: our service can greet us
    When we call "/hello"
    Then we receive "Hello world!"