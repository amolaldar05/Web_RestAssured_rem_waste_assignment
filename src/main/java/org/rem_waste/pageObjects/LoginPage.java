package org.rem_waste.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @FindBy(id = "userEmail")
    private WebElement username;

    @FindBy(id = "userPassword")
    private WebElement password;

    @FindBy(css = "input[type='Submit']")
    private WebElement loginButton;

    @FindBy(css = "div[aria-label='Login Successfully']")
    private WebElement loginSuccessMsg;


    @FindBy(xpath = "//div[@id='toast-container']//div[contains(@class, 'toast-error') or contains(@class, 'toast-success')]//div[contains(@class, 'toast-message') or contains(@class, 'toast-title')]\n")
    private WebElement toastMsg;

    @FindBy(css = "div.invalid-feedback > div")
    private WebElement fieldErrorMsg;

    @FindBy(css = "div[aria-label='Incorrect email or password.']")
    private WebElement loginErrorMessage;


    public void enterUsername(String user) {
        if(!user.isEmpty()){
            username.clear();
        }
        username.sendKeys(user);
    }

    public void enterPassword(String pass) {
    if(!pass.isEmpty()){
            password.clear();
        }
        password.sendKeys(pass);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public String getToastMsg() {
        try {
            wait.until(ExpectedConditions.visibilityOf(toastMsg));
            return toastMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get success message: " + e.getMessage(), e);
        }
    }
    public String getSuccessMsg() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginSuccessMsg));
            return loginSuccessMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get success message: " + e.getMessage(), e);
        }
    }

    public String getFieldErrorMsg() {
        try {
            wait.until(ExpectedConditions.visibilityOf(fieldErrorMsg));
            return fieldErrorMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get password field error message: " + e.getMessage(), e);
        }
    }
    public String getErrorMsg(){
        try {
            wait.until(ExpectedConditions.visibilityOf(loginErrorMessage));
            return loginErrorMessage.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get error message: " + e.getMessage(), e);
        }
    }
}