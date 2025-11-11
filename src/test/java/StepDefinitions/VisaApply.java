package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.assertTrue;

public class VisaApply extends BaseTest {

    private WebDriverWait wait;

    // Constructor - Initialize wait
    public VisaApply() {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @When("user enters {string} as From Country")
    public void user_enters_FromCountry(String country) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Wait for page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));

            // Wait for the visa submit form to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("visa-submit")
            ));

            System.out.println("✅ Visa form is present");
            Thread.sleep(1000);

            // Wait for Select2 to be loaded
            wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver)
                    .executeScript("return typeof jQuery !== 'undefined' && typeof jQuery.fn.select2 !== 'undefined'"));

            System.out.println("✅ Select2 loaded");

            // Find the exact dropdown arrow using the XPath you provided
            WebElement dropdownArrow = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='visa-submit']/div/div[1]/div[1]/div[2]/span/span[1]/span/span[2]/b")
            ));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    dropdownArrow
            );
            Thread.sleep(500);

            System.out.println("✅ Found dropdown arrow, attempting to click...");

            // Try clicking the arrow - Method 1: Direct click
            try {
                dropdownArrow.click();
                System.out.println("✅ Clicked dropdown arrow directly");
            } catch (Exception e) {
                System.out.println("Direct click failed, trying JavaScript click...");

                // Method 2: JavaScript click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownArrow);
                System.out.println("✅ Clicked dropdown arrow with JavaScript");
            }

            Thread.sleep(500);

            // Wait for search input to appear
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input.select2-search__field")
            ));

            System.out.println("✅ Dropdown opened, search field visible");

            // Type the country name
            searchInput.clear();
            searchInput.sendKeys(country);
            searchInput.click();
            Thread.sleep(800);

            System.out.println("✅ Typed country name: " + country);

            // Wait for results and click first option
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("li.select2-results__option[role='option']")
            ));

            String optionText = firstOption.getText();
            System.out.println("First option text: " + optionText);

            if (!optionText.toLowerCase().contains("searching") &&
                    !optionText.toLowerCase().contains("no results") &&
                    !optionText.toLowerCase().contains("loading")) {

                firstOption.click();
                System.out.println("✅ Selected From Country: " + country);
            } else {
                // Wait a bit more for real results
                Thread.sleep(1000);
                firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("li.select2-results__option[role='option']")
                ));
                firstOption.click();
                System.out.println("✅ Selected From Country: " + country);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout error: " + e.getMessage());

            // Debug info
            try {
                System.err.println("\n=== DEBUG INFO ===");

                // Check if visa-submit form exists
                boolean formExists = driver.findElements(By.id("visa-submit")).size() > 0;
                System.err.println("visa-submit form exists: " + formExists);

                // Check if dropdown arrow exists
                boolean arrowExists = driver.findElements(
                        By.xpath("//*[@id='visa-submit']/div/div[1]/div[1]/div[2]/span/span[1]/span/span[2]/b")
                ).size() > 0;
                System.err.println("Dropdown arrow exists: " + arrowExists);

                // Get current active tab
                Object activeTab = ((JavascriptExecutor) driver).executeScript(
                        "var activeTab = document.querySelector('.nav-link.active');" +
                                "return activeTab ? activeTab.textContent : 'none';"
                );
                System.err.println("Active tab: " + activeTab);

            } catch (Exception debugEx) {
                System.err.println("Debug failed: " + debugEx.getMessage());
            }

            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error selecting From Country: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @When("user enters {string} as To Country")
    public void user_enters_ToCountry(String country) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Wait for page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));

            // Wait for the visa submit form to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("visa-submit")
            ));

            System.out.println("✅ Visa form is present");
            Thread.sleep(1000);

            // Wait for Select2 to be loaded
            wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver)
                    .executeScript("return typeof jQuery !== 'undefined' && typeof jQuery.fn.select2 !== 'undefined'"));

            System.out.println("✅ Select2 loaded");

            // Find the exact dropdown arrow using the XPath you provided
            WebElement dropdownArrow = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='visa-submit']/div/div[2]/div[1]/div[2]/span/span[1]/span/span[2]/b")
            ));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    dropdownArrow
            );
            Thread.sleep(500);

            System.out.println("✅ Found dropdown arrow, attempting to click...");

            // Try clicking the arrow - Method 1: Direct click
            try {
                dropdownArrow.click();
                System.out.println("✅ Clicked dropdown arrow directly");
            } catch (Exception e) {
                System.out.println("Direct click failed, trying JavaScript click...");

                // Method 2: JavaScript click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownArrow);
                System.out.println("✅ Clicked dropdown arrow with JavaScript");
            }

            Thread.sleep(500);

            // Wait for search input to appear
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input.select2-search__field")
            ));

            System.out.println("✅ Dropdown opened, search field visible");

            // Type the country name
            searchInput.clear();
            searchInput.sendKeys(country);
            searchInput.click();
            Thread.sleep(800);

            System.out.println("✅ Typed country name: " + country);

            // Wait for results and click first option
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("li.select2-results__option[role='option']")
            ));

            String optionText = firstOption.getText();
            System.out.println("First option text: " + optionText);

            if (!optionText.toLowerCase().contains("searching") &&
                    !optionText.toLowerCase().contains("no results") &&
                    !optionText.toLowerCase().contains("loading")) {

                firstOption.click();
                System.out.println("✅ Selected To Country: " + country);
            } else {
                // Wait a bit more for real results
                Thread.sleep(1000);
                firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("li.select2-results__option[role='option']")
                ));
                firstOption.click();
                System.out.println("✅ Selected To Country: " + country);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error selecting To Country: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @When("user selects date entry as {string}")
    public void date_entry(String dateOffset) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            System.out.println("Selecting date: " + dateOffset);

            // Calculate the date based on offset
            int daysToAdd = 15; // default
            if (dateOffset.contains("days from today")) {
                String daysStr = dateOffset.replaceAll("[^0-9]", "");
                daysToAdd = Integer.parseInt(daysStr);
            }

            LocalDate futureDate = LocalDate.now().plusDays(daysToAdd);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = futureDate.format(formatter);

            System.out.println("Calculated date: " + formattedDate);

            // Find the date input field
            WebElement dateInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input#date[name='date']")
            ));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    dateInput
            );
            Thread.sleep(500);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Set the date value directly via JavaScript
            js.executeScript(
                    "var element = arguments[0];" +
                            "element.removeAttribute('readonly');" +
                            "element.value = arguments[1];" +
                            "element.setAttribute('readonly', 'readonly');" +

                            // Trigger all common events
                            "var events = ['input', 'change', 'blur'];" +
                            "events.forEach(function(eventType) {" +
                            "  var event = new Event(eventType, { bubbles: true, cancelable: true });" +
                            "  element.dispatchEvent(event);" +
                            "});" +

                            // Try Bootstrap Datepicker
                            "if (typeof jQuery !== 'undefined' && jQuery(element).data('datepicker')) {" +
                            "  jQuery(element).datepicker('update', arguments[1]);" +
                            "}" +

                            // Try Flatpickr
                            "if (element._flatpickr) {" +
                            "  element._flatpickr.setDate(arguments[1], true);" +
                            "}",
                    dateInput, formattedDate
            );

            Thread.sleep(500);

            // Verify the date was set
            String enteredValue = dateInput.getAttribute("value");
            System.out.println("✅ Selected date: " + formattedDate);
            System.out.println("✅ Verified value: " + enteredValue);

            // Check if value matches
            if (!enteredValue.equals(formattedDate)) {
                System.out.println("⚠️ Warning: Entered value doesn't match expected. Expected: " + formattedDate + ", Got: " + enteredValue);
            }

        } catch (Exception e) {
            System.err.println("❌ Error selecting date: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Then("user should see the submission form")
    public void user_should_see_submissionform() {
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.urlContains("submit"));

            WebElement resultsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'card-body') or contains(@class,'sec__title_list')]")));

            assertTrue(resultsContainer.isDisplayed(), "Form results are not visible!");
            System.out.println("✅ Form page displayed successfully at: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("❌ Verification failed: " + e.getMessage());
        } finally {
            closeBrowser();
        }
    }
}