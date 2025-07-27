package org.rem_waste.pageObjects;

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @FindBy(id = "userEmail") private WebElement username;
    @FindBy(id = "userPassword") private WebElement password;
    @FindBy(css = "input[type='Submit']") private WebElement loginButton;
    @FindBy(css = "div[aria-label='Login Successfully']") private WebElement loginSuccessMsg;
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

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        log.info("Clicked on Login button");
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
