Feature: PHPTravels Tours Booking Functionality
  As a user of PHPTravels
  I want to apply for visa
  So that I can view availability

  Scenario: Search for Tour Packages
    Given user is on PHPTravels home page
    When user selects "Visa" tab
      When user enters "India" as From Country
    When user enters "Afghanistan" as To Country
    And user selects date entry as "15 days from today"
    And user clicks search button
    Then user should see the submission form
