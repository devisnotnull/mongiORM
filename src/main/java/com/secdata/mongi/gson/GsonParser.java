package com.secdata.mongi.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by alexb on 19/04/2016.
 */
public class GsonParser {

    private static final Gson gson = new GsonBuilder()
            .create();

    private GsonParser(){

    }

    public static Gson action(){
        return gson;
    }

}
