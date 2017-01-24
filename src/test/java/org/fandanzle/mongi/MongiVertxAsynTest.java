package org.fandanzle.mongi;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.fandanzle.mongi.entity.Cars;
import org.fandanzle.mongi.entity.Database;
import org.fandanzle.mongi.entity.Person;
import org.fandanzle.mongi.vertx.Mongi;
import org.fandanzle.mongi.vertx.Query;
import org.fandanzle.mongi.vertx.QueryAsync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by alexb on 06/07/15.
 *
 */
@RunWith(VertxUnitRunner.class)
public class MongiVertxAsynTest {

    private Logger logger = Logger.getLogger(MongiVertxAsynTest.class);
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

        Person person = new Person();
        person.setName("Alex Lee Brown");
        person.setHeight("33.4");

        QueryAsync query = new QueryAsync(mongiVertx);

        query.insert(Cars.class, cars , e-> {

            if(e.succeeded()){
                System.out.println("=========================");
                System.out.println("OID returnded");
                System.out.println("=========================");
                System.out.println( Json.encodePrettily(  person.get_id().toString() ) );

                query.findOne(Person.class, person.get_id().toString() , find ->{

                    if (find.succeeded()){
                        System.out.println("=========================");
                        System.out.println("PERSON object was sucessfully saved and found again ! HORRAR !!");
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
                async.complete();
                assertFalse(true);
            }

        });

        assertTrue(true);

    }

}
