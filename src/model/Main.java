package model;

import controller.AllegroAPIRequest;
import controller.BestOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException{

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<InputData> inData = new ArrayList<>();
//        inData.add(new InputData(Product.FIRST_PRODUCT,"iphone X", 80, true, 2000, 3000));
//        inData.add(new InputData(Product.SECOND_PRODUCT,"samsung galaxy s10", 80, true, 2000, 3000));
//        inData.add(new InputData(Product.THIRD_PRODUCT,"parasol", 80, true, 20, 200));
//        inData.add(new InputData(Product.FOURTH_PRODUCT,"choinka",  80, true, 100, 1000));
//        inData.add(new InputData(Product.FIFTH_PRODUCT,"pióro", 80, true, 100, 5000));


        for(int i = 0; i < inData.size(); i++){
            threads.add(new Thread(new AllegroAPIRequest(inData.get(i))));
            threads.get(i).start();

        }

        for (int j = 0; j < threads.size(); j++){ //wait for all thread to complete their tasks
            threads.get(j).join();
        }

//        for(Product prod : new AllegroAPIRequest().getCompleteProductList().get(0)){
//            System.out.println(prod);
//        }

        new BestOptions(AllegroAPIRequest.getCompleteOffersList());



        System.out.println("-----------------");
//        for(int k = 0; k < AllegroAPIRequest.getCompleteProductList().size(); k++){
//            System.out.println(AllegroAPIRequest.getCompleteProductList().get(k).get(40).getProductID());
//        }
        System.out.println(AllegroAPIRequest.getCompleteOffersList().size());


    }
}
