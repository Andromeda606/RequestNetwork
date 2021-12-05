package com.andromeda.requestnetwork.request;

import java.io.IOException;

import okhttp3.Response;

public class Return {
    
    Response response;
    String res = null;
    
    public Return(Response response){
        this.response = response;
    }

    public String getString(){
        try {
            if (res == null){
                res = this.response.body().string();
            }
            return res;
            
        } catch (IOException | NullPointerException nullPointerException) {
            return null;
        }
    }
    
    public Response getResponse(){
        return this.response;
    }
}
