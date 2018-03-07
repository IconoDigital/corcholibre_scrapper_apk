package com.iconodigital.corcholibre;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP){
//            ProgressBar  progressBar=findViewById(R.id.progressBar2);
//            Drawable wrapDrawable= DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
//            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, R.color.colorPrimary));
//            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
//        }
        try{
            Intent intent=getIntent();
            if(intent.hasExtra("product_id_list")){
                JSONObject data_obj=new JSONObject();
                data_obj.put("product_id_list",intent.getStringExtra("product_id_list"));
                data_obj.put("list_name",intent.getStringExtra("list_name"));
                data_obj.put("sender_name",intent.getStringExtra("sender_name"));
                data_obj.put("sender_email",intent.getStringExtra("sender_email"));
                save_shared_list_helper  list_save_helper=new save_shared_list_helper(getApplicationContext(),data_obj);
                list_save_helper.save_list();
            }



        }
        catch(JSONException e){
            e.printStackTrace();
        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, 50);
    }
}
