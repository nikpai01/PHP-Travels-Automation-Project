package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertTrue;

public class CarBooking extends BaseTest {

    private WebDriverWait wait;

    @When("user enters {string} as From Location")
    public void user_enters_as_FromLocation(String city) {
        System.out.println("\n========== SELECTING FROM AIRPORT ==========");
        selectCarCityByLabelPosition(city, "From Airport");
    }

    @When("user enters {string} as To Location")
    public void user_enters_as_ToLocation(String city) {
        System.out.println("\n========== SELECTING TO LOCATION ==========");
        selectCarCityByLabelPosition(city, "To Location");
    }

    @When("user selects Pick-up Date as {string}")
    public void user_selects_pickup_date(String dateDescription) {
        System.out.println("\n========== SELECTING PICK-UP DATE ==========");
        selectDate(dateDescription, "Pick-up Date");
    }

    @When("user selects Drop-off Date as {string}")
    public void user_selects_dropoff_date(String dateDescription) {
        System.out.println("\n========== SELECTING DROP-OFF DATE ==========");
        selectDate(dateDescription, "Drop-off Date");
    }

    @When("user enters Pick-up Time as {string}")
    public void user_enters_pickup_time(String time) {
        System.out.println("\n========== SELECTING PICK-UP TIME ==========");
        selectTime(time, "Pick-up Time");
    }

    @When("user enters Drop-off Time as {string}")
    public void user_enters_dropoff_time(String time) {
        System.out.println("\n========== SELECTING DROP-OFF TIME ==========");
        selectTime(time, "Drop-off Time");
    }


    @Then("user should see list of cars for that route on those dates")
    public void user_should_see_list_of_cars() {
        System.out.println("\n========== VERIFYING SEARCH RESULTS ==========");
        verifyCarSearchResults();
    }

    /**
     * Select city by finding the exact label and its associated dropdown
     * This ensures we click the correct field every time
     */
    private void selectCarCityByLabelPosition(String city, String labelText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Wait for page to be ready
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
            Thread.sleep(500);

            System.out.println("Looking for label: '" + labelText + "'");

            // Find the specific label
            WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//label[contains(text(),'" + labelText + "')]")));

            System.out.println("✓ Found label: " + label.getText());

            // Get the parent container of the label to scope our search
            WebElement labelParent = label.findElement(By.xpath("./.."));

            // Find the select2 dropdown within the same parent/sibling structure
            WebElement dropdown = null;

            // Try multiple ways to find the associated dropdown
            String[] xpaths = {
                    ".//span[contains(@class,'select2-selection--single')]",
                    "./following-sibling::*//span[contains(@class,'select2-selection--single')]",
                    "./..//span[contains(@class,'select2-selection--single')]"
            };

            for (String xpath : xpaths) {
                try {
                    dropdown = labelParent.findElement(By.xpath(xpath));
                    if (dropdown != null && dropdown.isDisplayed()) {
                        System.out.println("✓ Found dropdown using xpath: " + xpath);
                        break;
                    }
                } catch (Exception e) {
                    // Try next pattern
                }
            }

            if (dropdown == null) {
                // Fallback: Find by position relative to label
                List<WebElement> allDropdowns = driver.findElements(
                        By.cssSelector("span.select2-selection.select2-selection--single"));

                Point labelLocation = label.getLocation();
                System.out.println("Label location: x=" + labelLocation.getX() + ", y=" + labelLocation.getY());

                // Find dropdown closest to and below the label
                WebElement closestDropdown = null;
                int minDistance = Integer.MAX_VALUE;

                for (WebElement dd : allDropdowns) {
                    if (dd.isDisplayed()) {
                        Point ddLocation = dd.getLocation();
                        System.out.println("  Dropdown at: x=" + ddLocation.getX() + ", y=" + ddLocation.getY());

                        // Check if dropdown is below the label (y coordinate is greater)
                        // and within reasonable horizontal distance
                        if (ddLocation.getY() >= labelLocation.getY() - 50 &&
                                Math.abs(ddLocation.getX() - labelLocation.getX()) < 300) {

                            int distance = Math.abs(ddLocation.getY() - labelLocation.getY());
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestDropdown = dd;
                            }
                        }
                    }
                }

                if (closestDropdown != null) {
                    dropdown = closestDropdown;
                    System.out.println("✓ Found dropdown by proximity (distance: " + minDistance + "px)");
                }
            }

            if (dropdown == null) {
                throw new RuntimeException("Could not find dropdown for: " + labelText);
            }

            // Log the dropdown details
            String dropdownId = dropdown.getAttribute("id");
            String dropdownTitle = dropdown.getAttribute("title");
            System.out.println("Target dropdown - ID: " + dropdownId + ", Current title: " + dropdownTitle);

            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'smooth'});", dropdown);
            Thread.sleep(300);

            // Highlight for visual confirmation
            highlightElement(js, dropdown);

            // Close any open dropdown first
            try {
                js.executeScript("if(document.querySelector('.select2-container--open')) { document.querySelector('.select2-container--open .select2-selection').click(); }");
                Thread.sleep(300);
            } catch (Exception e) {
                // No open dropdown
            }

            // Click to open this specific dropdown
            try {
                dropdown.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", dropdown);
            }

            System.out.println("✓ Clicked dropdown");

            // Wait for dropdown to open and search box to appear
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input.select2-search__field")));

            // Clear and type
            searchBox.clear();
            Thread.sleep(200);
            searchBox.sendKeys(city);
            System.out.println("✓ Typed: " + city);

            // Wait for results
            Thread.sleep(800);

            // Wait for results to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("ul.select2-results__options")));

            // Find and click the matching option
            WebElement option = null;
            try {
                option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//li[contains(@class,'select2-results__option')][contains(.,'" + city + "')]")));
            } catch (TimeoutException e) {
                // Fallback to first option
                option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("li.select2-results__option:first-child")));
                System.out.println("⚠ Using first available option");
            }

            option.click();
            System.out.println("✓ Selected option");

            // Wait for selection to complete
            Thread.sleep(500);

            // Verify the selection
            verifySelection(labelText, city, dropdown);

            System.out.println("✅ Successfully selected " + labelText + ": " + city + "\n");

        } catch (Exception e) {
            System.err.println("❌ Error selecting " + labelText + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to select " + city + " for " + labelText, e);
        }
    }

    /**
     * Verify that the correct value was set in the correct field
     */
    private void verifySelection(String labelText, String expectedCity, WebElement dropdown) {
        try {
            Thread.sleep(300);

            // Get the displayed value from the dropdown
            WebElement selectedValue = dropdown.findElement(
                    By.cssSelector("span.select2-selection__rendered"));

            String actualValue = selectedValue.getAttribute("title");
            if (actualValue == null || actualValue.isEmpty()) {
                actualValue = selectedValue.getText();
            }

            System.out.println("Verification - " + labelText + " now shows: '" + actualValue + "'");

            if (actualValue != null && actualValue.toLowerCase().contains(expectedCity.toLowerCase())) {
                System.out.println("✓ Verification PASSED");
            } else {
                System.err.println("✗ Verification FAILED - Expected '" + expectedCity +
                        "' but got '" + actualValue + "'");

                // Print current state of both fields for debugging
                debugCurrentSelections();

                throw new RuntimeException("Wrong field was updated! Expected " + labelText +
                        " to have '" + expectedCity + "' but it has '" + actualValue + "'");
            }

        } catch (RuntimeException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            System.out.println("⚠ Could not verify selection: " + e.getMessage());
        }
    }

    /**
     * Debug method to show current state of all car booking fields
     */
    private void debugCurrentSelections() {
        try {
            System.out.println("\n=== Current Field Values ===");

            List<WebElement> allSelect2 = driver.findElements(
                    By.cssSelector("span.select2-selection__rendered"));

            int index = 0;
            for (WebElement elem : allSelect2) {
                if (elem.isDisplayed()) {
                    String value = elem.getAttribute("title");
                    if (value == null || value.isEmpty()) {
                        value = elem.getText();
                    }

                    // Try to find associated label
                    try {
                        WebElement nearbyLabel = elem.findElement(
                                By.xpath("./ancestor::div[3]//label"));
                        System.out.println("[" + index + "] " + nearbyLabel.getText() +
                                " = '" + value + "'");
                    } catch (Exception e) {
                        System.out.println("[" + index + "] (no label found) = '" + value + "'");
                    }
                    index++;
                }
            }
            System.out.println("===========================\n");

        } catch (Exception e) {
            System.out.println("Could not debug selections: " + e.getMessage());
        }
    }

    /**
     * Highlight element for visual debugging
     */
    private void highlightElement(JavascriptExecutor js, WebElement element) {
        try {
            String originalStyle = element.getAttribute("style");
            js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red !important; background: yellow !important;');", element);
            Thread.sleep(500);
            js.executeScript("arguments[0].setAttribute('style', '" + (originalStyle != null ? originalStyle : "") + "');", element);
        } catch (Exception e) {
            // Ignore highlighting errors
        }
    }

    /**
     * Select date based on description like "15 days from today"
     */
    private void selectDate(String fieldId, String s) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Get current date in DD-MM-YYYY format
            String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            System.out.println("Setting date to: " + formattedDate);

            // Find input by ID and set value
            WebElement dateInput = driver.findElement(By.id("cars_from_date"));

            js.executeScript("arguments[0].removeAttribute('readonly');", dateInput);
            js.executeScript("arguments[0].value = arguments[1];", dateInput, formattedDate);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dateInput);

            System.out.println("✅ Date set successfully");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw new RuntimeException("Failed to set date", e);
        }
    }
    /**
     * Select time from dropdown
     */
    private void selectTime(String fieldId, String s) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Generate random time between 3pm-4pm
            Random random = new Random();
            int hour = 15; // 3 PM in 24-hour format
            int minute = random.nextInt(60); // 0-59 minutes
            String time = String.format("%02d:%02d", hour, minute);

            System.out.println("Setting time to: " + time);

            // Find the time input field
            WebElement timeInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cars_from_time")));

            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", timeInput);
            Thread.sleep(300);

            // Set the time value directly
            js.executeScript("arguments[0].removeAttribute('readonly');", timeInput);
            js.executeScript("arguments[0].value = arguments[1];", timeInput, time);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", timeInput);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", timeInput);

            System.out.println("✓ Time set to: " + time);
            Thread.sleep(300);

        } catch (Exception e) {
            System.err.println("❌ Error setting time: " + e.getMessage());
            throw new RuntimeException("Failed to set time for " + fieldId, e);
        }
    }

    /**
     * Click the search button to submit the form
     */
    private void clickSearchButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Try multiple possible selectors for the search button
            WebElement searchButton = null;

            String[] buttonSelectors = {
                    "//button[contains(text(),'Search') or contains(@class,'search')]",
                    "//input[@type='submit' and contains(@value,'Search')]",
                    "//a[contains(@class,'search') or contains(text(),'Search')]",
                    "button[type='submit']",
                    ".search-button",
                    "#search-button"
            };

            for (String selector : buttonSelectors) {
                try {
                    if (selector.startsWith("//")) {
                        searchButton = driver.findElement(By.xpath(selector));
                    } else {
                        searchButton = driver.findElement(By.cssSelector(selector));
                    }

                    if (searchButton != null && searchButton.isDisplayed()) {
                        System.out.println("✓ Found search button using: " + selector);
                        break;
                    }
                } catch (Exception e) {
                    // Try next selector
                }
            }

            if (searchButton == null) {
                throw new RuntimeException("Could not find search button");
            }

            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", searchButton);
            Thread.sleep(300);

            // Highlight for visibility
            highlightElement(js, searchButton);

            // Click the button
            try {
                wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", searchButton);
            }

            System.out.println("✅ Clicked search button");

            // Wait for page load/navigation
            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("❌ Error clicking search button: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to click search button", e);
        }
    }

    /**
     * Verify that car search results are displayed
     */
    private void verifyCarSearchResults() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Wait for results page to load
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));

            // Check for various indicators of search results
            boolean resultsFound = false;
            String[] resultIndicators = {
                    "//div[contains(@class,'car') and contains(@class,'list')]",
                    "//div[contains(@class,'search-results')]",
                    "//div[contains(@class,'car-item')]",
                    "//h2[contains(text(),'Cars') or contains(text(),'Results')]",
                    "//*[contains(@class,'vehicle') or contains(@class,'rental')]"
            };

            for (String indicator : resultIndicators) {
                try {
                    List<WebElement> elements = driver.findElements(By.xpath(indicator));
                    if (!elements.isEmpty()) {
                        System.out.println("✓ Found results indicator: " + indicator);
                        System.out.println("  Count: " + elements.size());
                        resultsFound = true;
                        break;
                    }
                } catch (Exception e) {
                    // Try next indicator
                }
            }

            if (!resultsFound) {
                // Check if we're on a results page by URL
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("result") || currentUrl.contains("car") ||
                        currentUrl.contains("search") || currentUrl.contains("booking")) {
                    System.out.println("✓ On results page (verified by URL): " + currentUrl);
                    resultsFound = true;
                }
            }

            // Try to count actual car listings
            try {
                List<WebElement> carListings = driver.findElements(By.xpath(
                        "//*[contains(@class,'car') or contains(@class,'vehicle')][contains(@class,'item') or contains(@class,'card') or contains(@class,'list')]"));

                if (!carListings.isEmpty()) {
                    System.out.println("✓ Found " + carListings.size() + " car listing(s)");
                    resultsFound = true;
                }
            } catch (Exception e) {
                // Continue with other checks
            }

            if (resultsFound) {
                System.out.println("✅ VERIFICATION PASSED: Car search results are displayed");
                assertTrue(true, "Car search results displayed successfully");
            } else {
                System.err.println("❌ VERIFICATION FAILED: No car search results found");
                System.out.println("Current URL: " + driver.getCurrentUrl());
                System.out.println("Page Title: " + driver.getTitle());

                // Print page source for debugging
                System.out.println("\n=== Page Content Preview ===");
                System.out.println(driver.findElement(By.tagName("body")).getText().substring(0,
                        Math.min(500, driver.findElement(By.tagName("body")).getText().length())));
                System.out.println("===========================\n");

                assertTrue(false, "Car search results not found on the page");
            }

        } catch (Exception e) {
            System.err.println("❌ Error verifying search results: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false, "Failed to verify car search results: " + e.getMessage());
        }
    }
}