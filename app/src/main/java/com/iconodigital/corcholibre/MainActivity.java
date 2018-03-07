package com.iconodigital.corcholibre;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.iconodigital.corcholibre.Utils.FacebookUserModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String name;
    String email;
    String userFBid;
    String profilePic_url;
    Drawable d;
    My_File_Fragment my_file_fragment = null;
    FacebookUserModel facebookUserModel;
    private BroadcastReceiver listReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            if (my_file_fragment != null) {
                my_file_fragment.refreshList();
            }
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle(getResources().getString(R.string.search_products2));

        facebookUserModel = FacebookUserModel.getFacebookUserSingletonModel(this);
        name = facebookUserModel.getFb_first_name() + " " + facebookUserModel.getFb_last_name();
        email = facebookUserModel.getFb_email();
        userFBid = facebookUserModel.getFb_id();
        profilePic_url = facebookUserModel.getFb_profileURL();

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        name = preferences.getString("Name", "");
//        email = preferences.getString("Email", "");
//        userFBid = preferences.getString("UserFBid", "");
//        ProfilePic = preferences.getString("ProfilePic", "");
//
//        if (!name.equalsIgnoreCase("")) {
//
//        }

        /**
         * CHECK IF NOTIFICATION INTENT
         */
        try {
            Intent intent = getIntent();
            if (intent.hasExtra("product_id_list")) {
                JSONObject data_obj = new JSONObject();
                data_obj.put("product_id_list", intent.getStringExtra("product_id_list"));
                data_obj.put("list_name", intent.getStringExtra("list_name"));
                data_obj.put("sender_name", intent.getStringExtra("sender_name"));
                data_obj.put("sender_email", intent.getStringExtra("sender_email"));
                save_shared_list_helper list_save_helper = new save_shared_list_helper(getApplicationContext(), data_obj);
                list_save_helper.save_list();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView nav_userName = (TextView) hView.findViewById(R.id.tvUserName);
        TextView nav_userEmail = (TextView) hView.findViewById(R.id.tvUserMail);
        ImageView nav_profilePicture = (ImageView) hView.findViewById(R.id.ivProfilePicture);

        nav_userName.setText(name);
        nav_userEmail.setText(email);

        load_profile_image(nav_profilePicture, userFBid);

        LocalBroadcastManager.getInstance(this).registerReceiver(listReceiver,
                new IntentFilter(applicationClass.LIST_RECIEVED_INTENT));

//        loadNavItemOnFragment(R.id.nav_ofertas);
        loadNavItemOnFragment(R.id.nav_search);
//        nav_profilePicture.setImageBitmap();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listReceiver);
        super.onDestroy();
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         //getMenuInflater().inflate(R.menu.main2, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        loadNavItemOnFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadNavItemOnFragment(int id) {
        ImageView searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setVisibility(View.GONE);

        if (id == R.id.nav_search) {

            final Search_Fragment fragmentS1 = new Search_Fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main2, fragmentS1).commit();
            //searchIcon for buscar producto search-fragment
            searchIcon.setVisibility(View.VISIBLE);
            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentS1.show_searchView();
                }
            });

        } else if (id == R.id.nav_my_lists) {

            my_file_fragment = new My_File_Fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main2, my_file_fragment).commit();

        } else if (id == R.id.nav_ofertas) {

            final Ofertas_Fragment fragmentS1 = new Ofertas_Fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main2, fragmentS1).commit();
            /*searchIcon.setVisibility(View.VISIBLE);
            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentS1.show_searchView();
                }
            });*/
        } else if (id == R.id.nav_notification) {
            DatabaseHandler db_handler = new DatabaseHandler(this);
            Intent list_intent = new Intent(this, Alert_list_activity.class);
            int alert_list_id = db_handler.getAlertListId();
            list_intent.putExtra("list_id", alert_list_id);
            list_intent.putExtra("list_name", "Alert List");
            this.startActivity(list_intent);

        }
        /*
        else if (id == R.id.nav_settings) {

        }
        */
        else if (id == R.id.nav_logout) {

//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            MainActivity.this.startActivity(intent);
            facebookUserModel.setFb_id("-1");

            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(intent);
            finish();

        }

        if (id != R.id.nav_my_lists) {
            my_file_fragment = null;
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void load_profile_image(final ImageView Iv, final String product_id) {

        final String url = "https://graph.facebook.com/" + product_id + "/picture";


        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this.getBaseContext()));
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(d)
                //.showImageOnFail(default_drawable)
                .showImageOnLoading(d).build();

//initialize image view


//download and display image from url


        imageLoader.displayImage(url, Iv, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                // TODO Auto-generated method stub
                // This will handle 404 and it will catch null exception
                // do here what you want to do
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(d)
                        .showImageOnFail(d)
                        .showImageOnLoading(d).build();
                imageLoader.displayImage(String.valueOf(R.drawable.ic_982), Iv, options);

            }

            @Override
            public void onLoadingComplete(String imageUri,
                                          View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                if (Iv == null)
                    return;

                Iv.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri,
                                           View view) {
            }

        });
    }


}
