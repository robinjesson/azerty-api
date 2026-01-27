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
    - label: cb
      owner.uid: robinj
    - label: decathlon
      category: LOCATION
      owner.uid: robinj
    - label: paye
      category: CATEGORY
      owner.uid: user2
    """

  Scenario: user can retrieve only their own tags
    When robinj get "/tags"
    Then we receive a status OK_200
    And we receive only:
    """yml
    - label: cb
    - label: decathlon
      category: LOCATION
    """

  Scenario: user can create their own tag
    When robinj post "/tags":
    """yml
    label: cb
    category: PAYMENT_MEAN
    """
    Then we receive a status CREATED_201
    And we receive only:
    """yml
    label: cb
    category: PAYMENT_MEAN
    """
    And the TagEntity entities contain:
    """yml
    - label: cb
      category: PAYMENT_MEAN
      owner.uid: robinj
    """