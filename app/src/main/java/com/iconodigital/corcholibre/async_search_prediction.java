package com.iconodigital.corcholibre;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.iconodigital.corcholibre.Search_Fragment.columns;

/**
 * Created by ARIJIT on 12/8/2016.
 */

public class async_search_prediction extends AsyncTask<String,Integer,JSONObject> {

    int page_no;
    int no_of_items_in_a_page;
    String search_item;
    SearchFeedResultsAdapter mSearchViewAdapter;

    Context ctx;
    ArrayList<String[]> suggestion_list;
    boolean offer=false;

    async_search_prediction(String search_item,SearchFeedResultsAdapter mSearchViewAdapter,Context ctx){
        this.search_item=search_item;
        this.mSearchViewAdapter=mSearchViewAdapter;
        this.page_no=1;
        this.no_of_items_in_a_page=10;

        this.ctx=ctx;
        suggestion_list=new ArrayList<>();
    }
    async_search_prediction(String search_item,SearchFeedResultsAdapter mSearchViewAdapter,Context ctx,boolean offer){
        this.search_item=search_item;
        this.mSearchViewAdapter=mSearchViewAdapter;
        this.page_no=1;
        this.no_of_items_in_a_page=10;

        this.ctx=ctx;
        this.offer=offer;
        suggestion_list=new ArrayList<>();
    }

    @Override
    protected JSONObject doInBackground(String... sUrl){
        JSONObject return_json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONArray product_info = new JSONArray();
        String JSON_data="";
        String responseString = "";

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

        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_SEARCH_PRODUCTS_MODIFIED, "UTF-8");
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
                        String[] suggestion = { c.getString("product_name"), c.getString("product_desc"), c.getString("brand_name")};
                        suggestion_list.add(suggestion);


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
    protected void onPostExecute(JSONObject return_json){

        MatrixCursor matrixCursor = convertToCursor(suggestion_list);
        mSearchViewAdapter.changeCursor(matrixCursor);
    }

    private MatrixCursor convertToCursor(List<String[]> suggestion_list) {
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (String[] suggestion : suggestion_list) {
            String[] temp = new String[4];
            i = i + 1;
            temp[0] = Integer.toString(i);

//            String feedUrl = feedlyResult.getFeedId();
//            if (feedUrl != null) {
//                int index = feedUrl.indexOf("feed/");
//                if (index != -1) {
//                    feedUrl = feedUrl.substring(5);
//                }
//            }
            temp[1] = suggestion[0];
            temp[2] = suggestion[1];
            temp[3] = suggestion[2];
            cursor.addRow(temp);
        }
        return cursor;
    }
}
