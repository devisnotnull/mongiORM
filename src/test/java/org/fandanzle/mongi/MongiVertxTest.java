package org.fandanzle.mongi;

import org.fandanzle.mongi.entity.Cars;
import org.fandanzle.mongi.entity.Database;
import org.fandanzle.mongi.entity.Person;
import org.fandanzle.mongi.vertx.MongiVertx;
import org.fandanzle.mongi.vertx.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fandanzle.mongi.adapter.MongoJsonToBson;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 * Created by alexb on 06/07/15.
 *
 */
@RunWith(VertxUnitRunner.class)
public class MongiVertxTest {

    private Logger logger = Logger.getLogger(MongiVertxTest.class);

    private Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

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

        MongiVertx mongiVertx = new MongiVertx(vertx , config);

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
        cars.setNumberplate("KJGD887JH");
        cars.setColor("BLUE");
        cars.setNumberplate("qfefwrgvregerg");

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

        IQuery query = new IQuery(mongiVertx);

        System.out.println("--------------------------------------------------------");
        System.out.println("Starting our query");
        System.out.println("--------------------------------------------------------");

        query.save(Cars.class, cars , e-> {

            System.out.println("=========================");
            System.out.println("Primary query");
            System.out.println("=========================");

            if(e.succeeded()){

                System.out.println("=========================");
                System.out.println("OID returnded");
                System.out.println("=========================");
                System.out.println( Json.encodePrettily(  e.result() ) );

                query.findOne(Cars.class, e.result() , find ->{

                    if (find.succeeded()){
                        System.out.println("=========================");
                        System.out.println("Car object was sucessfully saved and found again ! HORRAR !!");
                        System.out.print( find.result().encodePrettily() );
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
            }

        });

    }

}
