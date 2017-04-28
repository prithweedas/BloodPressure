package com.example.prithwee.bloodpressure;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Prithwee on 26-04-2017.
 */

public class NetworkUtils {
    private static final String baseUrl = "http://ritwicktestiot.azurewebsites.net/api/sensor/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String getURL(String param){
        return  baseUrl + param;
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getURL(url), params, responseHandler);
    }
}
