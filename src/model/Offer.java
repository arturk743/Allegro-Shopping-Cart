package model;

public class Offer {
    //class contain information about specific offer
    private String productName;
    private String productID;
    private String sellerID;
    private float productPrice;
    private float deliveryPrice;
    private int productFlag; //inform which product does the offer concern
    private float productPriceWithDelivery;
    private String url;
    private int numberOfItem; //how many pieces of the product we want to buy

    public Offer(int productFlag, String productName, String productID, String sellerID, float productPrice, float deliveryPrice, String url, int numberOfItem) {
        this.productName = productName;
        this.productID = productID;
        this.sellerID = sellerID;
        this.productPrice = productPrice;
        this.deliveryPrice = deliveryPrice;
        this.productFlag = productFlag;
        this.productPriceWithDelivery = productPrice + deliveryPrice;
        this.url = url;
        this.numberOfItem = numberOfItem;
    }

    public String toString() {
        return "ProdName: " + productName +"   "+ "SellerID: " + sellerID +"   "+ "ProdID: " + productID + "   " + "Price: " + productPrice + "   " + "Delivery: " + deliveryPrice;
    }

    public String getProductID() {
        return productID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public float getDeliveryPrice() {
        return deliveryPrice;
    }

    public float getProductPriceWithDelivery() {
        return productPriceWithDelivery;
    }

    public String getUrl() {
        return url;
    }

    public String getProductName() {
        return productName;
    }

    public int getNumberOfItem() { return numberOfItem; }

    public int getProductFlag() { return productFlag; }
}

