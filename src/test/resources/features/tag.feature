Feature: Tag

  Background:
    Given a user named robinj

  Scenario: when calling actuator then return 200
    When we call "/actuator/health"
    Then we receive a status OK_200
    And we receive:
    """yml
    status: UP
    """