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
import org.rem_waste.utils.ResourceEnum;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class EndToEndAPITest extends BaseTest {

    SoftAssert softAssert;
    String token;
    String userId;
    String productId;
    String orderId;
    String productOrderId;
    LoginPage loginPage;
    ProductListPage productListPage;
    String productName= "Pen";
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.get("basePath");
        loginPage= new LoginPage(getDriver());
        productListPage = new ProductListPage(getDriver());
    }

    @Test
    public void getTokenTest() {
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
        userId = jsonPath.get("userId");

        softAssert.assertNotNull(token, "Token is null.");
        softAssert.assertEquals(jsonPath.getString("message"), "Login Successfully", "Login message mismatch");
        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "getTokenTest")
    public void getAllProductsTest() {
        softAssert = new SoftAssert();
        String body="{\n" +
                "\"productName\": \"\",\n" +
                "\"minPrice\": null,\n" +
                "\"maxPrice\": null,\n" +
                "\"productCategory\": [],\n" +
                "\"productSubCategory\": [],\n" +
                "\"productFor\": []\n" +
                "}";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("productName", "");
        requestBody.put("minPrice", null);
        requestBody.put("maxPrice", null);
        requestBody.put("productCategory", new ArrayList<>());
        requestBody.put("productSubCategory", new ArrayList<>());
        requestBody.put("productFor", new ArrayList<>());

        given().log().all()
                .header("Authorization",  token)
                .contentType("application/json")
                .body(body)
                .when()
                .post(ResourceEnum.GET_ALL_PRODUCTS.getResourcePath())
                .then()
                .log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("all_products_schema.json"));
    }


    @Test(dependsOnMethods = "getAllProductsTest")
    public void addProductTest() {
        softAssert = new SoftAssert();

        // Multipart (for image upload) needs special handling
        File productImage = new File(System.getProperty("user.dir") + "/src/main/resources/pen.png");

        String response = given().log().all()
                .header("Authorization",token)
                .multiPart("productName", "Pen")
                .multiPart("productAddedBy", userId)
                .multiPart("productCategory", "fashion")
                .multiPart("productSubCategory", "Writing")
                .multiPart("productPrice", "11500")
                .multiPart("productDescription", "Goods")
                .multiPart("productFor", "women")  // match casing with cart
                .multiPart("productImage", productImage)
                .when()
                .post(ResourceEnum.ADD_PRODUCT.getResourcePath())
                .then()
                .statusCode(201)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        productId = jsonPath.getString("productId");

        softAssert.assertNotNull(productId, "Product ID is null");
        softAssert.assertEquals(jsonPath.getString("message"), "Product Added Successfully");
        softAssert.assertAll();
    }

    @Test(enabled = false)
    public void addProductToCartTest() throws JsonProcessingException {
        RestAssured.baseURI = "https://rahulshettyacademy.com";

        AddToCartRequest.Product product = new AddToCartRequest.Product();
        product.set_id(productId); // ← set from response of addProduct()
        product.setProductName(productName);
        product.setProductCategory("fashion");
        product.setProductSubCategory("Writing");
        product.setProductPrice(11500);
        product.setProductDescription("Goods");
        product.setProductRating("0");
        product.setProductTotalOrders("0");
        product.setProductStatus(true);
        product.setProductFor("women");
        product.setProductAddedBy(userId); // ← logged-in user's ID
        product.set__v(0);

        AddToCartRequest cartRequest = new AddToCartRequest();
        cartRequest.set_id(userId); // ← logged-in user's ID
        cartRequest.setProduct(product);

        // ✅ Print request body to debug
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(cartRequest));

        String response = given().log().all()
                .header("Authorization", token)
                .contentType("application/json")
                .body(cartRequest)
                .when()
                .post(ResourceEnum.ADD_TO_CART.getResourcePath())
                .then()
                .statusCode(200) // or 201 based on API
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        softAssert.assertEquals(jsonPath.getString("message"), "Product Added To Cart");
        softAssert.assertAll();
    }



    @Test(dependsOnMethods = "addProductTest")
    public void createOrderTest() {
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

        Assert.assertNotNull(orderIds);
        Assert.assertFalse(orderIds.isEmpty());
        Assert.assertNotNull(productOrderIds);
        Assert.assertFalse(productOrderIds.isEmpty());

        this.orderId = orderIds.get(0);
        this.productOrderId = productOrderIds.get(0);

        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "createOrderTest")
    public void getOrderDetailsTest() {
        softAssert = new SoftAssert();

        OrderDetailsResponse orderResponse = given().log().all()
                .header("Authorization",  token)
                .queryParam("id", orderId)
                .when()
                .get(ResourceEnum.GET_ORDER_DETAILS.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().as(OrderDetailsResponse.class);

        softAssert.assertEquals(orderResponse.getMessage(), "Orders fetched for customer Successfully");

        System.out.println("Product: " + orderResponse.getData().getProductName());
        System.out.println("Ordered by: " + orderResponse.getData().getOrderBy());
        System.out.println("Price: " + orderResponse.getData().getOrderPrice());

        softAssert.assertAll();
    }

    @Test(dependsOnMethods = "createOrderTest")
    public void checkAddedProductONUITest(){
        loginPage.enterUsername(ConfigReader.get("userEmail"));
        loginPage.enterPassword(ConfigReader.get("userPassword"));
        loginPage.clickLoginButton();
        String successMsg=loginPage.getSuccessMsg();
        softAssert.assertEquals(successMsg, "Login Successfully", "Login message mismatch");
        productListPage.getProductName(productName);
        System.out.println("Product 'Pen' is displayed on UI after adding it via API.");
    }

    @Test(dependsOnMethods = "checkAddedProductONUITest")
    public void deleteProduct() {
        softAssert = new SoftAssert();

        String response = given().log().all()
                .header("Authorization",  token)
                .pathParam("productId", productId)
                .when()
                .delete(ResourceEnum.DELETE_PRODUCT.getResourcePath())
                .then()
                .statusCode(200)
                .log().all()
                .extract().asString();

        JsonPath jsonPath = new JsonPath(response);
        softAssert.assertEquals(jsonPath.getString("message"), "Product Deleted Successfully");
        softAssert.assertAll();
    }
}
