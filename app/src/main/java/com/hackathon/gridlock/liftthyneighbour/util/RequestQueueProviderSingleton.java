package com.hackathon.gridlock.liftthyneighbour.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 *
 * Singleton class. Provides RequestQueue
 * Created by mukund on 6/19/17.
 */

public class RequestQueueProviderSingleton {

    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue(Context context){
        if (requestQueue == null ) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }
}
