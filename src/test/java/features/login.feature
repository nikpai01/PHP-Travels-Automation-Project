Feature: PHPTravels Login Functionality

  Scenario: Valid login with correct credentials
    Given user is on PHPTravels login page
    When user enters valid credentials
    And clicks login
    Then user should see dashboard page

  Scenario: Invalid login with wrong credentials
    Given user is on PHPTravels login page
    When user enters invalid credentials
    And clicks login
    Then user should see an error alert message
