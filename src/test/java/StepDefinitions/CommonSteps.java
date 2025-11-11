package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.testng.Assert.assertTrue;

public class CommonSteps extends BaseTest {

    private WebDriverWait wait;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Given("user is on PHPTravels home page")
    public void user_is_on_PHPTravels_home_page() {
        openBrowser();
        driver = getDriver();
        driver.manage().window().maximize();
        driver.get("https://phptravels.net/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        System.out.println("✅ Navigated to PHPTravels home page");
    }

    @When("user selects {string} tab")
    public void user_selects_tab(String tabName) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class,'nav-link') and contains(.,'" + tabName + "')]")));
            tab.click();
            System.out.println("✅ Selected tab: " + tabName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to click tab: " + e.getMessage());
        }
    }

    @When("user selects check-in date as {string}")
    public void user_selects_check_in_date_as(String offsetText) {
        selectDate(offsetText, true);
    }

    @When("user selects check-out date as {string}")
    public void user_selects_check_out_date_as(String offsetText) {
        selectDate(offsetText, false);
    }

    private void selectDate(String offsetText, boolean isCheckIn) {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            int daysFromToday = Integer.parseInt(offsetText.replaceAll("\\D+", ""));
            LocalDate targetDate = LocalDate.now().plusDays(daysFromToday);
            String formattedDate = targetDate.format(formatter);

            String fieldId = isCheckIn ? "checkin" : "checkout";
            WebElement dateField = wait.until(ExpectedConditions.elementToBeClickable(By.id(fieldId)));

            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];",
                    dateField, formattedDate);

            System.out.println("✅ Selected " + (isCheckIn ? "checkin" : "checkout") + ": " + formattedDate);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to select date: " + e.getMessage());
        }
    }

    @When("user clicks search button")
    public void user_clicks_search_button() {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement searchBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#hotels-search > div > div.col-lg-1 > button")));

            System.out.println("✅ Found search button");
            Thread.sleep(1000);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});",
                    searchBtn);
            Thread.sleep(500);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.pointerEvents = 'auto';" +
                            "arguments[0].disabled = false;",
                    searchBtn);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBtn);
            System.out.println("✅ Successfully clicked search button");
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("❌ Error clicking search button: " + e.getMessage());
            throw new RuntimeException("Failed to click search button: " + e.getMessage());
        }
    }
}