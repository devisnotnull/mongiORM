package org.fandanzle.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fandanzle.mongi.IMongi;
import org.fandanzle.mongi.annotation.LinkedCollection;
import org.fandanzle.mongi.entity.Collection;
import org.fandanzle.mongi.entity.Database;
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
     * Save provided object if it matches an existing clazz
     * @param save
     * @param item
     * @param resultHandler
     */
    public IQuery save(Class save, Object item, Handler<AsyncResult<String>> resultHandler){

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

        mongoClient.save(collection.getCollectionName(), new JsonObject( Json.encode(item) ), e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else System.out.println("BADDDDDDD");
        });

        return this;

    }

    /**
     *
     * @param save
     * @param item
     * @param resultHandler
     */
    public IQuery update(Class save, String id, Object item, Handler<AsyncResult<String>> resultHandler){

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

        return this;

    }

    /**
     * Perform a query again a collection, Using standard mongo query tools
     * @param save
     * @param objectId
     * @param resultHandler
     */
    public IQuery query(Class save, String objectId, Handler<AsyncResult<JsonObject>> resultHandler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(save)).findFirst().get();

        if(collection==null){
            // Asyn return, failed to find our collection
            resultHandler.handle(Future.failedFuture("Collection does not exist"));
        }

        JsonObject jsonObject = new JsonObject().put("_id", objectId);

        mongoClient.findOne(collection.getCollectionName(), jsonObject, new JsonObject(), e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;

    }

    /**
     * Perform a generic query against any collection within the context of this database
     * @param query
     * @param resultHandler
     * @return
     */
    public IQuery query(String query,  Handler<AsyncResult<JsonObject>> resultHandler){




        return this;

    }

    /**
     * Find a document from a collection based on its primary ID
     * @param save
     * @param objectId
     * @param resultHandler
     */
    public IQuery findOne(Class save, String objectId, Handler<AsyncResult<JsonObject>> resultHandler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(
                x -> x.getCollectionClazz().equals(save)
        ).findFirst().get();

        if(collection==null){
            resultHandler.handle(Future.failedFuture("Collection does not exist"));

        }

        JsonObject jsonObject = new JsonObject().put("_id", objectId);

        mongoClient.findOne(collection.getCollectionName(), jsonObject, new JsonObject(), e -> {
            if (e.succeeded()) resultHandler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) resultHandler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;
    }



}
