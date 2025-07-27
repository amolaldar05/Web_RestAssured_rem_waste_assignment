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
import java.util.List;

public class ProductListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger log = LoggerUtil.getLogger(ProductListPage.class);

    public ProductListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @FindBy(css = ".card-body h5 b")
    private List<WebElement> productNames;

    public void getProductName(String productName) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
            boolean isProductFound = false;

            for (WebElement product : productNames) {
                if (product.getText().equalsIgnoreCase(productName)) {
                    isProductFound = true;
                    log.info(" Product found: {}", product.getText());
                    break;
                }
            }

            if (!isProductFound) {
                log.warn(" Product not found: {}", productName);
            }

        } catch (Exception e) {
            log.error("Exception while searching for product '{}': {}", productName, e.getMessage(), e);
        }
    }
}
