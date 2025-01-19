Feature: User info

  Scenario: test user
    Given a user named robinj
    When robinj get "/users/me"
    Then we receive a status OK_200
    And we receive:
    """json
    {
      "uid": "robinj"
    }
    """

