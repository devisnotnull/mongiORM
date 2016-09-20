package org.fandanzle.mongi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.util.JSON;
import org.fandanzle.mongi.adapter.MongoJsonToBson;
import org.fandanzle.mongi.entity.Person;
import org.fandanzle.mongi.offical.MongiOffical;
import org.fandanzle.mongi.offical.MongiOfficalAsync;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexb on 06/07/15.
 */
public class MongiOfficalTest {

    private Logger logger = Logger.getLogger(MongiOfficalTest.class);
    Gson gson = new GsonBuilder()
            //.serializeNulls()
            .registerTypeAdapter(ObjectId.class, MongoJsonToBson.oidToBson)
            .registerTypeAdapter(Date.class, MongoJsonToBson.dateToBson)
            .create();

    //@Before
    public void setup() throws Exception {

        logger.info("===============================================================");
        System.out.println("SETUPPPP");

    }

    //@Test
    public void testVertxInit(){

        System.out.println("===============================================================");
        System.out.println("VERTX INIT");

        MongiOffical mongiOffical = new MongiOffical("test_mongi_offical", "localhost" , 27017);

        Person person = new Person();
        //person.set_id( UUID.randomUUID().toString() );
        //person.setDob(new Date());
        person.setHeight("5,4");
        person.setName( UUID.randomUUID().toString() );

        String coll = gson.toJson( person );
        Document doc = Document.parse( coll );

        System.out.println("-------------------------------------------------------");
        System.out.println( doc.toJson() );
        System.out.println("-------------------------------------------------------");

        mongiOffical.getMongoClient().getDatabase("test_mongi_offical").getCollection("test_collection").insertOne(doc);

        assertTrue(true);
    }

    //@Test
    public void testMongoIndexCreation(){

        System.out.println("===============================================================");
        System.out.println("VERTX INDEX CREATION");

        MongiOffical mongiOffical = new MongiOffical("test_mongi_offical", "localhost" , 27017);
        mongiOffical.buildOrmSolution("org.fandanzle.mongi");

        assertTrue(true);

    }

    @Test
    public void testConcatenate() {
        String result = "onetwo";
        assertEquals("onetwo", result);
    }

    public void cleanUp(){

        MongiOffical mongiOffical = new MongiOffical("test_mongi_offical", "localhost" , 27017);
        mongiOffical.getMongoClient().getDatabase("test_mongi_offical").drop();
        assertTrue(true);

    }

}
