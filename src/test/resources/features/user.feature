Feature: Users

  Background:
    Given a user named robinj
    And that the UserEntity entities will contain:
    """yml
    - uid: robinj
      email: robinj@email.fr
      password: x
    - uid: user2
      email: user2@email.fr
      password: x
    """

  Scenario: User can retrieve the list of all users
    When robinj get "/users"
    Then we receive a status OK_200
    And we receive only:
    """yml
    - uid: robinj
    - uid: user2
    """
