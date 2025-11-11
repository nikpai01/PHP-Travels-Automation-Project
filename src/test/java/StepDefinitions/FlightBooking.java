package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.assertTrue;

public class FlightBooking extends BaseTest {

    private WebDriverWait wait;

    @When("user enters {string} as Flying From")
    public void user_enters_as_FlyingFrom(String city) {
        selectFlightCity(city, "from", "Flying From");
    }

    @When("user enters {string} as Destination To")
    public void user_enters_as_DestinationTo(String city) {
        selectFlightCity(city, "to", "Destination To");
    }

    private void selectFlightCity(String city, String fieldName, String fieldLabel) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Click on the input box
            WebElement cityInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='" + fieldName + "']")
            ));
            cityInput.click();
            cityInput.clear();
            cityInput.sendKeys(city);

            // Wait a moment for autocomplete suggestions to load
            Thread.sleep(500);

            // Press Enter to select the first suggestion
            cityInput.sendKeys(Keys.ENTER);

            System.out.println("✅ Selected " + fieldLabel + ": " + city);

        } catch (Exception e) {
            throw new RuntimeException("Failed to enter " + fieldLabel + ": " + e.getMessage());
        }
    }

    @When("user selects Depart Date as {string}")
    public void user_selects_depart_date_as(String offsetText) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            int daysFromToday = Integer.parseInt(offsetText.replaceAll("\\D+", ""));
            LocalDate targetDate = LocalDate.now().plusDays(daysFromToday);

            // Format as dd-MM-yyyy
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = targetDate.format(formatter);

            // Select departure date field
            WebElement dateField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[name='depart']")
            ));

            // Set the date using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];",
                    dateField, formattedDate);

            System.out.println("✅ Selected departure date: " + formattedDate);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to select departure date: " + e.getMessage());
        }
    }

    @Then("user should see list of flights for that route on that day")
    public void user_should_see_list_of_available_flights() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            // 1) If a new tab opened, switch to it
            String original = driver.getWindowHandle();
            for (String h : driver.getWindowHandles()) {
                if (!h.equals(original)) {
                    driver.switchTo().window(h);
                    break;
                }
            }

            // 2) Optional: wait for any loading overlay/spinner to go away (best-effort)
            // Adjust selector if your app shows a loading mask
            try {
                By spinner = By.cssSelector(".loading, .preloader, .spinner, .overlay, .lds-ring");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
            } catch (Exception ignored) { /* no spinner found */ }

            // 3) Wait for either the URL to match OR the results container to be visible (SPA-safe)
            By resultsContainer = By.cssSelector("main section .container"); // <— replace with a stable locator
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("flights"),
                    ExpectedConditions.visibilityOfElementLocated(resultsContainer)
            ));

            // 4) Finally assert results are visible (prefer stable selector over long xpaths)
            WebElement results = wait.until(ExpectedConditions.visibilityOfElementLocated(resultsContainer));
            org.testng.Assert.assertTrue(results.isDisplayed(), "Flight results are not visible!");

            System.out.println("✅ Flight results displayed at: " + driver.getCurrentUrl());

        } catch (Exception e) {
            // Capture quick debug info + screenshot
            System.out.println("❌ Verification failed: " + e.getMessage());
            System.out.println("URL: " + driver.getCurrentUrl());
            System.out.println("Title: " + driver.getTitle());
            // implement if you have a util
            throw e; // fail the step
        } finally {
            closeBrowser();
        }
    }
}
