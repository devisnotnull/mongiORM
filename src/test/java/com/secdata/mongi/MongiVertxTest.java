package com.secdata.mongi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.secdata.mongi.adapter.MongoJsonToBson;
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
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by alexb on 06/07/15.
 */
@RunWith(VertxUnitRunner.class)
public class MongiVertxTest {

    private Logger logger = Logger.getLogger(MongiVertxTest.class);

    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

    private JsonObject mongoConfig = new JsonObject();
    private MongoClient mongoClient;
    private Vertx vertx;

    @Before
    public void setup() throws Exception {

        vertx = Vertx.vertx();

        mongoConfig = new JsonObject().put("db_name", "test_vertx");

        mongoClient = MongoClient.createShared(vertx, mongoConfig);

    }

    /**
     *
     * @return
     */
    public MongoClient getMongoClient(){
        return mongoClient;
    }

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

    @Test
    public void testVertxInit(TestContext context){

        final Async async = context.async();

        Person person = new Person();
        person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");

        logger.info("++++++++++++++++++++++++=============================================");
        logger.info(gson.toJson(person));
        JsonObject product1 = new JsonObject(Json.encode(person));

        mongoClient.save("test_collection", product1, id -> {
            logger.info("Adding to mongo collection");
            context.assertTrue(id.succeeded() , "If true operation to add document was sucessfull");
            context.assertNotNull(id.result(), "If this operation was a sucess then this would be a mongo objectID");
            logger.info(id.result());
            assertTrue(true);
            async.complete();
        });


    }

    @Test
    public void testMongoIndexCreation(TestContext context){

        final Async async = context.async();

        JsonObject config = new JsonObject().put("db_name", "test_vertx");

        MongiVertx mongiVertx = new MongiVertx(vertx , config);

        mongiVertx.buildOrmSolution("com.secdata.mongi.entity");

        assertTrue(true);

        mongiVertx.listCollectionIndexes();

        async.complete();

    }



    public void cleanUp(){

        JsonObject config = new JsonObject().put("db_name", "test_vertx");
        MongiVertx mongiVertx = new MongiVertx(vertx , config);
        mongiVertx.getMongoClient().dropCollection("test_collection", (handle) -> {

        });

        /**
        mongiVertx.getMongoClient().runCommand("dropDatabase" , new JsonObject().put("test_vertx",true) , (handler) -> {

        });
         **/

        assertTrue(true);

    }

}
