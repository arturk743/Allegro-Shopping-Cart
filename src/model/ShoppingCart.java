package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShoppingCart {
    //class contain set of offers
    private ArrayList<Offer> shoppingList = new ArrayList<>();
    private float totalDeliveryCost = 0;
    private float totalCost = Float.MAX_VALUE;

    private void calculateDeliveryCost(){
        ArrayList<Offer> temp = new ArrayList<>(shoppingList);
        while(temp.isEmpty()==false){
            String sellerID = temp.get(0).getSellerID();
            float deliveryCost = temp.get(0).getDeliveryPrice();
            for(int i = 1; i < temp.size(); i++){
                if(sellerID.equals(temp.get(i).getSellerID())){
                    deliveryCost = Math.max(deliveryCost, temp.get(i).getDeliveryPrice());
                }
            }
            for (int i = 0; i < temp.size(); i++) { //remove shops that are multiple times in shopping list
                if (sellerID.equals(temp.get(i).getSellerID())) {
                    temp.remove(i);
                    i--;
                }
            }
            totalDeliveryCost += deliveryCost;
        }
    }

    public void calculateTotalCost(){
        totalCost = 0;
        totalDeliveryCost = 0;
        calculateDeliveryCost();
        for(int i = 0; i < shoppingList.size(); i++){
            totalCost += shoppingList.get(i).getProductPrice() * shoppingList.get(i).getNumberOfItem();
        }
        totalCost += totalDeliveryCost;
    }

    public ShoppingCart clone(){
        ShoppingCart p = new ShoppingCart();
        for(int i = 0; i < shoppingList.size(); i++){
            p.addProduct(shoppingList.get(i));
        }
        p.calculateTotalCost();
        return p;
    }

    public boolean containProductID(String productID){
        for (Offer offer : shoppingList){
            if (offer.getProductID().equals(productID)){
                return true;
            }
        }
        return false;
    }

    public void sortShoppingList(){
        Collections.sort(shoppingList, new Comparator<Offer>() {
            @Override
            public int compare(Offer offer1, Offer offer2) {
                return offer1.getProductFlag() - offer2.getProductFlag();
            }
        });
    }
    public String toString(){
        String temp="";
        for(int i = 0; i < shoppingList.size(); i++){
            temp = temp + shoppingList.get(i).toString() + "\n";
        }
        return temp;
    }

    public int getShoppingListSize(){
        return shoppingList.size();
    }

    public ArrayList<Offer> getShoppingList(){
        return shoppingList;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void addProduct(Offer offer){
        shoppingList.add(offer);
    }
}
