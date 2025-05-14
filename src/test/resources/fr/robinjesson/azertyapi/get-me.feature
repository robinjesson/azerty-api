Feature: Get connected user information

  Scenario: When existing user in jwt request its information, then the response is ok with its information
    Given a user named robinj
    And that the UserEntity entities will contain:
    """yml
    - uid: robinj
      email: robinj@email.fr
      password: x
    """
    When robinj get "/users/me"
    Then we receive a status OK_200
    And we receive:
    """yml
    uid: "robinj"
    """

  Scenario: When user in jwt is not found, then the response is not found
    Given a user named unknown
    When unknown get "/users/me"
    Then we receive a status NOT_FOUND_404

