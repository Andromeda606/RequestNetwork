package com.andromeda.requestnetwork.request;

import android.app.Activity;

import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.Response;

public class RequestNetwork {
    private HashMap<String, Object> params = new HashMap<>();
    private String paramsJson = null;
    private HashMap<String, Object> headers = new HashMap<>();

    private final Activity activity;

    public RequestNetwork(Activity activity) {
        this.activity = activity;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public void setParams(String params) {
        this.paramsJson = params;
    }

    public String getParamsJson(){
        return paramsJson;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public Activity getActivity() {
        return activity;
    }

    public void startRequestNetwork(String method, String url, RequestListener requestListener) {
        RequestNetworkController.getInstance().execute(this, method, url, requestListener);
    }
    
    public Return request(String method, String url) {
        return new Return(RequestNetworkController.getInstance().executeAsync(this, method, url));
    }
    
    public Return post(String url) {
        return new Return(RequestNetworkController.getInstance().executeAsync(this, RequestNetworkController.POST, url));
    }
    
    public Return get(String url) {
        return new Return(RequestNetworkController.getInstance().executeAsync(this, RequestNetworkController.GET, url));
    }


    public interface RequestListener {
        void onResponse(String response, Response request);
        void onErrorResponse(String message);
    }
}
