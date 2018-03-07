package com.iconodigital.corcholibre;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by ARIJIT on 01-Feb-17.
 */

public class save_shared_list_helper {
    Context ctx;
    JSONObject data_obj;
    save_shared_list_helper(Context ctx, JSONObject data_obj){
        this.ctx=ctx;
        this.data_obj=data_obj;
    }

    public void save_list(){
        try{

            String product_id_list=data_obj.getString("product_id_list");
            String list_name=data_obj.getString("list_name");
            String sender_name=data_obj.getString("sender_name");
            String sender_email=data_obj.getString("sender_email");
            DatabaseHandler db_handler=new DatabaseHandler(ctx);
            long list_id=db_handler.addList(list_name+":"+sender_name);
            JSONArray prod_id_quantity_list_json=new JSONArray(product_id_list);

            JSONObject prod_id_list=new JSONObject();

            async_get_products async_get_products_req=new async_get_products(prod_id_quantity_list_json,list_id,ctx);
            async_get_products_req.execute();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }


    }


