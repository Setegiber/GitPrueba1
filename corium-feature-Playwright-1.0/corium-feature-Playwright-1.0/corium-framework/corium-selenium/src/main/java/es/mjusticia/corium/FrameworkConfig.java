package es.mjusticia.corium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class FrameworkConfig extends ApiMethods {

    /**
     * Sets up the Selenium WebDriver environment before the test class execution.
     * Invokes the {@code setupSelenium()} method.
     */
    @BeforeClass(alwaysRun = true)
    public void setUpConfig() {
        if (getSeleniumBrowserDriverProperty().contains("firefox")){
            killGeckoDriver();
        }
        if (getSeleniumStartProperty().equalsIgnoreCase("false")) {
            loggerSlf4jInfo("selenium.start = false, if you want to setup Selenium " +
                    "first change the property to true");
            return;
        }
        if (getSeleniumClearCacheProperty().equalsIgnoreCase("true")) {
            loggerSlf4jInfo("Selenium, clearing cache...");
            WebDriverManager.firefoxdriver().clearDriverCache().clearResolutionCache();
            WebDriverManager.edgedriver().clearDriverCache().clearResolutionCache();
            WebDriverManager.chromedriver().clearDriverCache().clearResolutionCache();
        }
        setupSelenium();
    }

    /**
     * Cleans up the Selenium WebDriver environment after the test class execution.
     * Invokes the {@code tearDown()} method.
     */
    @AfterClass(alwaysRun = true)
    public void tearDownConfig(){
        tearDown();
    }

}
