Feature: User info

  Background:
    Given a user named robinj
    And that the UserEntity entities will contain:
    """yml
    - uid: robinj
      email: robinj@email.fr
      password: x
    """

  Scenario: test user
    When robinj get "/users/me"
    Then we receive a status OK_200
    And we receive:
    """yml
    uid: "robinj"
    """

