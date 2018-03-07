package com.iconodigital.corcholibre;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ARIJIT on 30-Jan-17.
 */

public class async_share_list extends AsyncTask<String,Integer,JSONObject> {
    String email; byte[] share_list_bytes; JSONArray share_list_json_arr;
    String sender_name, sender_email,list_name;
    Context ctx;
    async_share_list(String email, byte[] share_list_bytes, JSONArray share_list_json_arr,
                     String list_name, String sender_name, String sender_email, Context ctx){
        this.email=email;
        this.share_list_bytes=share_list_bytes;
        this.share_list_json_arr=share_list_json_arr;
        this.sender_name=sender_name;
        this.sender_email=sender_email;
        this.list_name=list_name;
        this.ctx=ctx;
    }

    @Override
    protected JSONObject doInBackground(String... sUrl){

        JSONObject return_json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONArray product_info = new JSONArray();
        String JSON_data="";
        String responseString = "";

        try {
            //String base64String = Base64.encodeBase64String(share_list_bytes);
            String base64String = Base64.encodeToString(share_list_bytes, Base64.DEFAULT);
            dataJson.put("email",email);
            dataJson.put("sender_email",sender_email);
            dataJson.put("sender_name",sender_name);
            dataJson.put("list_name",list_name);
            dataJson.put("share_list_bytes_base64", share_list_bytes);
            dataJson.put("share_list_json_arr", share_list_json_arr);

            JSON_data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_SHARE_LIST, "UTF-8");
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

                Toast.makeText(ctx,"List shared successfully",Toast.LENGTH_LONG).show();


            } else {
                //toast error message;
//                Toast.makeText(ctx, "Could not update profile", Toast.LENGTH_SHORT).show();
                Toast.makeText(ctx,"Could not share list",Toast.LENGTH_LONG).show();

            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }
}
