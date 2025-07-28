package org.rem_waste.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.rem_waste.utils.ConfigReader;
import org.rem_waste.utils.LoggerUtil;
import org.slf4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private final String browserName = ConfigReader.get("browser").toLowerCase();
    private static final Logger log = LoggerUtil.getLogger(BaseTest.class);

    public static WebDriver getDriver() {
        return driver.get();
    }

    @BeforeClass(alwaysRun = true)
    public void setupDriver() {
        log.info("Initializing WebDriver for browser: {}", browserName);
        initializeDriver();

        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        log.info("WebDriver setup complete with implicit wait and maximized window");
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("Preparing browser for new test - clearing cookies and local/session storage");
        getDriver().manage().deleteAllCookies();

        String baseUrl = ConfigReader.get("baseUrl");
        getDriver().get(baseUrl);
        log.info("Navigated to base URL: {}", baseUrl);

        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.sessionStorage.clear();");
        log.info("Cleared browser local and session storage");
    }

    private void initializeDriver() {
        switch (browserName) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                if (ConfigReader.get("headless").equalsIgnoreCase("true")) {
                    log.info("Running Chrome in headless mode");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080","start-maximized");
                    driver.set(new ChromeDriver(options));
                } else {
                    log.info("Running Chrome in normal mode");
                    driver.set(new ChromeDriver());
                }
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                log.info("Running Firefox browser");
                driver.set(new FirefoxDriver());
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                log.info("Running Edge browser");
                driver.set(new EdgeDriver());
                break;

            case "safari":
                WebDriverManager.safaridriver().setup();
                log.info("Running Safari browser");
                driver.set(new SafariDriver());
                break;

            default:
                log.error("Unsupported browser specified in config: {}", browserName);
                throw new RuntimeException("Unsupported browser: " + browserName);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        log.info("Tearing down WebDriver instance");
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
            log.info("WebDriver successfully closed and removed from ThreadLocal");
        }
    }
}
