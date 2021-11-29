package com.andromeda.requestnetwork.request;

import android.app.Activity;

import java.util.HashMap;

public class RequestNetworkHelper {
    Activity activity;

    public RequestNetworkHelper(Activity activity){
        this.activity = activity;
    }

    public void sendAnonymous(String method, String url, RequestNetwork.RequestListener requestListener){
        new RequestNetwork(activity).startRequestNetwork(method, url, requestListener);
    }

    public void sendParam(String method, String url, RequestNetwork.RequestListener requestListener, String json){
        RequestNetwork requestNetwork = new RequestNetwork(activity);
        requestNetwork.setParams(json);
        requestNetwork.startRequestNetwork(method,url,requestListener);
    }


    public void sendJson(String method, String url, RequestNetwork.RequestListener requestListener, String json){
        RequestNetwork requestNetwork = new RequestNetwork(activity);
        requestNetwork.setParams(json);
        requestNetwork.startRequestNetwork(method,url,requestListener);
    }

    public void sendJsonHeader(String method, String url, RequestNetwork.RequestListener requestListener, String json, HashMap<String, Object> headers){
        RequestNetwork requestNetwork = new RequestNetwork(activity);
        requestNetwork.setParams(json);
        requestNetwork.setHeaders(headers);
        requestNetwork.startRequestNetwork(method,url,requestListener);
    }

    public void sendParamHeader(String method, String url, RequestNetwork.RequestListener requestListener, HashMap<String, Object> body, HashMap<String, Object> headers){
        RequestNetwork requestNetwork = new RequestNetwork(activity);
        requestNetwork.setParams(body);
        requestNetwork.setHeaders(headers);
        requestNetwork.startRequestNetwork(method,url,requestListener);
    }


    

}
