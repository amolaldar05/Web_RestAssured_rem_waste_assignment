package org.rem_waste.pojo;


public class AddToCartRequest {
    private String _id;
    private Product product;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public static class Product {
        private String _id;
        private String productName;
        private String productCategory;
        private String productSubCategory;
        private int productPrice;
        private String productDescription;
        private String productRating;
        private String productTotalOrders;
        private boolean productStatus;
        private String productFor;
        private String productAddedBy;
        private int __v;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public String getProductSubCategory() {
            return productSubCategory;
        }

        public void setProductSubCategory(String productSubCategory) {
            this.productSubCategory = productSubCategory;
        }

        public int getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(int productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        public String getProductRating() {
            return productRating;
        }

        public void setProductRating(String productRating) {
            this.productRating = productRating;
        }

        public String getProductTotalOrders() {
            return productTotalOrders;
        }

        public void setProductTotalOrders(String productTotalOrders) {
            this.productTotalOrders = productTotalOrders;
        }

        public boolean isProductStatus() {
            return productStatus;
        }

        public void setProductStatus(boolean productStatus) {
            this.productStatus = productStatus;
        }

        public String getProductFor() {
            return productFor;
        }

        public void setProductFor(String productFor) {
            this.productFor = productFor;
        }

        public String getProductAddedBy() {
            return productAddedBy;
        }

        public void setProductAddedBy(String productAddedBy) {
            this.productAddedBy = productAddedBy;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
    }

}

