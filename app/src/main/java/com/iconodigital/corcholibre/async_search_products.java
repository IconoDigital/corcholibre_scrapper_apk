package com.iconodigital.corcholibre;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sourav on 03-Dec-16.
 */

public class async_search_products extends AsyncTask<String,Integer,JSONObject> {


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
    String search_item;
    boolean search_prediction;
    private List<Product> productList_all;
    private List<Product> productList_tottuses;
    private List<Product> productList_plazavea;
    private Myfile_RecyclerView_Adapter ListAdapter;
    private Ofertas_RecyclerView_Adapter mAdapter_All_ofertas;
    private Ofertas_RecyclerView_Adapter mAdapter_Tottus_ofertas;
    private Ofertas_RecyclerView_Adapter mAdapter_Plazavea_ofertas;
    private Search_RecyclerView_Adapter mAdapter_All_Search;
    private Search_RecyclerView_Adapter mAdapter_Tottus_Search;
    private Search_RecyclerView_Adapter mAdapter_Plazavea_Search;
    private boolean offer=false;




    async_search_products(String search_item, int page_no, int no_of_items_in_a_page,
                          List<Product> productList_all, List<Product> productList_tottuses,
                          List<Product> productList_plazavea, Search_RecyclerView_Adapter mAdapter_All_Search,
                          Search_RecyclerView_Adapter mAdapter_Tottus_Search, Search_RecyclerView_Adapter mAdapter_Plazavea_Search
    ) {
        this.search_item = search_item;
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList_all = productList_all;
        this.productList_tottuses = productList_tottuses;
        this.productList_plazavea = productList_plazavea;
        this.mAdapter_All_Search = mAdapter_All_Search;
        this.mAdapter_Tottus_Search = mAdapter_Tottus_Search;
        this.mAdapter_Plazavea_Search = mAdapter_Plazavea_Search;
    }
 async_search_products(String search_item, int page_no, int no_of_items_in_a_page,
                       List<Product> productList_all, List<Product> productList_tottuses,
                       List<Product> productList_plazavea, Ofertas_RecyclerView_Adapter mAdapter_All_ofertas,
                       Ofertas_RecyclerView_Adapter mAdapter_Tottus_ofertas, Ofertas_RecyclerView_Adapter mAdapter_Plazavea_ofertas, boolean offer
    ) {
        this.search_item = search_item;
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList_all = productList_all;
     this.productList_tottuses = productList_tottuses;
        this.productList_plazavea = productList_plazavea;
        this.mAdapter_All_ofertas = mAdapter_All_ofertas;
        this.mAdapter_Tottus_ofertas = mAdapter_Tottus_ofertas;
        this.mAdapter_Plazavea_ofertas = mAdapter_Plazavea_ofertas;
        this.offer=offer;
    }


    @Override
    protected JSONObject doInBackground(String... sUrl) {
        JSONObject return_json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONArray product_info = new JSONArray();

        try {
            dataJson.put("search_item",search_item);
            dataJson.put("page_no", page_no);
            dataJson.put("no_of_items_in_a_page", no_of_items_in_a_page);
            if(offer){
                dataJson.put("offer",true);
            }
            JSON_data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String responseString = "";
        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_SEARCH_PRODUCTS, "UTF-8");
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
                    productList_all.clear();
                    productList_tottuses.clear();
                    productList_plazavea.clear();
                    for (int i = 0; i < product_info.length(); i++) {
                        JSONObject c = product_info.getJSONObject(i);
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

                        productList_all.add(Item);
                        if (Item.store.equals(applicationClass.TYPE_TOTTAS_SMALL))
                            productList_tottuses.add(Item);
                        if (Item.store.equals(applicationClass.TYPE_PLAZAVIA_SMALL))
                            productList_plazavea.add(Item);

                    }

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
    protected void onPostExecute(JSONObject return_json) {

        if(!offer){
            mAdapter_All_Search.notifyDataSetChanged();
            mAdapter_Tottus_Search.notifyDataSetChanged();
            mAdapter_Plazavea_Search.notifyDataSetChanged();
        }
        else{
            mAdapter_All_ofertas.notifyDataSetChanged();
            mAdapter_Tottus_ofertas.notifyDataSetChanged();
            mAdapter_Plazavea_ofertas.notifyDataSetChanged();
        }

    }


}