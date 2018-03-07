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
 * Created by sourav on 28-Nov-16.
 */

public class async_show_products extends AsyncTask<String,Integer,JSONObject> {


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

    private List<Product> productList_all = new ArrayList<>();
    private List<Product> productList_tottuses;
    private List<Product> productList_plazavea;
    private Ofertas_RecyclerView_Adapter ListAdapter_all;
    private Ofertas_RecyclerView_Adapter ListAdapter_tottus;
    private Ofertas_RecyclerView_Adapter ListAdapter_plazavea;
    private Ofertas_RecyclerView_Adapter ofertasAdapter;
    private Search_RecyclerView_Adapter SearchAdapter;



    async_show_products(int page_no, int no_of_items_in_a_page,
                        List<Product> productList_all,
                        List<Product> productList_tottuses,
                        List<Product> productList_plazavea,
                        Ofertas_RecyclerView_Adapter ListAdapter_all,
                        Ofertas_RecyclerView_Adapter ListAdapter_tottus,
                        Ofertas_RecyclerView_Adapter ListAdapter_plazavea
    ) {
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList_all = productList_all;
        this.productList_tottuses = productList_tottuses;
        this.productList_plazavea = productList_plazavea;
        this.ListAdapter_all = ListAdapter_all;
        this.ListAdapter_tottus = ListAdapter_tottus;
        this.ListAdapter_plazavea = ListAdapter_plazavea;
    }


    async_show_products(int page_no, int no_of_items_in_a_page, List<Product> productList, Ofertas_RecyclerView_Adapter ofertasAdapter) {
        this.page_no = page_no;
        this.no_of_items_in_a_page = no_of_items_in_a_page;
        this.productList_all = productList;
        this.ofertasAdapter = ofertasAdapter;
    }



    @Override
    protected JSONObject doInBackground(String... sUrl) {
        JSONObject return_json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONArray product_info = new JSONArray();

        try {
            dataJson.put("page_no", page_no);
            dataJson.put("no_of_items_in_a_page", no_of_items_in_a_page);
            JSON_data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String responseString = "";
        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_SHOW_PRODUCTS, "UTF-8");
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
                        productList_all.add(Item);
                        //Log.d("ITEM SOURCE "+i+" :",Item.store);
                        if (productList_tottuses != null && Item.store.equals(applicationClass.TYPE_TOTTAS_SMALL))
                            productList_tottuses.add(Item);
                        if (productList_plazavea != null && Item.store.equals(applicationClass.TYPE_PLAZAVIA_SMALL))
                            productList_plazavea.add(Item);

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
        if ( ListAdapter_all != null) {
            ListAdapter_all.notifyDataSetChanged();
            ListAdapter_tottus.notifyDataSetChanged();
            ListAdapter_plazavea.notifyDataSetChanged();

        } else if (ofertasAdapter!=null){
            ofertasAdapter.notifyDataSetChanged();
        }
    }


}
