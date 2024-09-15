Feature: a polite spring boot service

  Scenario: when calling actuator then return 200
    When we call "/actuator/health"
    Then we receive a status OK_200
    And we receive:
    """json
    {
      "status": "UP"
    }
    """