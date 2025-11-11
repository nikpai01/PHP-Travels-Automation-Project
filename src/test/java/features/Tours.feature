Feature: PHPTravels Tours Booking Functionality
  As a user of PHPTravels
  I want to search for Tour Packages
  So that I can view availability and make a booking

  Scenario: Search for Tour Packages
    Given user is on PHPTravels home page
    When user selects "Tours" tab
    When user enters "Mumbai" as Visiting place
    And user selects in date as "15 days from today"
    And user clicks search button
    Then user should see list of available Tour Packages
