package com.ripple.price.util;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Geert Weening (geert@ripple.com) on 2/6/14.
 */
public class JSONRequest extends Request<JSONObject>
{
    private final Response.Listener<JSONObject> mListener;

    public JSONRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override public Response<JSONObject> parseNetworkResponse(NetworkResponse response)
    {
        try {
            String charset = HttpHeaderParser.parseCharset(response.headers);
            return Response.success(new JSONObject(new String(response.data, charset.equals(HTTP.DEFAULT_CONTENT_CHARSET) ? HTTP.UTF_8 : charset)), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override protected VolleyError parseNetworkError(VolleyError volleyError)
    {
        return super.parseNetworkError(volleyError);
    }

    @Override public void deliverError(VolleyError error)
    {
        super.deliverError(error);
    }

    @Override protected void deliverResponse(JSONObject response)
    {
        mListener.onResponse(response);
    }

}

