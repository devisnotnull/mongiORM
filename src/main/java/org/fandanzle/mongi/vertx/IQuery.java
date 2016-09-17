package com.stump201.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stump201.mongi.IMongi;
import com.stump201.mongi.annotation.LinkedCollection;
import com.stump201.mongi.entity.Collection;
import com.stump201.mongi.entity.Database;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by alexb on 19/04/2016.
 */
public class IQuery {

    private static Logger logger = Logger.getLogger(IQuery.class);

    MongoClient mongoClient = null;
    Database mongiDb = null;

    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public IQuery(MongiVertx mongi){
        mongiDb = mongi.getMongoDatabase();
        mongoClient = mongi.getMongoClient();
    }

    /**
     *
     * @param save
     * @param item
     * @param resultHandler
     */
    public void save(Class save, Object item, Handler<AsyncResult<String>> resultHandler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(save)).findFirst().get();

        if( item.getClass().equals(save) ){

        } else {
            resultHandler.handle(Future.failedFuture("Doesnt match"));
        }

        for (Field field : item.getClass().getFields()) {

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                linkedCollection.linkedCollection();
            }

        }

        mongoClient.save(collection.getCollectionName(), new JsonObject(Json.encode(item)), e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

    }

    /**
     *
     * @param save
     * @param item
     * @param resultHandler
     */
    public void update(Class save, String id, Object item, Handler<AsyncResult<String>> resultHandler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(save)).findFirst().get();

        if( item.getClass().equals(save) ){
        } else {
            resultHandler.handle(Future.failedFuture("Doesnt match"));
        }

        for (Field field : item.getClass().getFields()) {

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                linkedCollection.linkedCollection();
            }

        }

        JsonObject query = new JsonObject().put("_id", id);
        JsonObject update = new JsonObject().put("$set", new JsonObject(jsonParser.toJson(item)));

        System.out.println(query.encodePrettily());
        System.out.println(update.encodePrettily());

        mongoClient.update(collection.getCollectionName(), query, update , e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture());
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });


    }

    /**
     *
     */
    public void linkDocument(Object base, Object sub, Handler<AsyncResult<String>> resultHandler){

        Collection collectionBase = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(base)).findFirst().get();

        Collection collectionSub = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(sub)).findFirst().get();

        boolean doesExist = false;

        System.out.println("==================================================");
        System.out.println("Annotations for class");
        for (Annotation annotation : base.getClass().getDeclaredAnnotations()) {
            System.out.println(Json.encodePrettily( annotation.toString() ) );
        }

        System.out.println("==================================================");
        System.out.println("Fields for class");
        for (Field field : base.getClass().getDeclaredFields()) {

            System.out.println(field.getName());
            System.out.println("==================");
            System.out.println("Annotations for field");
            System.out.println( Json.encodePrettily( field.getDeclaredAnnotations() ) );

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                System.out.println("===================================");
                System.out.println("WE HAVE A MATCHH ");
                System.out.println(Json.encodePrettily( linkedCollection.linkedCollection() ) );
                if(linkedCollection.linkedCollection().equals(sub)) doesExist = true;
            }

        }

        if(!doesExist){
            System.out.println("===================================");
            System.out.println("CLAZZ NOT FOUND");
            System.out.println("Base clazz : " + base.getClass().getCanonicalName());
            System.out.println("Sub clazz : " + sub.getClass().getCanonicalName());
            resultHandler.handle(Future.failedFuture("Sub document doesnt match clazz"));
            return;
        }

        /**
         JsonObject query = new JsonObject().put("_id", item);

         findOne( collectionBase.getCollectionClazz() , item , baseHandler -> {

         if(baseHandler.succeeded()){

         System.out.println("===================================");
         System.out.println("BASE CLAZZ LOCATED");
         System.out.println(baseHandler.result().encodePrettily());

         JsonObject baseEntity = baseHandler.result();
         String id = baseEntity.getString("_id");

         findOne( sub , subId , subHandler -> {

         if(subHandler.succeeded()){

         System.out.println("===================================");
         System.out.println("SUB CLAZZ LOCATED");
         System.out.println(subHandler.result().encodePrettily());



         }
         if (subHandler.failed()){
         resultHandler.handle(Future.failedFuture("Sub document not found"));
         }

         });

         }

         if(baseHandler.failed()){
         resultHandler.handle(Future.failedFuture("Base document not found"));
         }
         **/

        //});

        /**
         mongoClient.update(collectionBase.getCollectionName(), query, new JsonObject(Json.encode(item)), e -> {
         if (e.succeeded()) resultHandler.handle(Future.succeededFuture());
         else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
         else logger.info("No Callback");
         });
         **/

    }


    /**
     *
     * @param base
     * @param item
     * @param sub
     * @param subId
     * @param resultHandler
     */
    public void linkDocument(Class base, String item, Class sub, String subId, Handler<AsyncResult<String>> resultHandler){

        String linkedField = "";
        Class linkedClazz;

        Collection collectionBase = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(base)).findFirst().get();

        if( !item.getClass().equals(base) )  resultHandler.handle(Future.failedFuture("Base document doesnt match"));;

        Collection collectionSub = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(sub)).findFirst().get();

        if( !item.getClass().equals(base) )  resultHandler.handle(Future.failedFuture("Sub document doesnt match"));

        boolean doesExist = false;

        System.out.println( base.getCanonicalName() );
        System.out.println( Json.encode( base.getClass().getFields() ) );
        System.out.println( Json.encode( base.getAnnotations()) );

        System.out.println("==================================================");
        System.out.println("Annotations for class");
        for (Annotation annotation : base.getDeclaredAnnotations()) {
            System.out.println(Json.encodePrettily( annotation.toString() ) );
        }

        System.out.println("==================================================");
        System.out.println("Fields for class");
        for (Field field : base.getDeclaredFields()) {

            System.out.println(field.getName());
            System.out.println("==================");
            System.out.println("Annotations for field");
            System.out.println( Json.encodePrettily( field.getDeclaredAnnotations() ) );

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){

                System.out.println("===================================");
                System.out.println("WE HAVE A MATCHH ");
                System.out.println(Json.encodePrettily( linkedCollection.linkedCollection() ) );

                linkedClazz = linkedCollection.linkedCollection();
                linkedField = field.getName();

                if(linkedCollection.linkedCollection().equals(sub)) doesExist = true;
            }

        }

        if(!doesExist){
            System.out.println("===================================");
            resultHandler.handle(Future.failedFuture("Sub document doesnt match clazz"));
            return;
        }

        JsonObject query = new JsonObject().put("_id", item);

        findOne( collectionBase.getCollectionClazz() , item , baseHandler -> {

            if(baseHandler.succeeded()){

                System.out.println("===================================");
                System.out.println("BASE CLAZZ LOCATED");
                System.out.println(baseHandler.result().encodePrettily());

                JsonObject baseEntity = baseHandler.result();
                String id = baseEntity.getString("_id");

                findOne( sub , subId , subHandler -> {

                    if(subHandler.succeeded()){

                        System.out.println("===================================");
                        System.out.println("SUB CLAZZ LOCATED");
                        System.out.println(subHandler.result().encodePrettily());

                    }
                    if (subHandler.failed()){
                        resultHandler.handle(Future.failedFuture("Sub document not found"));
                    }

                });

            }

            if(baseHandler.failed()){
                resultHandler.handle(Future.failedFuture("Base document not found"));
            }

        });

        /**
         mongoClient.update(collectionBase.getCollectionName(), query, new JsonObject(Json.encode(item)), e -> {
         if (e.succeeded()) resultHandler.handle(Future.succeededFuture());
         else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
         else logger.info("No Callback");
         });
         **/

    }

    /**
     *
     * @param base
     * @param item
     *
     * @param resultHandler
     */
    public void linkDocument(Class base, String id, Object item, Handler<AsyncResult<String>> resultHandler){

        Collection collectionBase = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(base)).findFirst().get();

        if( !item.getClass().equals(base) )  resultHandler.handle(Future.failedFuture("Base document doesnt match"));

        boolean doesExist = false;

        System.out.println( base.getCanonicalName() );
        System.out.println( Json.encode( base.getClass().getFields() ) );
        System.out.println( Json.encode( base.getAnnotations()) );

        for (Annotation annotation : base.getDeclaredAnnotations()) {
            System.out.println(annotation.toString());
        }

        for (Field field : base.getFields()) {

            JsonObject query = new JsonObject().put("_id", id);

            findOne(collectionBase.getCollectionClazz(), jsonParser.toJson(query), baseHandler -> {

                if (baseHandler.succeeded()) {

                    System.out.println("===================================");
                    System.out.println("BASE CLAZZ LOCATED");
                    System.out.println(baseHandler.result().encodePrettily());

                    JsonObject baseEntity = baseHandler.result();

                }

                if (baseHandler.failed()) {
                    resultHandler.handle(Future.failedFuture("Base document not found"));
                }

            });

            /**
             mongoClient.update(collectionBase.getCollectionName(), query, new JsonObject(Json.encode(item)), e -> {
             if (e.succeeded()) resultHandler.handle(Future.succeededFuture());
             else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
             else logger.info("No Callback");
             });
             **/
        }


    }



    /**
     *
     * @param save
     * @param objectId
     * @param resultHandler
     */
    public void findOne(Class save, String objectId, Handler<AsyncResult<JsonObject>> resultHandler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(save)).findFirst().get();

        if(collection==null){
            resultHandler.handle(Future.failedFuture("Collection does not exist"));
            return;
        }

        JsonObject jsonObject = new JsonObject().put("_id", objectId);

        mongoClient.findOne(collection.getCollectionName(), jsonObject, new JsonObject(), e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });


    }


}
