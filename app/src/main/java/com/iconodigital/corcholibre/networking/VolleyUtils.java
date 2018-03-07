package com.iconodigital.corcholibre.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Admin on 28-08-2017.
 */

public class VolleyUtils {
    private static VolleyUtils mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private VolleyUtils(Context context) {
        // Specify the application context
        mContext = context;
        // Get the request queue
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyUtils getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mInstance == null) {
            mInstance = new VolleyUtils(context);
        }
        // Return MySingleton new Instance
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        // If RequestQueue is null the initialize new RequestQueue
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // Add the specified request to the request queue
        request.setShouldCache(false);//to get latest updates from server
        getRequestQueue().add(request);
    }

}

