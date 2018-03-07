package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ARIJIT on 02-Feb-17.
 */

public class async_get_products extends AsyncTask<String,Integer,JSONObject> {
    JSONArray product_id_quantity_json;
    Context context;
    long list_id;
    async_get_products(JSONArray product_id_quantity_json, long list_id,Context context){
        this.product_id_quantity_json=product_id_quantity_json;
        this.context=context;
        this.list_id=list_id;
    }
    @Override
    protected JSONObject doInBackground(String... sUrl) {
        JSONObject return_json = new JSONObject();
        JSONObject product_ids_json=new JSONObject();
        JSONArray prod_id_arr=new JSONArray();
        HashMap <Integer,Integer> prod_quantity= new HashMap<>();
        try{
            for (int i=0;i<product_id_quantity_json.length();i++){
                JSONObject prod_quantity_pair=product_id_quantity_json.getJSONObject(i);
                prod_id_arr.put(prod_quantity_pair.getInt("product_id"));
                prod_quantity.put(prod_quantity_pair.getInt("product_id"),prod_quantity_pair.getInt("quantity"));

            }

            product_ids_json.put("product_id_list",prod_id_arr);
        }
        catch(JSONException e){
            e.printStackTrace();
        }


        String JSON_data=product_ids_json.toString();
        String responseString = "";


        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_GET_PRODUCT_DETAILS, "UTF-8");
            multipart.addFormField("JSON_data", "" + JSON_data);


            List<String> response = multipart.finish();
            Log.d("", "SERVER REPLIED:");

            for (String line : response) {
                Log.d("", "Upload Files Response:::" + line);
// get your server response here.
                responseString += line;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            return_json = new JSONObject(responseString);


            if (return_json.getInt("status") == 0) {
                //update database


                try {
                   JSONArray product_info = return_json.getJSONArray("product_list");
                    ArrayList<Product> prod_list = new ArrayList<>();

                    for (int i = 0; i < product_info.length(); i++) {
                        JSONObject c = product_info.getJSONObject(i);
                        int quantity=prod_quantity.get(c.getInt("product_id"));
                        Product Item = new Product(
                                c.getInt("product_id"),
                                c.getString("product_name"),
                                c.getString("product_desc"),
                                c.getString("brand_name"),
                                c.getString("unit"),
                                c.getString("price"),
                                c.getString("offer_price"),
                                c.getString("offer_title"),
                                c.getString("offer_conditions"),
                                c.getString("store"),
                                c.getInt("category_id"),
                                c.getString("valid_until"),
                                c.getString("url"),
                                c.getString("category_keyword"),
                                c.getString("quantity_keyword"));
                        Item.setQuantity(quantity);
                        prod_list.add(Item);



                    }
                    DatabaseHandler db_handler=new DatabaseHandler(context);
                    db_handler.addProducts(prod_list,list_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //toast error message;
//                Toast.makeText(ctx, "Could not update profile", Toast.LENGTH_SHORT).show();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return return_json;


    }

    @Override
    protected void onPostExecute(JSONObject return_json){
        /**
         * Alert user about success or failure
         */
        try{
            if (return_json.getInt("status") == 0) {
                //update database

                Toast.makeText(context,"List saved successfully",Toast.LENGTH_LONG).show();
                sendMessage();


            } else {
                //toast error message;
//                Toast.makeText(ctx, "Could not update profile", Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Could not save list",Toast.LENGTH_LONG).show();

            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(applicationClass.LIST_RECIEVED_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
