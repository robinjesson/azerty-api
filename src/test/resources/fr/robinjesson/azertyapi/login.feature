Feature: Login feature

  Scenario: Signup
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

  Scenario: Signup and can login with good password
    When we post "/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
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

  Scenario: Signup and can't login with wrong password
    When we post "/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    And we post "/auth/login":
    """yml
    uid: userTest
    password: wrongPwd
    """
    Then we receive a status FORBIDDEN_403

  Scenario: Can't create twice the same user
    When we post "/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: fakePwd
    """
    Then we receive a status OK_200
    And we post "/auth/signup":
    """yml
    uid: userTest
    email: user@test.fr
    password: otherFakePwd
    """
    Then we receive a status FORBIDDEN_403


