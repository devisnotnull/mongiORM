package com.secdata.mongi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.secdata.mongi.adapter.MongoJsonToBson;
import com.secdata.mongi.entity.Person;
import com.secdata.mongi.offical.MongiOffical;
import com.secdata.mongi.offical.MongiOfficalAsync;
import com.secdata.mongi.vertx.MongiRxVertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexb on 06/07/15.
 */
public class MongiOfficalAsyncTest {

    private Logger logger = Logger.getLogger(MongiOfficalAsyncTest.class);

    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

    @Before
    public void setup() throws Exception {

        System.out.println("===============================================================");
        System.out.println("SETUPPPP");

    }



    @Test
    public void testVertxInit(){

        System.out.println("===============================================================");
        System.out.println("VERTX INIT");


        Person person = new Person();
        person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");



    }

    @Test
    public void testMongoIndexCreation(){

        System.out.println("===============================================================");
        System.out.println("VERTX INDEX CREATION");

        MongiOfficalAsync mongiOffical = new MongiOfficalAsync("test_mongi_async_offical", "localhost" , 27017);
        mongiOffical.buildOrmSolution("com.secdata.mongi");

        assertTrue(true);

    }

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

    public void cleanUp(){

        MongiOfficalAsync mongiOffical = new MongiOfficalAsync("test_mongi_async_offical", "localhost" , 27017);
        //mongiOffical.getMongoClient().getDatabase("test_mongi_async_offical").drop( ( res , re ) -> {});
        assertTrue(true);

    }

}
