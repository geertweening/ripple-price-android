package com.ripple.price;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ripple.price.util.JSONArrayRequest;
import com.ripple.price.util.Log;
import com.ripple.price.util.RippleVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class PriceManager
{
    public static PriceManager instance;

    private Context context;
    private Handler bgHandler;
    private final static String EXCHANGE_RATES_API = "http://ct.ripple.com:5993/api/exchangeRates";

    private PriceManager(Context context)
    {
        if (instance != null) {
            return;
        }

        instance = this;
        this.context = context;

        // Set up background thread handler and do some stuff on the bg thread
        new Thread("background-tasks")
        {
            public void run()
            {
                Looper.prepare();
                bgHandler = new Handler();
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                getExchangeRates();

                Looper.loop();
            }
        }.start();
    }

    public static void init(Context context)
    {
        new PriceManager(context);
    }

    public void post(Runnable runnable)
    {
        bgHandler.post(runnable);
    }

    private void getExchangeRates()
    {
        try {
            JSONObject payLoad = new JSONObject();
            JSONArray currencies = new JSONArray();
            currencies.put("USD");
            currencies.put("XRP");
            payLoad.put("currencies", currencies);

            JSONArray gateways = new JSONArray();
            gateways.put("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B");
            payLoad.put("gateways", gateways);


            JSONArrayRequest request = new JSONArrayRequest(EXCHANGE_RATES_API, payLoad, new Response.Listener<JSONArray>()
            {
                @Override
                public void onResponse(JSONArray jsonObject)
                {
                    Log.debug(jsonObject);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    Log.error(volleyError);
                }
            }
            );

            RippleVolley.getRequestQueue().add(request);


        } catch (JSONException e) {
            return;
        }

    }
}
