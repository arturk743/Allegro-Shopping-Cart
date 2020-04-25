package model;



import controller.BestOptions;

import java.util.ArrayList;

public class GlobalVariables {
    //class contain variables used by different parts of program
    private static BestOptions bestOptions;
    private static ArrayList<InputData> productList = new ArrayList<>();
    private static int selectedIndex;
    private static long startTimeOfMakingRequests =0;
    private static ArrayList<String> notFoundItems = new ArrayList<String>();

    public void removeProduct(){
        //used to remove selected product from list(in EditProduct)
        this.productList.remove(selectedIndex);
        selectedIndex = -1; // set to -1 in order to avoid remove another product
    }

    public void reset(){
        //reset variables after showing results
        bestOptions = null;
        notFoundItems = new ArrayList<>();
        selectedIndex = -1;
    }

    public static void setStartTimeOfMakingRequests(long startTimeOfMakingRequests) {
        GlobalVariables.startTimeOfMakingRequests = startTimeOfMakingRequests;
    }

    public static long getStartTimeOfMakingRequests() {
        return startTimeOfMakingRequests;
    }

    public ArrayList<String> getNotFoundItems() {
        return notFoundItems;
    }

    public void addNotFoundItems(String notFoundItems) {
        this.notFoundItems.add(notFoundItems);
    }

    public ArrayList<InputData> getProductList() {
        return productList;
    }

    public void addProduct(InputData productList) {
        this.productList.add(productList);
    }

    public void removeProduct(int index){
        this.productList.remove(index);
    }

    public void setSelectedIndex(int selectedIndex) {
        GlobalVariables.selectedIndex = selectedIndex;
    }

    public InputData getProduct(){
        return productList.get(selectedIndex);
    }

    public static BestOptions getBestOptions() {
        return bestOptions;
    }

    public static void setBestOptions(BestOptions bestOptions) {
        GlobalVariables.bestOptions = bestOptions;
    }
}
