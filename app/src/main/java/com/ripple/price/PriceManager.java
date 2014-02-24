package com.ripple.price;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ripple.price.util.JSONArrayRequest;
import com.ripple.price.util.Log;
import com.ripple.price.util.RippleVolley;
import com.ripple.price.util.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class PriceManager extends Observable
{
    public static PriceManager instance;

    private static String[] CURRENCIES_LIST = {"USD", "CNY", "EUR", "BTC", "LTC", "NMC"};
    Map<String, ExchangeRate> exchangeRates;
    Map<String, JSONArray> trades;

    private Context context;
    private Handler bgHandler;
    private final static String EXCHANGE_RATES_API = "http://api.ripplecharts.com/api/offersExercised/offersExercised";

    public static class ExchangeRate
    {
        public final String name;
        public final String address;
        public JSONArray rate;

        public ExchangeRate(String address, String name)
        {
            this.address = address;
            this.name = name;
        }
    }

    private Map<String, List<CurrencyRate>> currencyRates = new HashMap<String, List<CurrencyRate>>();

    public static class CurrencyRate
    {
        public String base;
        public String trade;
        public String issuer;
        public Double open;
        public Double close;
        public Double baseVolume;

        public CurrencyRate(String base, String trade, String issuer, Double open, Double close, Double baseVolume)
        {
            this.base = base;
            this.trade = trade;
            this.issuer = issuer;
            this.open = open;
            this.close = close;
            this.baseVolume = baseVolume;
        }
    }

    private PriceManager(Context context)
    {
        if (instance != null) {
            return;
        }

        exchangeRates = new HashMap<String, ExchangeRate>();
        exchangeRates.put("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B", new ExchangeRate("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B", "Bitstamp"));
        exchangeRates.put("razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA", new ExchangeRate("razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA", "RippleChina"));
        exchangeRates.put("rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK", new ExchangeRate("rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK", "RippleCN"));
        exchangeRates.put("rNPRNzBB92BVpAhhZr4iXDTveCgV5Pofm9", new ExchangeRate("rNPRNzBB92BVpAhhZr4iXDTveCgV5Pofm9", "RippleIsrael"));
        exchangeRates.put("rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q", new ExchangeRate("rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q", "SnapSwap"));
        exchangeRates.put("rLEsXccBGNR3UPuPu2hUXPjziKC3qKSBun", new ExchangeRate("rLEsXccBGNR3UPuPu2hUXPjziKC3qKSBun", "The Rock"));
        exchangeRates.put("rPDXxSZcuVL3ZWoyU82bcde3zwvmShkRyF", new ExchangeRate("rPDXxSZcuVL3ZWoyU82bcde3zwvmShkRyF", "WisePass"));
        exchangeRates.put("rfYv1TXnwgDDK4WQNbFALykYuEBnrR4pDX", new ExchangeRate("rfYv1TXnwgDDK4WQNbFALykYuEBnrR4pDX", "Div. Rippler"));
        exchangeRates.put("rGwUWgN5BEg3QGNY3RX2HfYowjUTZdid3E", new ExchangeRate("rGwUWgN5BEg3QGNY3RX2HfYowjUTZdid3E", "TTBit"));
        exchangeRates.put("r3ADD8kXSUKHd6zTCKfnKT3zV9EZHjzp1S", new ExchangeRate("r3ADD8kXSUKHd6zTCKfnKT3zV9EZHjzp1S", "Ripple Union"));
        exchangeRates.put("rkH1aQbL2ajA7HUsx8VQRuL3VaEByHELm", new ExchangeRate("rkH1aQbL2ajA7HUsx8VQRuL3VaEByHELm", "Ripple Money"));
        exchangeRates.put("rJHygWcTLVpSXkowott6kzgZU6viQSVYM1", new ExchangeRate("rJHygWcTLVpSXkowott6kzgZU6viQSVYM1", "Justcoin"));
        exchangeRates.put("rM8199qFwspxiWNZRChZdZbGN5WrCepVP1", new ExchangeRate("rM8199qFwspxiWNZRChZdZbGN5WrCepVP1", "XRP China"));
        exchangeRates.put("ra9eZxMbJrUcgV8ui7aPc161FgrqWScQxV", new ExchangeRate("ra9eZxMbJrUcgV8ui7aPc161FgrqWScQxV", "Peercover"));

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

            for (Map.Entry<String, ExchangeRate> entry : exchangeRates.entrySet()) {
                getExchangeRates(currency, entry.getKey());
            }
        }
    }

    public Map<String, List<CurrencyRate>> getCurrencyRates()
    {
        return currencyRates;
    }

    @Override
    public void addObserver(Observer observer)
    {
        super.addObserver(observer);
        Log.debug("adding observer, count %s", countObservers());
        setChanged();
        notifyObservers();
    }

    private void getExchangeRates(final String currency, final String issuer)
    {
        try {

            // { "startTime":"2014-02-24T18:40:30.368Z", "endTime":"2014-02-24T19:40:30.368Z","timeIncrement":"hour","timeMultiple":1,"descending":false,"base":{"currency":"XRP"},"trade":{"currency":"USD","issuer":"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"}}

            SimpleDateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            iso8601.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date now = new Date();
            String startTime = iso8601.format(new Date(now.getTime() -  Time.DAY));
            String endTime = iso8601.format(now);

            JSONObject payLoad = new JSONObject();
            payLoad.put("startTime", startTime);
            payLoad.put("endTime", endTime);
            payLoad.put("timeIncrement","all");
            payLoad.put("timeMultiple", 1);
            payLoad.put("descending", false);

            JSONObject base = new JSONObject();
            base.put("currency", "XRP");

            JSONObject trade = new JSONObject();
            trade.put("currency", currency);
            trade.put("issuer", issuer);

            payLoad.put("base", base);
            payLoad.put("trade", trade);

            Log.debug("%s", payLoad);

            JSONArrayRequest request = new JSONArrayRequest(EXCHANGE_RATES_API, payLoad, new Response.Listener<JSONArray>()
            {
                @Override
                public void onResponse(JSONArray jsonObject)
                {
                    Log.debug("response for %s: %s", currency, jsonObject);
                    try {
                        addCurrencyRates(currency, issuer, jsonObject);
                    } catch (JSONException e) {
                        Log.error(e);
                    }
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

    private void addCurrencyRates(String currency, String issuer, JSONArray tradeResponse) throws JSONException
    {
        ArrayList<CurrencyRate> rates = new ArrayList<CurrencyRate>();

        if (tradeResponse.length() < 2) {
            return;
        }

        JSONArray rate = tradeResponse.getJSONArray(1);

        // "startTime","baseCurrVolume","tradeCurrVolume","numTrades","openPrice","closePrice","highPrice","lowPrice","vwavPrice"

        Double baseCurrVolume = rate.getDouble(1);
        Double tradeCurrVolume = rate.getDouble(2);
        Double numTrades = rate.getDouble(3);
        Double openPrice = rate.getDouble(4);
        Double closePrice = rate.getDouble(5);
        Double highPrice = rate.getDouble(6);
        Double lowPrice = rate.getDouble(7);

        String baseCurrency = "XRP";
        String tradeCurrency = currency;
        String tradeIssuer = exchangeRates.get(issuer).name;

        if (!baseCurrency.equals(tradeCurrency)) {
            rates.add(new CurrencyRate(baseCurrency, tradeCurrency, tradeIssuer, openPrice, closePrice, baseCurrVolume));
        }

        if (rates.size() > 0) {
            if (currencyRates.containsKey(currency)) {
                rates.addAll(currencyRates.get(currency));
            }
            currencyRates.put(currency, rates);
            setChanged();
            notifyObservers();
        }



    }
}
