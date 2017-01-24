package org.fandanzle.mongi;

import org.fandanzle.mongi.entity.Cars;
import org.fandanzle.mongi.entity.Database;
import org.fandanzle.mongi.entity.Person;
import org.fandanzle.mongi.vertx.Mongi;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.fandanzle.mongi.vertx.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 *
 * Created by alexb on 06/07/15.
 *
 */
@RunWith(VertxUnitRunner.class)
public class MongiVertxTest {

    private Logger logger = Logger.getLogger(MongiVertxTest.class);
    private Vertx vertx;

    /**
     *
     * Setup for the test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {


    }


    /**
     *
     * @param context
     */
    @Test
    public void testMongoIndexCreation(TestContext context){


        vertx = Vertx.vertx();

        final Async async = context.async();

        JsonObject config = new JsonObject().put("db_name", "test_vertx");

        Mongi mongiVertx = new Mongi(vertx , config);

        System.out.println("--------------------------------------------------------");
        System.out.println("STANDARD TEST");
        System.out.println("--------------------------------------------------------");

        // Build our ORM profile
        mongiVertx.buildOrmSolution("org.fandanzle.mongi.entity").setRebuild(true);
        // Fetch the current database context, Mongi will create all of your collections
        // within one database
        Database database = mongiVertx.getMongoDatabase();

        Cars cars = new Cars();
        cars.setNumberplate(UUID.randomUUID().toString());
        cars.setColor("BLUE");

        Cars cars1 = new Cars();
        cars.setNumberplate(UUID.randomUUID().toString());
        cars.setColor("BLUE");

        Cars cars2 = new Cars();
        cars.setNumberplate(UUID.randomUUID().toString());
        cars.setColor("BLUE");

        Person person = new Person();
        person.setName("Alex Lee Brown " + UUID.randomUUID());
        person.setHeight("33.4");

        person.getCarsEmbed().add(cars);
        person.getCarsEmbed().add(cars1);
        person.getCarsEmbed().add(cars2);

        person.getCarsReference().add(cars);
        person.getCarsReference().add(cars1);
        person.getCarsReference().add(cars2);

        Query query = new Query(mongiVertx);

        System.out.println("INSERTING !!!!!!!!!");



        query.insert(Person.class, person , e-> {

            if(e.succeeded()){

                System.out.println("=========================");
                System.out.println("PERSON WAS INSERTED ");
                System.out.println("=========================");
                System.out.println( Json.encodePrettily(  e.result() ) );

                query.findOne(Person.class, person.get_id().toString() , find ->{

                    if (find.succeeded()){
                        System.out.println("=========================");
                        System.out.println("PERSON object was sucessfully saved and found again ! HORRAR !!");
                        System.out.print( Json.encodePrettily( find.result() ) );

                        System.out.println("=========================");
                        async.complete();
                        assertTrue(true);
                    }
                    else if (find.failed()){
                        async.complete();
                        assertTrue(false);
                    }
                    else{
                        async.complete();
                        assertTrue(false);
                    }

                });

                async.complete();
                assertTrue(true);

            }
            if(e.failed()){
                System.out.println("=========================");
                System.out.println("ERROR WERE NOT LINKED");
                //e.cause().printStackTrace();
                System.out.println("=========================");
                System.out.println(e.cause().getClass());
                System.out.println(e.cause().getMessage());
                System.out.println("=========================");

                async.complete();
                assertFalse(true);
            }

        });


    }

}
