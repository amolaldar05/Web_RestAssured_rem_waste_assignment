package org.rem_waste.uiTests;

import org.rem_waste.BaseComponent.BaseTest;
import org.rem_waste.pageObjects.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class LoginTests extends BaseTest {
    LoginPage loginPage;
    SoftAssert softAssert;

    @BeforeMethod
    public void setUp(){
        // Only initialize page objects and SoftAssert here
        loginPage = new LoginPage(getDriver());
        softAssert = new SoftAssert();
    }

    @Test(priority = 3, dataProvider="loginData", dataProviderClass = org.rem_waste.utils.LoginDataProvider.class)
    public void validLoginTest(Map<String, String> testData) {
        loginPage.enterUsername(testData.get("username"));
        loginPage.enterPassword(testData.get("password"));
        loginPage.clickLoginButton();

        String toastMsg = loginPage.getToastMsg();
        softAssert.assertEquals(toastMsg.trim(),testData.get("toastMsg"));
        softAssert.assertAll();
    }

    @Test(enabled = false)
    public void invalidLoginTest() {
        loginPage.enterUsername("jivan@gmail.com");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getErrorMsg();
        softAssert.assertEquals(errorMsg.trim(), "Incorrect email or password.");
        softAssert.assertAll();
    }

    @Test(priority = 1)
    public void invalidLoginWithEmptyUsernameTest() {
        loginPage.enterUsername("");
        loginPage.enterPassword("Jivan@9900");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        softAssert.assertEquals(errorMsg.trim(), "*Email is required");
        softAssert.assertAll();
    }

    @Test(priority = 2)
    public void invalidLoginWithEmptyPasswordTest() {
        loginPage.enterUsername("jivan@gmail.com");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        softAssert.assertEquals(errorMsg.trim(), "*Password is required");
        softAssert.assertAll();
    }

    @Test(priority = 0)
    public void emptyLoginTest() {
        loginPage.enterUsername("");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getFieldErrorMsg();
        softAssert.assertEquals(errorMsg.trim(), "*Email is required");
        softAssert.assertAll();
    }
}
