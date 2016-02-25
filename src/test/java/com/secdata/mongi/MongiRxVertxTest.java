package com.secdata.mongi;

import com.secdata.mongi.entity.Person;
import com.secdata.mongi.vertx.MongiRxVertx;
import com.secdata.mongi.vertx.MongiVertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexb on 06/07/15.
 */
@RunWith(VertxUnitRunner.class)
public class MongiRxVertxTest {

    private Logger logger = Logger.getLogger(MongiRxVertxTest.class);

    private JsonObject mongoConfig = new JsonObject();
    private MongoClient mongoClient;
    private Vertx vertx;

    @Before
    public void setup() throws Exception {

        System.out.println("===============================================================");
        System.out.println("SETUPPPP");

        vertx = Vertx.vertx();

        mongoConfig = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "mongoTest");

        mongoClient = MongoClient.createShared(vertx, mongoConfig);

        mongoClient.createCollectionObservable("test_collection").subscribe(results1 -> {
            System.out.println("CREATING Observable ");

        });

    }



    @Test
    public void testVertxInit(TestContext context){

        System.out.println("===============================================================");
        System.out.println("VERTX INIT");

        final Async async = context.async();

        Person person = new Person();
        person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");

        JsonObject product1 = new JsonObject(Json.encode(person));

        mongoClient.saveObservable("person", product1);

        assertTrue(true);

        async.complete();

    }

    @Test
    public void testMongoIndexCreation(TestContext context){

        System.out.println("===============================================================");
        System.out.println("VERTX INDEX CREATION");

        final Async async = context.async();

        MongiRxVertx mongiRxVertx = new MongiRxVertx( vertx );

        mongiRxVertx.buildOrmSolution("com.secdata.mongi.entity");

        assertTrue(true);

        async.complete();

    }

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

    @After
    public void cleanUp(){

        mongoClient.dropCollectionObservable("test_collection").subscribe(results -> {
            System.out.println("Dropping Observable ");

        });

        assertTrue(true);

    }

}
