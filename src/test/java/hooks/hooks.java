package hooks;

import com.base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class hooks extends BaseTest {
    @Before
    public void beforeScenario() {
        System.out.println("=== Starting Scenario ===");
    }

    @After
    public void afterScenario() {
        closeBrowser();
        System.out.println("=== Scenario Finished ===");
    }
}
