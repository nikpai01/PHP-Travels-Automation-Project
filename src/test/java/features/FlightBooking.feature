Feature: PHPTravels Flight Booking Functionality
  As a user of PHPTravels
  I want to search for flights from source to destination city
  So that I can view available flights and make a booking

  Scenario: Search for flights for a specific city
    Given user is on PHPTravels home page
    When user selects "Flights" tab
    When user enters "Delhi" as Flying From
    When user enters "Mumbai" as Destination To
    And user selects Depart Date as "15 days from today"
    And user clicks search button
    Then user should see list of flights for that route on that day
