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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private final String browserName = ConfigReader.get("browser").toLowerCase();

    public static WebDriver getDriver() {
        return driver.get();
    }

    @BeforeClass(alwaysRun = true)
    public void setupDriver() {
        initializeDriver();
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        // Clear cookies and storage before each test method
        getDriver().manage().deleteAllCookies();
       // Navigate to base URL fresh before each test
        getDriver().get(ConfigReader.get("baseUrl"));
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("window.localStorage.clear();");
        js.executeScript("window.sessionStorage.clear();");


    }

    private void initializeDriver() {
        switch (browserName) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                if( ConfigReader.get("headless").equalsIgnoreCase("true")) {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
                    driver.set(new ChromeDriver(options));
                } else {
                    driver.set(new ChromeDriver());
                }
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver.set(new EdgeDriver());
                break;
            case "safari":
                WebDriverManager.safaridriver().setup();
                driver.set(new SafariDriver());
                break;
            default:
                throw new RuntimeException("Unsupported browser: " + browserName);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}
