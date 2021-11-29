package com.andromeda.requestnetwork;

import com.google.gson.Gson;

import java.util.HashMap;

public class HashMapGenerator{
    HashMap<String, Object> hashMap = new HashMap<>();

    public HashMapGenerator put(String key, Object value){
        hashMap.put(key, value);

        return this;
    }

    public HashMap<String, Object>  build(){
        return hashMap;
    }

    public String toJson(){
        return new Gson().toJson(hashMap);
    }
}
