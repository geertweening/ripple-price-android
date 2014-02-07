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

    private static String[] CURRENCIES_LIST = {"USD", "CNY", "EUR", "BTC", "LTC", "NMC"};
    private static String[] GATEWAY_NAMES = {
            "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B", //:  "Bitstamp",
//            "razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA", //: "RippleChina",
//            "rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK", //: "RippleCN",
//            "rNPRNzBB92BVpAhhZr4iXDTveCgV5Pofm9", //: "RippleIsrael",
//            "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q", //: "SnapSwap",
//            "rLEsXccBGNR3UPuPu2hUXPjziKC3qKSBun", //: "The Rock",
//            "rPDXxSZcuVL3ZWoyU82bcde3zwvmShkRyF", //: "WisePass",
//            "rfYv1TXnwgDDK4WQNbFALykYuEBnrR4pDX", //: "Div. Rippler",
//            "rGwUWgN5BEg3QGNY3RX2HfYowjUTZdid3E", //: "TTBit",
//            "r3ADD8kXSUKHd6zTCKfnKT3zV9EZHjzp1S", //: "Ripple Union",
//            "rkH1aQbL2ajA7HUsx8VQRuL3VaEByHELm", //:  "Ripple Money",
//            "rJHygWcTLVpSXkowott6kzgZU6viQSVYM1", //: "Justcoin",
//            "rM8199qFwspxiWNZRChZdZbGN5WrCepVP1", //: "XRP China",
//            "ra9eZxMbJrUcgV8ui7aPc161FgrqWScQxV", //: "Peercover"
    };

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
        for (String currency : CURRENCIES_LIST) {
            getExchangeRates(currency);
        }
    }

    private void getExchangeRates(final String currency)
    {
        try {
            JSONObject payLoad = new JSONObject();
            JSONArray currencies = new JSONArray();
            currencies.put(currency);
            currencies.put("XRP");
            payLoad.put("currencies", currencies);

            JSONArray gateways = new JSONArray();
            for (String gateway : GATEWAY_NAMES) {
                gateways.put(gateway);
            }

            payLoad.put("gateways", gateways);

            Log.debug("exchangeRate %s - %s", currency, gateways);

            JSONArrayRequest request = new JSONArrayRequest(EXCHANGE_RATES_API, payLoad, new Response.Listener<JSONArray>()
            {
                @Override
                public void onResponse(JSONArray jsonObject)
                {
                    Log.debug("response for %s: %s", currency, jsonObject);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    Log.error("error for %s", currency);
                    Log.error(volleyError);
                }
            }
            );

            RippleVolley.getRequestQueue().add(request);


        } catch (JSONException e) {
            Log.error(e);
        }

    }
}
