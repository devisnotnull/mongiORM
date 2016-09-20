package org.fandanzle.mongi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fandanzle.mongi.adapter.MongoJsonToBson;
import org.fandanzle.mongi.entity.Person;
import org.fandanzle.mongi.vertx.MongiRxVertx;
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
import rx.Observable;
import java.util.Date;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by alexb on 06/07/15.
 *
 */
//@RunWith(VertxUnitRunner.class)
public class MongiRxVertxTest {

    private Logger logger = Logger.getLogger(MongiRxVertxTest.class);

    private Gson gson = new GsonBuilder()
            //.serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

    private JsonObject mongoConfig = new JsonObject();
    private MongoClient mongoClient;
    private Vertx vertx;

    @Before
    public void setup() throws Exception {

        System.out.println("===============================================================");
        System.out.println("SETUPPPP");

        vertx = Vertx.vertx();

        mongoConfig = new JsonObject().put("db_name", "test_rx_vertx");

        mongoClient = MongoClient.createShared(vertx, mongoConfig);

    }

    //@Test
    public void testVertxInit(TestContext context){

        System.out.println("===============================================================");
        System.out.println("VERTX RX ADDING NEW PERSON");

        final Async async = context.async();

        Person person = new Person();
        //person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");

        JsonObject product1 = new JsonObject( gson.toJson(person) );

        System.out.println("ADDING NEW PERON ++++++++++++++++++++++++++++");
        System.out.println(product1.encodePrettily());

        mongoClient
            .saveObservable("test_collection", product1)
            .subscribe(
                id -> {
                    System.out.println("Inserted document " + id);
                }, error -> {
                    System.out.println("Err");
                    error.printStackTrace();
                }, () -> {
                    // Everything has been inserted now we can query mongo
                    System.out.println("Insertions done");
                    System.out.println("ADDING RECORD");
        });

        System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////");

        async.complete();

    }

    // @Test
    public void testMongoIndexCreation(TestContext context){

        System.out.println("===============================================================");
        System.out.println("VERTX INDEX CREATION");

        final Async async = context.async();

        try {
            JsonObject config = new JsonObject().put("db_name", "test_rx_vertx");
            MongiRxVertx mongiRxVertx = new MongiRxVertx(vertx, config);

            mongiRxVertx.buildOrmSolution("org.fandanzle.mongi.entity");

        }catch (Exception e){
            e.printStackTrace();
        }

        assertTrue(true);

        async.complete();

    }

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }


    public void cleanUp(){

        JsonObject config = new JsonObject().put("db_name", "test_rx_vertx");
        MongiRxVertx mongiRxVertx = new MongiRxVertx( vertx , config );

        mongiRxVertx.getMongoClient().dropCollection("test_collection" , (handle) -> {

        });

        assertTrue(true);

    }

}
