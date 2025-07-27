package org.rem_waste.utils;

public enum ResourceEnum {

    GET_TOKEN("/api/ecom/auth/login"),
    GET_ALL_PRODUCTS("/api/ecom/product/get-all-products"),
    ADD_PRODUCT("/api/ecom/product/add-product"),
    ADD_TO_CART("/api/ecom/cart/add-to-cart"),
    CREATE_ORDER("/api/ecom/order/create-order"),
    GET_ORDER_DETAILS("/api/ecom/order/get-orders-details"),
    DELETE_PRODUCT(" /api/ecom/product/delete-product/{productId}");

    private final String resourcePath;

    ResourceEnum(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
