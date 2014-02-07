package com.ripple.price.util;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * A request for retrieving a {@link org.json.JSONArray} response body at a given URL, allowing for an
 * optional {@link JSONObject} to be passed in as part of the request body.
 * Based on Volleys JsonArrayRequest
 */
public class JSONArrayRequest extends JsonRequest<JSONArray>
{

    private static int TIMEOUT_MS = (int)(30*Time.SEC);
    private static int RETRIES = 1;

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JSONArrayRequest(int method, String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener)
    {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, RETRIES, 1f));
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #JSONArrayRequest(int, String, JSONObject, Listener, ErrorListener)
     */
    public JSONArrayRequest(String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener)
    {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response)
    {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}

