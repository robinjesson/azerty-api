Feature: Login feature

  Scenario: Signup
    When we post "/v0/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    Then we receive a status CREATED_201
    And the UserEntity entities contain:
    """yml
    uid: userTest
    email: user@test.fr
    password: ?not fakePwd
    lastConnection: ?isNull
    lastPasswordModification: ?notNull
    """

  Scenario: Signup and can login with good password
    When we post "/v0/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    And we post "/v0/auth/login":
    """yml
    uid: userTest
    password: fakePwd
    """
    Then we receive a status NO_CONTENT_204
    And the UserEntity entities contain:
    """yml
    uid: userTest
    lastConnection: ?notNull
    """

  Scenario: Signup and can't login with wrong password
    When we post "/v0/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    And we post "/v0/auth/login":
    """yml
    uid: userTest
    password: wrongPwd
    """
    Then we receive a status FORBIDDEN_403

  Scenario: Can't create twice the same user
    When we post "/v0/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    Then we receive a status CREATED_201
    And we post "/v0/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: otherFakePwd
    """
    Then we receive a status FORBIDDEN_403


