package com.secdata.mongi;

import com.google.gson.annotations.Expose;
import com.secdata.mongi.entity.Person;
import com.secdata.mongi.vertx.MongiVertx;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by alexb on 06/07/15.
 */
//@RunWith(VertxUnitRunner.class)
public class MongiVertxTest {

    private Logger logger = Logger.getLogger(MongiVertxTest.class);

    private JsonObject mongoConfig = new JsonObject();
    private MongoClient mongoClient;
    private Vertx vertx;

    @Before
    public void setup() throws Exception {

        vertx = Vertx.vertx();

        mongoConfig = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "mongoTest");

        mongoClient = MongoClient.createShared(vertx, mongoConfig);

        mongoClient.dropCollection("person", (handle) -> {

        });

    }

    //@Test
    public void testVertxInit(TestContext context){

        final Async async = context.async();

        Person person = new Person();
        person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");

        JsonObject product1 = new JsonObject(Json.encode(person));

        mongoClient.save("person", product1, id -> {

            logger.info("Adding to mongo collection");
            context.assertTrue(id.succeeded() , "If true operation to add document was sucessfull");
            context.assertNotNull(id.result(), "If this operation was a sucess then this would be a mongo objectID");
            logger.info(id.result());
            async.complete();


        });

    }

    //@Test
    public void testMongoIndexCreation(TestContext context){

        final Async async = context.async();

        MongiVertx mongiVertx = new MongiVertx(vertx);

        mongiVertx.buildOrmSolution("com.secdata.mongi.entity");

        assertTrue(true);

        mongiVertx.listCollectionIndexes();

        async.complete();

    }

    //@Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

    //@After
    public void cleanUp(){

        assertTrue(true);

    }

}
