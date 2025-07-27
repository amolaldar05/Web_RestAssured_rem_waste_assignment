package org.rem_waste.apiTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.rem_waste.BaseComponent.BaseTest;
import org.rem_waste.pageObjects.LoginPage;
import org.rem_waste.pageObjects.ProductListPage;
import org.rem_waste.pojo.AddToCartRequest;
import org.rem_waste.pojo.OrderDetailsResponse;
import org.rem_waste.utils.ConfigReader;
import org.rem_waste.utils.LoggerUtil;
import org.rem_waste.utils.ResourceEnum;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class EndToEndAPITest extends BaseTest {

    private static final Logger log = LoggerUtil.getLogger(EndToEndAPITest.class);

    SoftAssert softAssert;
    String token;
    String userId;
    String productId;
    String orderId;
    String productOrderId;
    LoginPage loginPage;
    ProductListPage productListPage;
    String productName = "Pen";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.get("basePath");
        loginPage = new LoginPage(getDriver());
        productListPage = new ProductListPage(getDriver());
        log.info("Test setup completed. Base URI set to {}", RestAssured.baseURI);
    }

    @Test
    public void getTokenTest() {
        log.info("⏳ Executing getTokenTest...");
        softAssert = new SoftAssert();

        Map<String, Object> map = new HashMap<>();
        map.put("userEmail", ConfigReader.get("userEmail"));
        map.put("userPassword", ConfigReader.get("userPassword"));

        String response = given()
                .header("Content-Type", "application/json")
                .body(map)
                .when()
                .post(ResourceEnum.GET_TOKEN.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        token = jsonPath.getString("token");
        userId = jsonPath.getString("userId");

        log.info(" Token received: {}", token);
        softAssert.assertNotNull(token, "Token is null.");
        softAssert.assertEquals(jsonPath.getString("message"), "Login Successfully", "Login message mismatch");
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "getTokenTest")
    public void getAllProductsTest() {
        log.info("⏳ Executing getAllProductsTest...");
        softAssert = new SoftAssert();

        String body = """
                {
                  "productName": "",
                  "minPrice": null,
                  "maxPrice": null,
                  "productCategory": [],
                  "productSubCategory": [],
                  "productFor": []
                }
                """;

        given().log().all()
                .header("Authorization", token)
                .contentType("application/json")
                .body(body)
                .when()
                .post(ResourceEnum.GET_ALL_PRODUCTS.getResourcePath())
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("all_products_schema.json"));

        log.info(" All products retrieved and validated against schema");
    }

    @Test(dependsOnMethods = "getAllProductsTest")
    public void addProductTest() {
        log.info("⏳ Executing addProductTest...");
        softAssert = new SoftAssert();

        File productImage = new File(System.getProperty("user.dir") + "/src/main/resources/pen.png");

        String response = given().log().all()
                .header("Authorization", token)
                .multiPart("productName", productName)
                .multiPart("productAddedBy", userId)
                .multiPart("productCategory", "fashion")
                .multiPart("productSubCategory", "Writing")
                .multiPart("productPrice", "11500")
                .multiPart("productDescription", "Goods")
                .multiPart("productFor", "women")
                .multiPart("productImage", productImage)
                .when()
                .post(ResourceEnum.ADD_PRODUCT.getResourcePath())
                .then()
                .statusCode(201)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        productId = jsonPath.getString("productId");

        log.info(" Product added successfully with ID: {}", productId);
        softAssert.assertNotNull(productId, "Product ID is null");
        softAssert.assertEquals(jsonPath.getString("message"), "Product Added Successfully");
        softAssert.assertAll();
    }

    @Test(enabled = false)
    public void addProductToCartTest() throws JsonProcessingException {
        log.info(" Executing addProductToCartTest...");

        AddToCartRequest.Product product = new AddToCartRequest.Product();
        product.set_id(productId);
        product.setProductName(productName);
        product.setProductCategory("fashion");
        product.setProductSubCategory("Writing");
        product.setProductPrice(11500);
        product.setProductDescription("Goods");
        product.setProductRating("0");
        product.setProductTotalOrders("0");
        product.setProductStatus(true);
        product.setProductFor("women");
        product.setProductAddedBy(userId);
        product.set__v(0);

        AddToCartRequest cartRequest = new AddToCartRequest();
        cartRequest.set_id(userId);
        cartRequest.setProduct(product);

        log.debug("🛒 Cart Request: {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(cartRequest));

        String response = given().log().all()
                .header("Authorization", token)
                .contentType("application/json")
                .body(cartRequest)
                .when()
                .post(ResourceEnum.ADD_TO_CART.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        softAssert.assertEquals(jsonPath.getString("message"), "Product Added To Cart");
        log.info(" Product added to cart successfully");
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "addProductTest")
    public void createOrderTest() {
        log.info("⏳ Executing createOrderTest...");
        softAssert = new SoftAssert();

        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("country", "India");
        orderDetails.put("productOrderedId", productId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("orders", Collections.singletonList(orderDetails));

        String response = given().log().all()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(ResourceEnum.CREATE_ORDER.getResourcePath())
                .then()
                .statusCode(201)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        softAssert.assertEquals(jsonPath.getString("message"), "Order Placed Successfully");

        List<String> orderIds = jsonPath.getList("orders");
        List<String> productOrderIds = jsonPath.getList("productOrderId");

        this.orderId = orderIds.get(0);
        this.productOrderId = productOrderIds.get(0);

        log.info(" Order created: orderId={}, productOrderId={}", orderId, productOrderId);
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "createOrderTest")
    public void getOrderDetailsTest() {
        log.info("⏳ Executing getOrderDetailsTest...");
        softAssert = new SoftAssert();

        OrderDetailsResponse orderResponse = given().log().all()
                .header("Authorization", token)
                .queryParam("id", orderId)
                .when()
                .get(ResourceEnum.GET_ORDER_DETAILS.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().as(OrderDetailsResponse.class);

        softAssert.assertEquals(orderResponse.getMessage(), "Orders fetched for customer Successfully");

        log.info(" Order Details → Product: {}, Ordered by: {}, Price: {}",
                orderResponse.getData().getProductName(),
                orderResponse.getData().getOrderBy(),
                orderResponse.getData().getOrderPrice());

        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "createOrderTest")
    public void checkAddedProductONUITest() {
        log.info(" Executing checkAddedProductONUITest...");
        softAssert = new SoftAssert();

        loginPage.enterUsername(ConfigReader.get("userEmail"));
        loginPage.enterPassword(ConfigReader.get("userPassword"));
        loginPage.clickLoginButton();

        String successMsg = loginPage.getSuccessMsg();
        softAssert.assertEquals(successMsg, "Login Successfully", "Login message mismatch");

        productListPage.getProductName(productName);

        log.info(" Verified on UI: Product '{}' is displayed after API add", productName);
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "checkAddedProductONUITest")
    public void deleteProduct() {
        log.info("⏳ Executing deleteProduct...");
        softAssert = new SoftAssert();

        String response = given().log().all()
                .header("Authorization", token)
                .pathParam("productId", productId)
                .when()
                .delete(ResourceEnum.DELETE_PRODUCT.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        softAssert.assertEquals(jsonPath.getString("message"), "Product Deleted Successfully");

        log.info("🗑️ Product deleted successfully. ID: {}", productId);
        softAssert.assertAll();
    }
}
