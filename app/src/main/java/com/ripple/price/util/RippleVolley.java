package com.ripple.price.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class RippleVolley {

    private static RequestQueue mRequestQueue;

    private RippleVolley()
    {
        // no instances
    }

    public static void init(Context context)
    {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
    }

    public static RequestQueue getRequestQueue()
    {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

}
