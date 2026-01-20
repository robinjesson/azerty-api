Feature: Tag

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
    And that the TagEntity entities will contain:
    """yml
    - label: tag 1 robinj
      owner.uid: robinj
    - label: tag 2 robinj
      owner.uid: robinj
    - label: tag 1 user2
      owner.uid: user2
    """

  Scenario: when calling actuator then return 200
    When robinj get "/tags"
    Then we receive a status OK_200
    And we receive only:
    """yml
    - label: tag 1 robinj
    - label: tag 2 robinj
    """