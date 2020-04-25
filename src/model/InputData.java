package model;

public class InputData {
    //class contain information about product provided by user
    private int productFlag;
    private String productName;
    private int sellerReputation;
    private boolean isNew;
    private float minPrice;
    private float maxPrice;
    private int numberOfItem;

    public InputData(int productFlag, String productName, int sellerReputation, boolean isNew, float minPrice, float maxPrice) {
        this.productFlag = productFlag;
        this.productName = productName;
        this.sellerReputation = sellerReputation;
        this.isNew = isNew;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public InputData(){
    }

    public InputData clone(){
        InputData temp = new InputData();
        temp.setProductName(productName);
        temp.setMaxPrice(maxPrice);
        temp.setMinPrice(minPrice);
        temp.setNumberOfItem(numberOfItem);
        temp.setSellerReputation(sellerReputation);
        temp.setProductFlag(productFlag);
        temp.setNew(isNew);
        return temp;
    }

    public String toString(){
        if (maxPrice == Float.MAX_VALUE) {
            return "Name: " + productName + "   Seller reputation: " + sellerReputation + "   Price from: " + minPrice;
        }else{
            return "Name: " + productName + "   Seller reputation: " + sellerReputation + "   Price range: " + minPrice + "-" + maxPrice;
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSellerReputation(int sellerReputation) {
        this.sellerReputation = sellerReputation;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getProductFlag() {
        return productFlag;
    }

    public String getProductName() {
        return productName;
    }

    public int getSellerReputation() {
        return sellerReputation;
    }

    public boolean isNew() {
        return isNew;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setProductFlag(int flag){
        this.productFlag = flag;
    }

    public int getNumberOfItem() {
        return numberOfItem;
    }

    public void setNumberOfItem(int numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

}