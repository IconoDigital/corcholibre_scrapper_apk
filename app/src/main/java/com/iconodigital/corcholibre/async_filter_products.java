package com.iconodigital.corcholibre;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sourav on 30-Nov-16.
 */

public class async_filter_products extends AsyncTask<String,Integer,JSONObject> {


    String JSON_data;
    Context ctx;


    int product_id;
    String product_name;
    String product_desc;
    String brand_name;
    String unit;
    String price;
    String offer_price;
    String offer_title;
    String offer_conditions;
    String source;
    int category_id;
    String valid_until;
    String url;


    int page_no;
    int no_of_items_in_a_page;
    String col_name;
    String filter;

    private List<Product> productList = new ArrayList<>();
    private Myfile_RecyclerView_Adapter ListAdapter;
    private Ofertas_RecyclerView_Adapter ofertasAdapter;
    private Search_RecyclerView_Adapter SearchAdapter;


    async_filter_products(String col_name, String filter, int page_no, int no_of_items_in_a_page, List<Product> productList, Myfile_RecyclerView_Adapter ListAdapter, Context ctx) {
        this.col_name = col_name;
        this.filter = filter;
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList = productList;
        this.ListAdapter = ListAdapter;
        this.ctx=ctx;
    }


    async_filter_products(String col_name, String filter, int page_no, int no_of_items_in_a_page, List<Product> productList, Ofertas_RecyclerView_Adapter ofertasAdapter, Context ctx) {
        this.col_name = col_name;
        this.filter = filter;
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList = productList;
        this.ofertasAdapter = ofertasAdapter;
        this.ctx=ctx;
    }


    async_filter_products(String col_name, String filter, int page_no, int no_of_items_in_a_page, List<Product> productList, Search_RecyclerView_Adapter SearchAdapter) {
        this.col_name = col_name;
        this.filter = filter;
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList = productList;
        this.SearchAdapter = SearchAdapter;
    }

    @Override
    protected JSONObject doInBackground(String... sUrl) {
        JSONObject return_json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONArray product_info = new JSONArray();

        try {
            if(col_name!=null)
            dataJson.put("col_name", col_name);
            if(filter!=null)
            dataJson.put("filter", filter);
            if(ofertasAdapter!=null)
            dataJson.put("offer", "offer");
            dataJson.put("sort_by", "max_savings");
            dataJson.put("order", "desc");
            dataJson.put("page_no", page_no);
            dataJson.put("no_of_items_in_a_page", no_of_items_in_a_page);
            JSON_data = dataJson.toString();

            //{"col_name":"store","filter":"tottus","offer":"offer","page_no":1,"no_of_items_in_a_page":150,"sort_by":"max_savings","order":"desc"}


        } catch (JSONException e) {
            e.printStackTrace();
        }


        String responseString = "";
        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_FILTER_PRODUCTS, "UTF-8");
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
                    product_info = return_json.getJSONArray("product_info");

                    for (int i = 0; i < product_info.length(); i++) {
                        JSONObject c = product_info.getJSONObject(i);
                        Product Item = new Product(c.getInt("product_id"),
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
                                c.optString("category_keyword",""),
                                c.optString("quantity_keyword","")
                        );
                        productList.add(Item);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //toast error message;
                Toast.makeText(ctx, "Could not update profile", Toast.LENGTH_SHORT).show();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return return_json;

    }





    @Override
    protected void onPostExecute(JSONObject return_json) {
        if ( ListAdapter != null) {
            ListAdapter.notifyDataSetChanged();

        } else if (ofertasAdapter!=null){
            ofertasAdapter.notifyDataSetChanged();
        }else {
            SearchAdapter.notifyDataSetChanged();
        }
    }


}