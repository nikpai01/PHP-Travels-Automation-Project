Feature: PHPTravels Car Booking Functionality
  As a user of PHPTravels
  I want to search for Cars from source to destination city
  So that I can view available Cars and make a booking

  Scenario: Search for Cars for a specific city
    Given user is on PHPTravels home page
    When user selects "Cars" tab
    And user enters "Delhi" as From Location
    And user enters "Mumbai" as To Location
    And user selects Pick-up Date as "15 days from today"
    And user enters Pick-up Time as "9:00am"
    And user selects Drop-off Date as "16 days from today"
    And user enters Drop-off Time as "9:00am"
    And user clicks search button
    Then user should see list of cars for that route on those dates
