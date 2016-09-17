package org.fandanzlee.mongi;

import org.fandanzlee.mongi.entity.Cars;
import org.fandanzlee.mongi.entity.Database;
import org.fandanzlee.mongi.entity.Person;
import org.fandanzlee.mongi.vertx.MongiVertx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.fandanzlee.mongi.adapter.MongoJsonToBson;
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

        System.out.println("--------------------------------------------------------");
        System.out.println("Vertx offical driver test");
        System.out.println("--------------------------------------------------------");

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
    public void testMongoIndexCreation(TestContext context){

        System.out.println("--------------------------------------------------------");
        System.out.println("Creating indexes");
        System.out.println("--------------------------------------------------------");

        final Async async = context.async();

        JsonObject config = new JsonObject().put("db_name", "test_vertx");

        MongiVertx mongiVertx = new MongiVertx(vertx , config);

        mongiVertx.buildOrmSolution("org.fandanzlee.mongi.entity");

        Database database = mongiVertx.mongiDb;

        System.out.println("--------------------------------------------------------");
        System.out.println("Our database");
        System.out.println("--------------------------------------------------------");

        System.out.println( Json.encodePrettily( database ) );

        System.out.println("--------------------------------------------------------");
        System.out.println("Does person exist");
        System.out.println("--------------------------------------------------------");
        System.out.println(database.getDatabaseEntities().contains(Mongi.class));


        Cars cars = new Cars();
        cars.setColor("BLUE");
        cars.setNumberplate("qfefwrgvregerg");

        Person person = new Person();
        person.setName("Alex Lee Brown");
        person.setHeight("33.4");

        mongiVertx.save(Cars.class, cars , e-> {

            if(e.succeeded()){

                System.out.println("=========================");
                System.out.println("WE HAVE OUR CAR");
                System.out.println(e.result());

                mongiVertx.save(Person.class, person, personCallback -> {

                    if (personCallback.succeeded()) {

                        System.out.println("=========================");
                        System.out.println("WE HAVE OUR PERSON");
                        System.out.println(personCallback.result());

                        mongiVertx.linkDocument( Person.class , personCallback.result() , Cars.class , e.result() , link -> {

                           if(link.succeeded()){
                               System.out.println("=========================");
                               System.out.println("WE ARE LINKED");
                           }
                            if(link.failed()){
                                System.out.println("=========================");
                                System.out.println("ERROR WERE NOT LINKED");
                            }

                        });

                        mongiVertx.linkDocument( Person.class , personCallback.result() , Cars.class , e.result() , link -> {

                            if(link.succeeded()){
                                System.out.println("=========================");
                                System.out.println("WE ARE LINKED");
                            }
                            if(link.failed()){
                                System.out.println("=========================");
                                System.out.println("ERROR WERE NOT LINKED");
                            }

                        });

                        /**
                        mongiVertx.findOne(Person.class, personCallback.result(), e1 -> {

                            if (e1.succeeded()) {

                                System.out.println(e1.result().encodePrettily());
                                Person person1 = gson.fromJson(e1.result().toString() , Person.class);
                                person1.setHeight("wfwrg");


                                mongiVertx.update(Person.class, personCallback.result() , person1 , update -> {

                                    if(update.succeeded()) System.out.println("WE UPDATED");
                                    if(update.failed()) System.out.println("WE FAILED");

                                });

                            }
                            if (e1.failed()) {

                            }
                        });
                         **/

                    }
                    if (personCallback.failed()) {
                        System.out.println("====================================");
                        System.out.println("Data does not match");
                        assertFalse(true);
                    }

                });

                mongiVertx.findOne(Cars.class, e.result() , e1 -> {
                    if(e1.succeeded()){
                        System.out.println(e1.result().encodePrettily());
                    }
                    if (e1.failed()){

                    }
                });

            }
            if (e.failed()){
                System.out.println("Data does not match");
                assertFalse(true);
            }

        });

        assertTrue(true);

        mongiVertx.listCollectionIndexes();

        async.complete();

    }

    @Test
    public void testVertxInit(TestContext context){

        System.out.println("--------------------------------------------------------");
        System.out.println("Creating document");
        System.out.println("--------------------------------------------------------");

        final Async async = context.async();

        Person person = new Person();
        //person.set_id( UUID.randomUUID().toString() );
        person.setDob(new Date());
        person.setHeight("5,4");
        person.setName("Shizzle King");

        System.out.println(gson.toJson(person));
        JsonObject product1 = new JsonObject(Json.encode(person));

        mongoClient.save("test_collection", product1, id -> {

            if(id.succeeded()){

                System.out.println(id.result());
                context.assertNotNull(id.result(), "If this operation was a sucess then this would be a mongo objectID");



                assertTrue(true);
            }
            if (id.failed()){
                context.fail("Unable to create document");
            }

            async.complete();

        });


    }

    //@After
    public void cleanUp(){

        System.out.println("--------------------------------------------------------");
        System.out.println("Running cleanup");
        System.out.println("--------------------------------------------------------");

        JsonObject config = new JsonObject().put("db_name", "test_vertx");
        MongiVertx mongiVertx = new MongiVertx(vertx , config);
        mongiVertx.getMongoClient().dropCollection("test_collection", (handle) -> {

        });

        assertTrue(true);

    }

}
