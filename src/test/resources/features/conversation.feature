Feature: Conversation

  Background:
    Given a user named robinj
    And that the UserEntity entities will contain:
    """yml
    - uid: robinj
      email: robinj@email.fr
      password: x
    """
    And that the ConversationEntity entities will contain:
    """yml
    - {}
    """
    And that the MessageEntity entities will contain:
    """yml
    - text: Bonjour, comment ça va?
      conversation.id: 1
      user.uid: robinj
    - text: Ça va bien, merci!
      conversation.id: 1
      user.uid: robinj
    """

  Scenario: User can retrieve messages from a conversation
    When robinj get "/conversations/1/messages"
    Then we receive a status OK_200
    And we receive:
    """yml
    - text: Bonjour, comment ça va?
      userUid: robinj
    - text: Ça va bien, merci!
      userUid: robinj
    """

  Scenario: User cannot retrieve messages from non-existent conversation
    When robinj get "/conversations/999/messages"
    Then we receive a status NOT_FOUND_404
