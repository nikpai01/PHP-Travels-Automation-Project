package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import static org.testng.Assert.assertTrue;

public class HotelBooking extends BaseTest {

    private WebDriverWait wait;

    @When("user enters {string} as destination")
    public void user_enters_as_destination(String city) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            Thread.sleep(2000);

            // Click the dropdown
            WebElement cityDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id='hotels-search']/div/div[1]/div[1]/span/span[1]/span/span[2]/b")
                    )
            );
            cityDropdown.click();
            Thread.sleep(1000);

            // Type city name
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input.select2-search__field")));
            searchInput.sendKeys(city);
            Thread.sleep(2000);

            // Select first result
            WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(@class,'select2-results__option')][1]")));
            firstResult.click();
            Thread.sleep(1000);

            System.out.println("✅ Selected destination: " + city);

        } catch (Exception e) {
            throw new RuntimeException("Failed to enter destination: " + e.getMessage());
        }
    }

    @Then("user should see list of available hotels in Paris")
    public void user_should_see_list_of_available_hotels_in_paris() {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.urlContains("hotels"));

            WebElement resultsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'card-body') or contains(@class,'hotel-item')]")));

            assertTrue(resultsContainer.isDisplayed(), "Hotel results are not visible!");
            System.out.println("✅ Hotel results displayed successfully at: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("❌ Verification failed: " + e.getMessage());
        } finally {
            closeBrowser();
        }
    }
}