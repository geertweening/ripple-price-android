package com.ripple.price;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ripple.price.util.Log;
import com.ripple.price.util.RippleVolley;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class PriceApplication extends Application implements Observer
{
    public static PriceApplication instance;

    private SharedPreferences sharedPrefs;
    private final static String SHARED_PREFS_NAME = "shared_prefs";

    public PriceApplication ()
    {
        if (instance != null) {
            throw new IllegalStateException("More than one Application instance!");
        }
        instance = this;
    }

    @Override public void onCreate()
    {
        sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        RippleVolley.init(this);
        PriceManager.init(this);

        PriceManager.instance.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o)
    {
        Log.debug("getting update in PriceApplication");
    }

}
