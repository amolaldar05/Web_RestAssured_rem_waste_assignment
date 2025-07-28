package org.rem_waste.pageObjects;

import org.openqa.selenium.By; // Added for invisibilityOfElementLocated
import org.openqa.selenium.JavascriptExecutor; // Added for JavaScript execution
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.rem_waste.utils.LoggerUtil;
import org.slf4j.Logger;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger log = LoggerUtil.getLogger(LoginPage.class);

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        // Increased WebDriverWait timeout for better stability in CI environments
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased from 5 to 15 seconds
    }

    @FindBy(id = "userEmail") private WebElement username;
    @FindBy(id = "userPassword") private WebElement password;
    @FindBy(css = "input[type='Submit']") private WebElement loginButton;
    @FindBy(css = "div[aria-label='Login Successfully']") private WebElement loginSuccessMsg;
    // Using By.xpath directly for invisibilityOfElementLocated as @FindBy elements might not be in DOM yet
    @FindBy(xpath = "//div[@id='toast-container']//div[contains(@class, 'toast-message') or contains(@class, 'toast-title')]") private WebElement toastMsg;
    @FindBy(css = "div.invalid-feedback > div") private WebElement fieldErrorMsg;
    @FindBy(css = "div[aria-label='Incorrect email or password.']") private WebElement loginErrorMessage;

    public void enterUsername(String user) {
        wait.until(ExpectedConditions.visibilityOf(username));
        if (!user.isEmpty()) username.clear();
        username.sendKeys(user);
        log.info("Entered username: {}", user);
    }

    public void enterPassword(String pass) {
        wait.until(ExpectedConditions.visibilityOf(password));
        if (!pass.isEmpty()) password.clear();
        password.sendKeys(pass);
        log.info("Entered password");
    }

    /**
     * Clicks the login button after waiting for it to be clickable,
     * ensuring potential intercepting elements are gone, and scrolling it into view.
     * Includes a JavaScript click fallback for increased robustness in CI environments.
     */
    public void clickLoginButton() {
        try {
            // 1. Wait for the element to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            log.debug("Login button is clickable.");

            // 2. Wait for potential intercepting elements to disappear
            // These are common toast messages or validation errors that might briefly cover elements.
            // Using By.locator for invisibilityOfElementLocated as it works even if element is not yet in DOM
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='toast-container']")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.invalid-feedback > div")));
            log.debug("Waited for potential interceptors (toast/field error) to disappear.");

            // 3. Scroll the element into view using JavaScript
            // This helps prevent issues where elements might be technically clickable but not fully in the viewport
            // or obscured by subtle rendering differences in headless environments.
            scrollToElement(loginButton);
            log.debug("Scrolled to login button.");

            // 4. Attempt the standard Selenium click
            log.info("Attempting standard click on Login button.");
            loginButton.click();
            log.info("Clicked on Login button successfully.");

        } catch (Exception e) {
            // If the standard click fails (e.g., ElementClickInterceptedException),
            // log the warning and attempt a JavaScript click as a fallback.
            log.warn("Standard click failed for login button ({}). Error: {}. Attempting JavaScript click as fallback.", loginButton.toString(), e.getMessage());
            try {
                performJavaScriptClick(loginButton);
                log.info("Clicked on Login button via JavaScript fallback successfully.");
            } catch (Exception jsClickException) {
                // If even the JavaScript click fails, log a severe error and re-throw.
                log.error("JavaScript click also failed for login button. Error: {}. This indicates a persistent issue.", jsClickException.getMessage());
                // Re-throw the original exception for proper test failure reporting
                throw new RuntimeException("Failed to click login button after multiple attempts (standard and JavaScript).", e);
            }
        }
    }

    /**
     * Helper method to scroll a WebElement into view using JavaScript.
     * This ensures the element is fully visible within the viewport.
     * @param element The WebElement to scroll into view.
     */
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        // Using 'block: "center", inline: "center"' tries to center the element in the viewport.
        // You can also use 'true' for a simpler scroll to top: "arguments[0].scrollIntoView(true);"
    }

    /**
     * Helper method to perform a click using JavaScript.
     * This is a fallback for when standard Selenium click methods fail due to interception.
     * @param element The WebElement to click.
     */
    private void performJavaScriptClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public String getToastMsg() {
        wait.until(ExpectedConditions.visibilityOf(toastMsg));
        String msg = toastMsg.getText();
        log.info("Toast Message: {}", msg);
        return msg;
    }

    public String getSuccessMsg() {
        wait.until(ExpectedConditions.visibilityOf(loginSuccessMsg));
        String msg = loginSuccessMsg.getText();
        log.info("Login Success Message: {}", msg);
        return msg;
    }

    public String getFieldErrorMsg() {
        wait.until(ExpectedConditions.visibilityOf(fieldErrorMsg));
        String msg = fieldErrorMsg.getText();
        log.info("Field Error Message: {}", msg);
        return msg;
    }

    public String getErrorMsg() {
        wait.until(ExpectedConditions.visibilityOf(loginErrorMessage));
        String msg = loginErrorMessage.getText();
        log.info("Login Error Message: {}", msg);
        return msg;
    }
}