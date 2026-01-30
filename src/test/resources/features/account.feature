Feature: Accounts

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
    And that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 10
    - name: compte 2
      user.uid: robinj
      startAmount: 10
    - name: compte 3
      user.uid: user2
      startAmount: 10
    """


  Scenario: When a user call its accounts, he get only his ones
    When robinj get "/accounts"
    Then we receive a status OK_200
    And we receive only:
    """yml
    - name: compte 1
    - name: compte 2
    """


  Scenario: When a user create an account, then it is created for him
    When robinj post "/accounts":
    """yml
    name: new account
    startAmount: 12
    """
    Then we receive a status CREATED_201
    And we receive:
    """yml
    name: new account
    startAmount: 12
    """
    And that the AccountEntity entities contain:
    """yml
    - name: new account
      user.uid: robinj
    """
