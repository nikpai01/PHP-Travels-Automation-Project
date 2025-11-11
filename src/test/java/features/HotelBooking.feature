Feature: PHPTravels Hotel Booking Functionality
  As a user of PHPTravels
  I want to search for hotels in a specific city
  So that I can view available hotels and make a booking

  Scenario: Search for hotels in a specific city
    Given user is on PHPTravels home page
    When user selects "Hotels" tab
    When user enters "Paris" as destination
    And user selects check-in date as "15 days from today"
    And user selects check-out date as "18 days from today"
    And user clicks search button
    Then user should see list of available hotels in Paris
