package com.iconodigital.corcholibre;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by sourav on 28-Nov-16.
 */

public class applicationClass extends Application {
    public static final String DOMAIN = "http://54.86.9.69/scraper/scrapper_webservice/";
    public static final String WS_REGISTER_USER= DOMAIN+"signup.php";
    public static final String WS_SHARE_LIST= DOMAIN+"shared_list.php";
    public static final String WS_GET_PRODUCT_DETAILS= DOMAIN+"get_product_details.php";
    public static final String WS_GET_USER= DOMAIN+"get_user.php";
    public static final String WS_SHOW_PRODUCTS= DOMAIN+"show_products.php";
    public static final String WS_SEARCH_PRODUCTS= DOMAIN+"search_products.php";
    public static final String WS_SEARCH_PRODUCTS_MODIFIED= DOMAIN+"modified_search_products.php";
    public static final String WS_FILTER_PRODUCTS= DOMAIN+"filter_products.php";
    public static final String WS_SAVE_SHOPPING_LIST= DOMAIN+"save_shopping_list.php";
    public static final String WS_GET_SHOPPING_LIST= DOMAIN+"get_shopping_list.php";
    public static final String WS_UPDATE_SHOPPING_LIST= DOMAIN+"update_shopping_list.php";
    public static final String WS_GET_ALL_SHOPPING_LIST= DOMAIN+"get_all_shopping_list.php";
    public static final String WS_SEND_MESSAGE= DOMAIN+"send_message.php";
    public static final String WS_COUNT_UNREAD_MESSAGE= DOMAIN+"count_unread_message.php";
    public static final String WS_IMAGE_URL= "http://54.86.9.69/scraper/product_images/thumbs/";
    public static final String WS_IMAGE_URL_BIG= "http://54.86.9.69/scraper/product_images/";
    public static final String WS_JPG= ".jpg";
    public static final String TYPE_ALL= "ALL";
    public static final String TYPE_TOTTAS= "TOTTAS";
    public static final String TYPE_PLAZAVIA= "PLAZAVIA";
    public static final String TYPE_ALL_SMALL= "all";
    public static final String TYPE_TOTTAS_SMALL= "tottus";
    public static final String TYPE_PLAZAVIA_SMALL= "plazavea";
    public static final String ALERT_LIST= "##alert_list##";
    public static final String LIST_RECIEVED_INTENT= "shoplist_new_list_recieved";
    public static final String DATE_FORMAT_RSS_2= "EEE, dd MMM yyyy HH:mm";
    public static final String CURRENCY_SYMBOL= "S/ ";
    /*** this variable is for maintaining the checked checkboxes for selected products ***/
    public static ArrayList<Integer> selected_product_ids_arr=new ArrayList<>();
    /*** this is the map of selected products ready to be added to a list**/
    public static HashMap<Integer, Product> selected_prod_map = new HashMap<>();
    public static Product single_selected_prod;
    private static applicationClass singleton;
    // TODO: clean this hashmap when new search is made

    public applicationClass getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;


        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }



}