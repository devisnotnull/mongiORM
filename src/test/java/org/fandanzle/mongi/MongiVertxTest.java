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
    public void bulkCarsCreate(TestContext context) {

        vertx = Vertx.vertx();

        final Async async = context.async();

        JsonObject config = new JsonObject().put("db_name", "test_vertx");
        // Build our ORM profile
        Mongi mongi = new Mongi(vertx , config);
        mongi.buildOrmSolution("org.fandanzle.mongi.entity").setRebuild(true);
        // Start query proxy
        Query query = new Query(mongi);

        Integer i = 100;

        List<Cars> carsList = new ArrayList<Cars>();

        while (i>0){

            Cars cars = new Cars();
            cars.setNumberplate(UUID.randomUUID().toString());
            cars.setColor("BLUE");

            System.out.println("--------------------------------------------------------");
            System.out.println("Creating Car");
            System.out.println("--------------------------------------------------------");
            System.out.println( Json.encodePrettily( cars ) ) ;

            carsList.add(cars);
            i--;
        }

        query.insertBulk(Cars.class, carsList, handler->{

            if (handler.succeeded()){
                System.out.println("=========================");
                System.out.println("Car object was sucessfully saved and found again ! HORRAR !!");
                System.out.print( handler.result() );
                System.out.println("=========================");
                async.complete();
                assertTrue(true);
            }
            else if (handler.failed()){
                System.out.println(handler.cause().getMessage());
                handler.cause().printStackTrace();
                async.complete();
                assertTrue(false);
            }

        });

    }

    /**
     *
     * @param context
     */
    //@Test
    public void testMongoIndexCreation(TestContext context){

        vertx = Vertx.vertx();

        final Async async = context.async();

        JsonObject config = new JsonObject().put("db_name", "test_vertx");

        Mongi mongiVertx = new Mongi(vertx , config);

        System.out.println("--------------------------------------------------------");
        System.out.println("Building ORM profiile");
        System.out.println("--------------------------------------------------------");
        // Build our ORM profile
        mongiVertx.buildOrmSolution("org.fandanzle.mongi.entity").setRebuild(true);

        System.out.println("--------------------------------------------------------");
        System.out.println("Our database from ORM Profile");
        System.out.println("--------------------------------------------------------");
        // Fetch the current database context, Mongi will create all of your collections
        // within one database
        Database database = mongiVertx.getMongoDatabase();
        System.out.println( Json.encodePrettily( database ) );

        System.out.println("--------------------------------------------------------");
        System.out.println("Database Entities");
        System.out.println("--------------------------------------------------------");
        // Fetch all collections mapped from your annotated entities
        System.out.println( Json.encodePrettily( database.getDatabaseEntities()) ) ;

        Cars cars = new Cars();
        cars.setNumberplate(UUID.randomUUID().toString());
        cars.setColor("BLUE");

        System.out.println("--------------------------------------------------------");
        System.out.println("Creating Car");
        System.out.println("--------------------------------------------------------");
        System.out.println( Json.encodePrettily( cars ) ) ;

        Person person = new Person();
        person.setName("Alex Lee Brown");
        person.setHeight("33.4");

        System.out.println("--------------------------------------------------------");
        System.out.println("Creating Person");
        System.out.println("--------------------------------------------------------");
        System.out.println( Json.encodePrettily( cars ) ) ;

        Query query = new Query(mongiVertx);

        System.out.println("--------------------------------------------------------");
        System.out.println("Starting our query");
        System.out.println("--------------------------------------------------------");

        query.insert(Cars.class, cars , e-> {

            System.out.println("========================");
            System.out.println("Primary query");
            System.out.println("=========================");

            if(e.succeeded()){

                System.out.println("=========================");
                System.out.println("OID returnded");
                System.out.println("=========================");
                System.out.println( Json.encodePrettily(  e.result() ) );

                async.complete();
                assertTrue(true);


                query.findOne(Cars.class, e.result() , find ->{

                    if (find.succeeded()){
                        System.out.println("=========================");
                        System.out.println("Car object was sucessfully saved and found again ! HORRAR !!");
                        System.out.print( find.result() );
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
