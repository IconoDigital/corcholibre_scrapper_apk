package com.iconodigital.corcholibre;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ARIJIT on 30-Jan-17.
 */

public class async_login_signup extends AsyncTask<String,Integer,JSONObject> {
    String fname; String lname; String email; String birthday; String fb_token;
    Context ctx;
    ProgressDialog pd;
    String token=null;
    String gender;
    String country;

    async_login_signup(String fname, String lname, String gender, String email, String birthday, String country,String fb_token,Context ctx){
        this.fname=fname;
        this.lname=lname;
        this.email=email;
        this.gender=gender;
        this.birthday=birthday;
        this.country=country;
        this.fb_token=fb_token;
        this.ctx=ctx;
    }

    @Override
    protected JSONObject doInBackground(String... sUrl){
        JSONObject dataJson = new JSONObject();
        JSONObject return_json= new JSONObject();
        JSONArray product_info = new JSONArray();
        String JSON_data="";
        String responseString = "";
            token = FirebaseInstanceId.getInstance().getToken();
        try {
            dataJson.put("first_name",fname);
            dataJson.put("last_name", lname);
            dataJson.put("email", email);
            dataJson.put("fb_token", fb_token);
            dataJson.put("gender", gender);
            dataJson.put("birth_year", birthday);
            dataJson.put("country", country);
            dataJson.put("firebase_token", token);

            JSON_data = dataJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            MultipartUtility multipart = new MultipartUtility(applicationClass.WS_REGISTER_USER, "UTF-8");
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
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return return_json;


    }

    @Override
    protected void onPostExecute(JSONObject return_json){
        try{
            if(return_json.getInt("status")==0){
                Toast.makeText(ctx,"Logged In",Toast.LENGTH_SHORT);

                Intent i = new Intent(ctx,MainActivity.class);
                ctx.startActivity(i);
                ((LoginActivity)ctx).finish();
            }
            else{
                Toast.makeText(ctx,"could Not log in :"+return_json.getString("msg") ,Toast.LENGTH_SHORT);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(ctx,"JSON Exception",Toast.LENGTH_SHORT);
        }


    }
}
