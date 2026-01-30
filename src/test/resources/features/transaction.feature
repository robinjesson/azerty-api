Feature: Transaction

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
    - label: alimentation
      owner.uid: robinj
    - label: transport
      owner.uid: robinj
    - label: salaire
      owner.uid: robinj
    - label: autre_tag
      owner.uid: user2
    """


  Scenario: Un utilisateur peut créer une transaction sur son compte
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    When robinj post "/transactions?accountId=1":
    """yml
    amount: 50.00
    transactionType: EXPENSE
    tagLabels:
      - alimentation
      - transport
    """
    Then we receive a status CREATED_201
    And we receive:
    """yml
    amount: 50.00
    isPointed: false
    isReconciled: false
    tags:
      - label: alimentation
      - label: transport
    """
    And that the TransactionEntity entities contain:
    """yml
    - account.id: 1
      amount: 50.00
      transactionType: EXPENSE
      isPointed: false
      isReconciled: false
    """


  Scenario: Un utilisateur ne peut pas créer une transaction sur le compte d'un autre utilisateur
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    Given a user named user2
    When user2 post "/transactions?accountId=1":
    """yml
    amount: 50.00
    transactionType: EXPENSE
    tagLabels:
      - autre_tag
    """
    Then we receive a status FORBIDDEN_403


  Scenario: Un utilisateur ne peut pas créer une transaction sur un compte inexistant
    When robinj post "/transactions?accountId=999999":
    """yml
    amount: 50.00
    transactionType: INCOME
    tagLabels:
      - alimentation
    """
    Then we receive a status NOT_FOUND_404


  Scenario: Un utilisateur peut modifier une transaction existante
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    And that the TransactionEntity entities will contain:
    """yml
    - account.id: 1
      transactionType: EXPENSE
      amount: 30.00
      transactionDate: 2026-01-15
      isPointed: false
      isReconciled: false
    """
    When robinj put "/transactions/1":
    """yml
    amount: 75.00
    transactionType: INCOME
    tagLabels:
      - alimentation
    """
    Then we receive a status OK_200
    And that the TransactionEntity entities contain:
    """yml
    - id: 1
      account.id: 1
      amount: 75.00
      transactionType: INCOME
      transactionDate: 2026-01-15
      isPointed: false
      isReconciled: false
    """


  Scenario: Un utilisateur ne peut pas modifier la transaction d'un autre utilisateur
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    And that the TransactionEntity entities will contain:
    """yml
    - account.id: 1
      transactionType: EXPENSE
      amount: 30.00
      transactionDate: 2026-01-15
      isPointed: false
      isReconciled: false
    """
    Given a user named user2
    When user2 put "/transactions/1":
    """yml
    amount: 75.00
    transactionType: EXPENSE
    tagLabels:
      - autre_tag
    """
    Then we receive a status FORBIDDEN_403


  Scenario: Un utilisateur ne peut pas modifier une transaction inexistante
    When robinj put "/transactions/999999":
    """yml
    amount: 75.00
    transactionType: TRANSFER_DEBIT
    tagLabels:
      - alimentation
    """
    Then we receive a status NOT_FOUND_404


  Scenario: Un utilisateur peut récupérer toutes les transactions d'un compte
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    - name: compte 2
      user.uid: robinj
      startAmount: 200
    """
    And that the TransactionEntity entities will contain:
    """yml
    - account.id: 1
      transactionType: EXPENSE
      amount: 20.00
      transactionDate: 2026-01-15
      isPointed: false
      isReconciled: false
    - account.id: 1
      transactionType: INCOME
      amount: 100.00
      transactionDate: 2026-01-16
      isPointed: true
      isReconciled: false
    - account.id: 2
      transactionType: EXPENSE
      amount: 50.00
      transactionDate: 2026-01-17
      isPointed: false
      isReconciled: false
    """
    When robinj get "/transactions?accountId=1"
    Then we receive a status OK_200
    And we receive only:
    """yml
    - amount: 20.00
      isPointed: false
      isReconciled: false
    - amount: 100.00
      isPointed: true
      isReconciled: false
    """


  Scenario: Un utilisateur ne peut pas récupérer les transactions d'un compte d'un autre utilisateur
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    And that the TransactionEntity entities will contain:
    """yml
    - account.id: 1
      transactionType: EXPENSE
      amount: 20.00
      transactionDate: 2026-01-15
      isPointed: false
      isReconciled: false
    """
    Given a user named user2
    When user2 get "/transactions?accountId=1"
    Then we receive a status FORBIDDEN_403


  Scenario: Un utilisateur ne peut pas récupérer les transactions d'un compte inexistant
    When robinj get "/transactions?accountId=999999"
    Then we receive a status NOT_FOUND_404


  Scenario: Un utilisateur récupère une liste vide si le compte n'a pas de transactions
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 200
    """
    When robinj get "/transactions?accountId=1"
    Then we receive a status OK_200
    And we receive:
    """yml
    []
    """

  Scenario: An user cannot create a transaction with a null amount
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    When robinj post "/transactions?accountId=1":
    """yml
    transactionType: EXPENSE
    tagLabels:
      - alimentation
    """
    Then we receive a status BAD_REQUEST_400

  Scenario: An user cannot create a transaction with a null transaction type
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    When robinj post "/transactions?accountId=1":
    """yml
    amount: 50.00
    tagLabels:
      - alimentation
    """
    Then we receive a status BAD_REQUEST_400

  Scenario: An user cannot create a transaction with empty tags
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    When robinj post "/transactions?accountId=1":
    """yml
    amount: 50.00
    transactionType: EXPENSE
    tagLabels: []
    """
    Then we receive a status BAD_REQUEST_400

  Scenario: An user cannot update a transaction with a null amount
    Given that the AccountEntity entities will contain:
    """yml
    - name: compte 1
      user.uid: robinj
      startAmount: 100
    """
    And that the TransactionEntity entities will contain:
    """yml
    - account.id: 1
      transactionType: EXPENSE
      amount: 30.00
      isPointed: false
      isReconciled: false
      transactionDate: "2026-01-01"
    """
    When robinj put "/transactions/1":
    """yml
    transactionType: INCOME
    tagLabels:
      - alimentation
    """
    Then we receive a status BAD_REQUEST_400

