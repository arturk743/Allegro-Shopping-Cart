package controller;

import model.GlobalVariables;
import model.Offer;
import model.ShoppingCart;

import java.util.ArrayList;

public class BestOptions {
    //class used to calculate best proposition taking into account different offers
    private ArrayList<ShoppingCart> bestOptions = new ArrayList<>();
    private ArrayList<ArrayList<String>> shopList = new ArrayList<>(); //list of list containing shops where you can buy the product


    public BestOptions(ArrayList<ArrayList<Offer>> completeOffersList){
        for(int i = 0; i < 3; i++) {
            bestOptions.add(new ShoppingCart());
        }

        completeOffersList.removeIf(e -> e.isEmpty()); //remove empty list from list

        createShopList(completeOffersList);
        calculateOptions(completeOffersList);
        printBestOptions();
    }

    private void createShopList(ArrayList<ArrayList<Offer>> completeOffersList){
        for (int i = 0; i < completeOffersList.size(); i++){
            shopList.add(new ArrayList<>());
            for (int k = 0; k < completeOffersList.get(i).size(); k++){
                shopList.get(i).add(completeOffersList.get(i).get(k).getSellerID());
                break;
            }
        }
        for(int i = 0; i < shopList.size(); i++ ){
            System.out.println(shopList.get(i));
        }
    }

    private boolean checkDuplicate(ShoppingCart shoppingCart){ // check if the same set of offers is in bestOptions
        for (ShoppingCart shopCart : bestOptions){
            int counter = 0;
            for (Offer offer : shoppingCart.getShoppingList()){
                if (shopCart.containProductID(offer.getProductID())){
                    counter++;
                }
            }
            if(counter == shoppingCart.getShoppingListSize()){
                return true;
            }
        }
        return false;
    }

    private void checkBetterOption(ShoppingCart shoppingCart){
        for(int i = 0; i < bestOptions.size(); i++){
            if(shoppingCart.getTotalCost() < bestOptions.get(i).getTotalCost()){
                if (checkDuplicate(shoppingCart) == false){
                    shoppingCart.sortShoppingList();
                    bestOptions.add(i, shoppingCart.clone());
                }
                break;
            }
        }
        if (bestOptions.size() > 3){
            bestOptions.remove(3);
        }
    }

    private void calculateOptions(ArrayList<ArrayList<Offer>> completeOffersList){
        ShoppingCart temp = new ShoppingCart();
//        for(int l = 0; l < completeOffersList.size(); l++){
//            temp.addProduct(completeOffersList.get(l).get(0));
//            temp.calculateTotalCost();
//            checkBetterOption(temp);
//        }

        for(int i = 0 ; i< completeOffersList.size(); i++){

            for(int k = 0; k < completeOffersList.get(i).size(); k++){
                temp = new ShoppingCart();
                temp.addProduct(completeOffersList.get(i).get(k));
                String sellerID = completeOffersList.get(i).get(k).getSellerID();

                for(int j = 0; j < completeOffersList.size(); j++){
                    if (j == i ){ continue; }
                    if (shopList.get(j).contains(sellerID)){
                        temp.addProduct(completeOffersList.get(j).get(shopList.get(j).indexOf(sellerID)));
                    } else {
                        temp.addProduct(completeOffersList.get(j).get(0));
                    }
                }

                temp.calculateTotalCost();
                checkBetterOption(temp);
            }
            if ((System.currentTimeMillis() - new GlobalVariables().getStartTimeOfMakingRequests()) > 12000){
                System.out.println("Time has ended.");
                break;
            }

        }
    }

    public void printBestOptions(){
        for(int i = 0; i < 3; i++){
            System.out.println("Propozycja nr " + (i+1));
            System.out.println(bestOptions.get(i));
        }
    }

    public ShoppingCart getBestOptions(int index) {
        return bestOptions.get(index);
    }

}
