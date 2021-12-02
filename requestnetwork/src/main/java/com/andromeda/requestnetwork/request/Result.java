package com.androsoft.twitteraccountcreator.Model;

import java.io.IOException;

import okhttp3.Response;

public class Return {
    
    Response response;
    
    public Return(Response response){
        this.response = response;
    }

    public String getString(){
        try {
            return this.response.body().string();
        } catch (IOException | NullPointerException nullPointerException) {
            return null;
        }
    }
    
    public Response getResponse(){
        return this.response;
    }
}
