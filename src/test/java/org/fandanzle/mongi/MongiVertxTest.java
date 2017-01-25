package org.fandanzle.mongi;

import org.fandanzle.mongi.entity.*;
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
    //@Test
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

        int gimme = 200;

        Person person = new Person();
        person.setName("Alex Lee Brown " + UUID.randomUUID());
        person.setHeight("33.4");

        int i = 1000;
        while (i > 0) {
            Cars cars = new Cars();
            cars.setNumberplate(UUID.randomUUID().toString());
            cars.setColor("BLUE");
            person.getCarsEmbed().add(cars);
            i--;
        }

        i = 1000;
        while (i > 0) {
            Cars cars = new Cars();
            cars.setNumberplate(UUID.randomUUID().toString());
            cars.setColor("BLUE");
            person.getCarsReference().add(cars);
            i--;

        }

        i = 1000;
        while (i > 0) {
            Phones p1 = new Phones();
            p1.setColor("blue");
            p1.setImei(UUID.randomUUID().toString());
            person.getPhonesEmbed().add(p1);
            i--;

        }

        i = 1000;
        while (i > 0) {
            Phones p1 = new Phones();
            p1.setColor("blue");
            p1.setImei(UUID.randomUUID().toString());
            person.getPhonesReference().add(p1);
            i--;
        }

        Query query = new Query(mongiVertx);

        System.out.println("INSERTING !!!!!!!!!");

        query.insert(Person.class, person , e-> {

            if(e.succeeded()){
                System.out.println( Json.encodePrettily(  e.result() ) );
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


        /**
         query.findOne(Person.class, person.get_id().toString(), find ->{

         if (find.succeeded()){

         System.out.println("=========================");
         System.out.println("PERSON object was sucessfully saved and found again ! HORRAR !!");
         Person pp = find.result();
         System.out.print( Json.encodePrettily( find.result() ) );

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
         **/

    }


    /**
     *
     * @param context
     */
    //@Test
    public void testScopesAndGroups(TestContext context){

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

        int gimme = 200;

        Group group = new Group();
        group.setName("Group " + UUID.randomUUID());
        group.setDescription("Description - " + UUID.randomUUID());

        int i = 1000;
        while (i > 0) {
            Scope scope = new Scope();
            scope.setName("Scope " + UUID.randomUUID().toString());
            group.getScopes().add(scope);
            i--;
        }

        Query query = new Query(mongiVertx);

        System.out.println("INSERTING !!!!!!!!!");

        query.insert(Group.class, group , e-> {

            if(e.succeeded()){
                System.out.println( Json.encodePrettily(  e.result() ) );
                async.complete();
                assertTrue(true);

                query.findOne(Group.class, group.get_id().toString(), find -> {
                    if(e.succeeded()){
                        System.out.println( Json.encodePrettily( find.result() ) );
                    }
                    else {

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


        /**
         query.findOne(Person.class, person.get_id().toString(), find ->{

         if (find.succeeded()){

         System.out.println("=========================");
         System.out.println("PERSON object was sucessfully saved and found again ! HORRAR !!");
         Person pp = find.result();
         System.out.print( Json.encodePrettily( find.result() ) );

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
         **/

    }




    /**
     *
     * @param context
     */
    @Test
    public void testInserMany(TestContext context){

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

        int gimme = 200;

        Group group = new Group();
        group.setName("Group " + UUID.randomUUID());
        group.setDescription("Description - " + UUID.randomUUID());

        int i = 1000;
        while (i > 0) {
            Scope scope = new Scope();
            scope.setName("Scope " + UUID.randomUUID().toString());
            group.getScopes().add(scope);
            i--;
        }

        Query query = new Query(mongiVertx);

        System.out.println("INSERTING BULK  !!!!!!!!!");

        query.bulkInsert(Scope.class, group.getScopes() , e-> {

            if(e.succeeded()){
                System.out.println( Json.encodePrettily(  e.result() ) );
                async.complete();
                assertTrue(true);


            }
            else{
                System.out.println("=========================");
                System.out.println("ERROR WERE NOT LINKED");
                e.cause().printStackTrace();
                System.out.println("=========================");
                System.out.println(e.cause().getClass());
                System.out.println(e.cause().getMessage());
                System.out.println("=========================");

                //async.complete();
                //assertFalse(true);
            }

        });


        /**
         query.findOne(Person.class, person.get_id().toString(), find ->{

         if (find.succeeded()){

         System.out.println("=========================");
         System.out.println("PERSON object was sucessfully saved and found again ! HORRAR !!");
         Person pp = find.result();
         System.out.print( Json.encodePrettily( find.result() ) );

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
         **/

    }





    /**
     *
     * @param context
     */
    @Test
    public void testComposites(TestContext context){

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

        int gimme = 200;

        Group group = new Group();
        group.setName("Group " + UUID.randomUUID());
        group.setDescription("Description - " + UUID.randomUUID());

        Scope scope = new Scope();
        scope.setName("Scope " + UUID.randomUUID().toString());

        Scope scope1 = new Scope();
        scope1.setName("Scope " + UUID.randomUUID().toString());

        Scope scope3 = new Scope();
        scope3.setName("Scope " + UUID.randomUUID().toString());

        Scope scope4 = new Scope();
        scope4.setName("Scope " + UUID.randomUUID().toString());

        scope.getComposites().add(scope1);
        scope.getComposites().add(scope3);
        scope.getComposites().add(scope4);

        Query query = new Query(mongiVertx);

        System.out.println("INSERTING BULK  !!!!!!!!!");

        query.insert(Scope.class, scope , e-> {

            if(e.succeeded()){
                System.out.println( Json.encodePrettily(  e.result() ) );
                async.complete();
                assertTrue(true);
            }
            else{
                System.out.println("=========================");
                System.out.println("ERROR WERE NOT LINKED");
                e.cause().printStackTrace();
                System.out.println("=========================");
                System.out.println(e.cause().getClass());
                System.out.println(e.cause().getMessage());
                System.out.println("=========================");

            }

        });


    }

}
