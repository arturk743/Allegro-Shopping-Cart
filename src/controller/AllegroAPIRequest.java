package controller;

import model.GlobalVariables;
import model.InputData;
import model.Offer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.net.HttpURLConnection;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.*;

public class AllegroAPIRequest implements Runnable{
    private static ArrayList<ArrayList<Offer>> completeOffersList = new ArrayList<>(); // complete list of offers created by all threads
    static int flag = 0 ; //variable control access to

    //set of urls used to make requests
    private static URL authURL;
    private static URL offerURL;
    private static URL sellerURL;
    private static URL prodURL;

    //clientID i clientSecret used to get accessToken
    private String clientID = "bd2e9c3c514945e5848a51c8a718a6f7";
    private String clientSecret = "IOjeZ9jayu8BWrx3j87as39cEboNhC1PWLDeRcN9Iy0qMDQUgqpGUB4R4DPgYqWf";
    private String accessToken;

    private ArrayList<Offer> listOfOffers = new ArrayList<>();
    private InputData inData = new InputData();

    static {
        try {
            authURL = new URL("https://allegro.pl/auth/oauth/token?grant_type=client_credentials");
            offerURL = new URL("https://api.allegro.pl/offers/listing");
            sellerURL = new URL("https://api.allegro.pl/users/");
            prodURL = new URL("https://api.allegro.pl/sale/offers");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean rightOrder(){ // control access to the completeOffersList
        if (Math.abs(flag-inData.getProductFlag()) == 1) { // make threads to write to completeOffersList in proper order
            return true;
        } else { return false; }
    }


    public AllegroAPIRequest(InputData inData) throws InterruptedException, IOException, URISyntaxException {
        this.accessToken = getAccessToken(clientID, clientSecret);
        this.inData = inData.clone();
    }

    public AllegroAPIRequest() throws InterruptedException, IOException, URISyntaxException{
        this.accessToken = getAccessToken(clientID, clientSecret);
        System.out.println(accessToken);
    }

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        new AllegroAPIRequest();
    }


    private JSONObject makeRequest(URL url, ArrayList<ArrayList<String>> headerParameters, String urlParameters){
        //function allow to make http requests
        HttpURLConnection connection = null;

        try {
            System.out.println(url + urlParameters);
            URI temp = URI.create(url + urlParameters);
            HttpGet request = new HttpGet(temp);

            for(ArrayList<String> parameters: headerParameters){
                request.addHeader(parameters.get(0), parameters.get(1));
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            String result="";
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }

            JSONObject jo = new JSONObject(result); //make json object from http response

            return jo;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) { connection.disconnect(); }
        }
    }

    public String getAccessToken(String clientID, String clientSecret) throws URISyntaxException, IOException, InterruptedException { //make HTTP request to get access token
        //function used to generate access token
        String auth = Base64.getEncoder().encodeToString((clientID+":"+clientSecret).getBytes()); //generate proper form of auth

        ArrayList<ArrayList<String>> headerParameters = new ArrayList<>(); //create header parameters
        headerParameters.add(new ArrayList<>(Arrays.asList("Content-Type","application/x-www-form-urlencoded")));
        headerParameters.add(new ArrayList<>(Arrays.asList("Authorization", "Basic " + auth)));

        JSONObject jo = makeRequest(authURL,headerParameters, "");
        String accessToken=jo.getString("access_token");

        return accessToken;

    }

    public ArrayList<Offer> getProduct() throws IOException, InterruptedException {
        //function allow to get list of offers for specific product
        ArrayList<String> category = new ArrayList<>(Arrays.asList("regular","promoted"));
        String phrase = inData.getProductName().replaceAll(" ", "+"); //create proper form of request

        ArrayList<ArrayList<String>> headerParameters = new ArrayList<>();
        headerParameters.add(new ArrayList<>(Arrays.asList("Authorization", "Bearer " + accessToken)));
        headerParameters.add(new ArrayList<>(Arrays.asList("Accept", "application/vnd.allegro.public.v1+json")));

        String urlParameters; //additional parameters for request

        long sumOfTimeForSellerRequests = 0; // used to determine how long it takes to check every seller reputation

        for(int q = 0; q < 100;q+=10) {
            if (inData.getMaxPrice()==Float.MAX_VALUE){
                urlParameters = "?phrase=" + phrase + "&price.from=" + inData.getMinPrice() + "&sellingMode.format=BUY_NOW&sort=+withDeliveryPrice&limit=10&offset=" + q;
            }else {
                urlParameters = "?phrase=" + phrase + "&price.from=" + inData.getMinPrice() + "&price.to=" + inData.getMaxPrice() + "&sellingMode.format=BUY_NOW&sort=+withDeliveryPrice&limit=10&offset=" + q;
            }
            if (inData.isNew()){
                urlParameters += "&parameter.11323=11323_1";
            } else{
                urlParameters += "&parameter.11323=11323_2";
            }
            JSONObject jo = makeRequest(offerURL, headerParameters,urlParameters); //create json object to extract information about every offer

            //offers are kept in two arrays - regular and promoted
            long start = System.currentTimeMillis();
            for (String cat : category){

                for (int i = 0; i < jo.getJSONObject("items").getJSONArray(cat).length(); i++) {
                    JSONObject product = new JSONObject(jo.getJSONObject("items").getJSONArray(cat).get(i).toString());
                    String sellerID = product.getJSONObject("seller").get("id").toString();

                    float rep = getSellerReputation(accessToken, sellerID);

                    String url; //url of offer
                    try{
                        url = product.getJSONObject("vendor").getString("url");
                    } catch (JSONException e){
                        url="https://allegro.pl/listing?string="+product.get("id").toString();
                    }

                    int availableNumberOfItem=0;
                    try{
                        availableNumberOfItem = Integer.parseInt(product.getJSONObject("stock").get("available").toString());
                        if(availableNumberOfItem < inData.getNumberOfItem()){
                            continue;
                        }
                    } catch (JSONException e) {
                        continue;
                    }

                    //if reputation meets required minimum, offer is added to listOfOffers
                    if (rep >= inData.getSellerReputation()) {
                        listOfOffers.add(new Offer(inData.getProductFlag(),
                                product.get("name").toString(),
                                product.get("id").toString(),
                                sellerID,
                                Float.parseFloat(product.getJSONObject("sellingMode").getJSONObject("price").getString("amount")),
                                Float.parseFloat(product.getJSONObject("delivery").getJSONObject("lowestPrice").getString("amount")),
                                url,
                                inData.getNumberOfItem()));
                    }
                }
            }
            long finish = System.currentTimeMillis();

            sumOfTimeForSellerRequests += finish - start;

            if (sumOfTimeForSellerRequests > 7000) { break; } // ends adding new offers to the list after 7 seconds
        }
        return listOfOffers;
    }


    private void sortProductList(){ //sort listOfOffers by price with delivery
        Collections.sort(listOfOffers, new Comparator<Offer>() {
            @Override
            public int compare(Offer offer1, Offer offer2) {
                return Float.compare(offer1.getProductPriceWithDelivery(),
                        offer2.getProductPriceWithDelivery());
            }
        });
    }

    public float getSellerReputation(String accessToken, String sellerID) throws IOException, InterruptedException { //make request to get seller reputation
        ArrayList<ArrayList<String>> headerParameters = new ArrayList<>();
        headerParameters.add(new ArrayList<>(Arrays.asList("Authorization", "Bearer "+accessToken)));
        headerParameters.add(new ArrayList<>(Arrays.asList("Accept","application/vnd.allegro.public.v1+json")));

        String urlParameters = sellerID+"/ratings-summary";

        try{
            JSONObject seller = makeRequest(sellerURL, headerParameters, urlParameters);
            if (seller == null){
                return 0;
            }
            return Float.parseFloat(seller.get("recommendedPercentage").toString().replace(",",".")); //recommended percentage need to bo modify to convert to float

        } catch (JSONException e){
            return 0; //if there is problem with request then 0 is returned
        }
    }


    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            getProduct();
            while(rightOrder() == false){
                Thread.sleep(100);
            }
            sortProductList();
            completeOffersList.add(listOfOffers);
            if (listOfOffers.size() == 0){
                new GlobalVariables().addNotFoundItems(inData.getProductName());
            }
            flag = inData.getProductFlag();
            System.out.println(flag);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
//        System.out.println(timeElapsed/1000.0);
    }

    public static ArrayList<ArrayList<Offer>> getCompleteOffersList() {
        return completeOffersList;
    }

    public void reset(){
        completeOffersList = new ArrayList<>();
        flag = 0 ;

    }
}
