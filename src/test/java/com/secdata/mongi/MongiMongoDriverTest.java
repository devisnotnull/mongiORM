package com.secdata.mongi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.secdata.mongi.adapter.MongoJsonToBson;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexb on 06/07/15.
 */
public class MongiMongoDriverTest {

    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

}
