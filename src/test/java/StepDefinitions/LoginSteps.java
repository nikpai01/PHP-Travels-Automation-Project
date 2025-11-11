package StepDefinitions;

import com.base.BaseTest;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginSteps extends BaseTest {
    WebDriver driver;
    WebDriverWait wait;

    @Given("user is on PHPTravels login page")
    public void openLoginPage() {
        openBrowser();
        driver = getDriver();
        driver.get("https://phptravels.net/login");
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ✅ Common helper to enter credentials
    private void enterCredentials(String email, String password) {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    @When("user enters valid credentials")
    public void enterValidCredentials() {
        enterCredentials("user@phptravels.com", "demouser");
        System.out.println("✅ Valid credentials entered successfully");
    }

    @When("user enters invalid credentials")
    public void enterInvalidCredentials() {
        enterCredentials("wronguser@phptravels.com", "wrongpass");
        System.out.println("❌ Invalid credentials entered");
    }

    @And("clicks login")
    public void clickLogin() {
        try {
            // Scroll down to reveal the login button
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
            System.out.println("Scrolled down 500px");

            // Wait after scrolling
            Thread.sleep(1500);

            // Print page source for debugging (first 2000 characters)
            String pageSource = driver.getPageSource();
            System.out.println("=== PAGE SOURCE SNIPPET ===");
            System.out.println(pageSource.substring(0, Math.min(2000, pageSource.length())));
            System.out.println("=== END SNIPPET ===");

            // Find all buttons on the page
            var allButtons = driver.findElements(By.tagName("button"));
            System.out.println("\n=== FOUND " + allButtons.size() + " BUTTONS ===");
            for (int i = 0; i < allButtons.size(); i++) {
                WebElement btn = allButtons.get(i);
                System.out.println("Button " + i + ":");
                System.out.println(" Text: '" + btn.getText() + "'");
                System.out.println(" Type: '" + btn.getAttribute("type") + "'");
                System.out.println(" Class: '" + btn.getAttribute("class") + "'");
                System.out.println(" ID: '" + btn.getAttribute("id") + "'");
                System.out.println(" Displayed: " + btn.isDisplayed());
                System.out.println(" Enabled: " + btn.isEnabled());
                System.out.println("---");
            }

            // Try multiple strategies to find and click the login button
            WebElement loginButton = null;

            // Strategy 1: Find submit button
            try {
                loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
                System.out.println("Found button using: button[type='submit']");
            } catch (Exception e1) {
                System.out.println("Strategy 1 failed: " + e1.getMessage());

                // Strategy 2: Find by text using normalize-space
                try {
                    loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']"));
                    System.out.println("Found button using: normalize-space xpath");
                } catch (Exception e2) {
                    System.out.println("Strategy 2 failed: " + e2.getMessage());

                    // Strategy 3: Find any button in form
                    try {
                        loginButton = driver.findElement(By.xpath("//form//button"));
                        System.out.println("Found button using: //form//button");
                    } catch (Exception e3) {
                        System.out.println("Strategy 3 failed: " + e3.getMessage());

                        // Strategy 4: Get the first visible button
                        for (WebElement btn : allButtons) {
                            if (btn.isDisplayed() && btn.isEnabled()) {
                                loginButton = btn;
                                System.out.println("Using first visible/enabled button");
                                break;
                            }
                        }
                    }
                }
            }

            if (loginButton == null) {
                throw new NoSuchElementException("Could not find login button with any strategy!");
            }

            // Scroll button into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    loginButton);
            Thread.sleep(1000);

            // Try to click
            try {
                System.out.println("Attempting regular click...");
                loginButton.click();
                System.out.println("Regular click successful!");
            } catch (Exception e) {
                System.out.println("Regular click failed: " + e.getMessage());
                System.out.println("Trying JavaScript click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);
                System.out.println("JavaScript click executed!");
            }

            // Wait for navigation
            Thread.sleep(3000);
            System.out.println("Current URL after click: " + driver.getCurrentUrl());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("user should see dashboard page")
    public void verifyDashboard() {
        wait.until(ExpectedConditions.titleContains("Dashboard"));
        String title = driver.getTitle().toLowerCase();
        if (!title.contains("dashboard")) {
            throw new AssertionError("Dashboard not loaded! Actual title: " + title);
        }
        System.out.println("✅ Dashboard verification successful!");
        closeBrowser();
    }

    @Then("user should see an error alert message")
    public void verifyErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait until the <h4> element containing error text becomes visible
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(text(),'Invalid') or contains(text(),'Wrong') or contains(text(),'incorrect')]")
            ));

            String text = errorMsg.getText();
            System.out.println("Error message displayed: " + text);

            // Basic validation
            if (text.toLowerCase().contains("invalid") || text.toLowerCase().contains("wrong")) {
                System.out.println("❌ Invalid login verified successfully.");
            } else {
                throw new AssertionError("Error message text mismatch: " + text);
            }
        } catch (TimeoutException e) {
            throw new AssertionError("Error message not found within timeout — possible locator issue.");
        } finally {
            closeBrowser();
        }
    }

}
