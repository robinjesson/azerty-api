Feature: Login feature

  Scenario: Signup and login
    When we post "/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    Then we receive a status OK_200
    And the UserEntity entities contain:
    """yml
    uid: userTest
    email: user@test.fr
    password: ?not fakePwd
    """
    And we post "/auth/login":
    """yml
    uid: userTest
    password: fakePwd
    """
    Then we receive a status OK_200
    And we receive:
    """yml
    token: ?notNull
    expiresIn: ?notNull
    """
    And we post "/auth/login":
    """yml
    uid: userTest
    password: wrongPwd
    """
    Then we receive a status FORBIDDEN_403


