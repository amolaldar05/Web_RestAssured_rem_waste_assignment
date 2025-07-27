package org.rem_waste.uiTests;

import org.rem_waste.BaseComponent.BaseTest;
import org.rem_waste.pageObjects.LoginPage;
import org.rem_waste.utils.LoggerUtil;  // Assuming you have this utility class
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.slf4j.Logger;

import java.util.Map;

public class LoginTests extends BaseTest {
    LoginPage loginPage;
    SoftAssert softAssert;
    private static final Logger log = LoggerUtil.getLogger(LoginTests.class);

    @BeforeMethod
    public void setUp(){
        log.info("Initializing LoginPage and SoftAssert instances");
        loginPage = new LoginPage(getDriver());
        softAssert = new SoftAssert();
    }

    @Test(priority = 3, dataProvider="loginData", dataProviderClass = org.rem_waste.utils.LoginDataProvider.class)
    public void validLoginTest(Map<String, String> testData) {
        log.info("Starting validLoginTest with username: {}", testData.get("username"));

        loginPage.enterUsername(testData.get("username"));
        loginPage.enterPassword(testData.get("password"));
        loginPage.clickLoginButton();

        String toastMsg = loginPage.getToastMsg();
        log.info("Received toast message: {}", toastMsg);

        softAssert.assertEquals(toastMsg.trim(), testData.get("toastMsg"));
        softAssert.assertAll();

        log.info("validLoginTest completed");
    }

    @Test(enabled = false)
    public void invalidLoginTest() {
        log.info("Starting invalidLoginTest with hardcoded credentials");

        loginPage.enterUsername("jivan@gmail.com");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getErrorMsg();
        log.info("Received error message: {}", errorMsg);

        softAssert.assertEquals(errorMsg.trim(), "Incorrect email or password.");
        softAssert.assertAll();

        log.info("invalidLoginTest completed");
    }

    @Test(priority = 1)
    public void invalidLoginWithEmptyUsernameTest() {
        log.info("Starting invalidLoginWithEmptyUsernameTest");

        loginPage.enterUsername("");
        loginPage.enterPassword("Jivan@9900");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        log.info("Received field error message: {}", errorMsg);

        softAssert.assertEquals(errorMsg.trim(), "*Email is required");
        softAssert.assertAll();

        log.info("invalidLoginWithEmptyUsernameTest completed");
    }

    @Test(priority = 2)
    public void invalidLoginWithEmptyPasswordTest() {
        log.info("Starting invalidLoginWithEmptyPasswordTest");

        loginPage.enterUsername("jivan@gmail.com");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        log.info("Received field error message: {}", errorMsg);

        softAssert.assertEquals(errorMsg.trim(), "*Password is required");
        softAssert.assertAll();

        log.info("invalidLoginWithEmptyPasswordTest completed");
    }

    @Test(priority = 0)
    public void emptyLoginTest() {
        log.info("Starting emptyLoginTest");

        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        log.info("Received field error message: {}", errorMsg);

        softAssert.assertEquals(errorMsg.trim(), "*Email is required");
        softAssert.assertAll();

        log.info("emptyLoginTest completed");
    }
}
