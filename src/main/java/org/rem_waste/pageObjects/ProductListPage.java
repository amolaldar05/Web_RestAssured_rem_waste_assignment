package org.rem_waste.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductListPage {

    private final WebDriver driver;
    WebDriverWait wait;

    public ProductListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @FindBy(css = ".card-body h5 b")
    List<WebElement> productNames;


    public void getProductName(String productName) {
        boolean isProductFound = false;
        for (WebElement product : productNames) {
            if (product.getText().equalsIgnoreCase(productName)) {
                isProductFound = true;
                System.out.println("Product found: " + product.getText());
                break;
            }
        }
        if (!isProductFound) {
            System.out.println("Product not found: " + productName);
        }

    }

}
