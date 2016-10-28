package org.fandanzle.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fandanzle.mongi.IMongi;
import org.fandanzle.mongi.IQuery;
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

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by alexb on 19/04/2016.
 */
public class Query implements IQuery<Handler<AsyncResult<JsonObject>>> {

    private static Logger logger = Logger.getLogger(Query.class);

    MongoClient mongoClient = null;
    Database mongiDb = null;


    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public Query(Mongi mongi){
        mongiDb = mongi.getMongoDatabase();
        mongoClient = mongi.getMongoClient();
    }

    /**
     * Find a collection by is ID param
     * @param clazz
     * @param id
     * @param handler
     * @return
     */
    public Query findOne( Class clazz, Object id, Handler<AsyncResult<JsonObject>> handler){

        Collection collection = getClazzCollection(clazz);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        JsonObject jsonObject = new JsonObject().put("_id", id);

        mongoClient.findOne(collection.getCollectionName(), jsonObject, new JsonObject(), e -> {
            if (e.succeeded()) handler.handle(Future.succeededFuture(e.result()));
            else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;
    }

    /**
     *
     * @param clazz
     * @param id
     * @param handler
     * @return
     */
    public Query query( Class clazz, JsonObject id, Handler<AsyncResult<JsonObject>> handler){

        return this;
    }

    /**
     *
     * @param clazz
     * @param object
     * @param handler
     * @return
     */
    public Query insert(Class clazz, Object object, Handler<AsyncResult<JsonObject>> handler){

        Collection collection = getClazzCollection(clazz);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        for (Field field : clazz.getClass().getFields()) {

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                linkedCollection.linkedCollection();
            }

        }

        mongoClient.save(collection.getCollectionName(), new JsonObject( Json.encode(object) ), e -> {

            if (e.succeeded()) {
                JsonObject idJson = new JsonObject().put("id" , e.result());
                handler.handle(
                        Future.succeededFuture(idJson)
                );
            }
            else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
            else System.out.println("BADDDDDDD");
        });

        return this;
    }

    /**
     *
     * @param clazz
     * @param object
     * @param handler
     * @return
     */
    public Query insertBulk(Class clazz, List<?> object, Handler<AsyncResult<JsonObject>> handler){

        Collection collection = getClazzCollection(clazz);

        if(object.size() == 0) {
            handler.handle(Future.failedFuture("A list with no items was passed"));
            return this;
        }

        String canName = object.get(0).getClass().getCanonicalName();
        String canCompareName = clazz.getCanonicalName();

        if(collection==null){
            System.out.println("BAD");
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        if(!canName.equals(canCompareName)){
            System.out.println("NO COMPARE");
            handler.handle(Future.failedFuture("List does not match generic type of query clazz"));
            return this;
        }

        for(Object item : object){

            try {

                if(!item.getClass().getCanonicalName().equals(canCompareName)){
                    handler.handle(Future.failedFuture("NOT SAME TYPE"));
                }

                mongoClient.save(collection.getCollectionName(), new JsonObject( Json.encode(item) ), e -> {

                    if (e.succeeded()) {
                        JsonObject idJson = new JsonObject().put("id" , e.result());
                        handler.handle(
                                Future.succeededFuture(idJson)
                        );
                    }
                    else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
                    else System.out.println("BADDDDDDD");
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return this;
    }

    /**
     *
     * @param clazz
     * @param id
     * @param object
     * @param handler
     * @return
     */
    public Query update(Class clazz, Object id, Object object, Handler<AsyncResult<JsonObject>> handler){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(clazz)).findFirst().get();

        if( object.getClass().equals(clazz) ){
        } else {
            handler.handle(Future.failedFuture("Doesnt match"));
        }

        JsonObject query = new JsonObject().put("_id", id);
        JsonObject update = new JsonObject().put("$set", new JsonObject(jsonParser.toJson(object)));

        mongoClient.update(collection.getCollectionName(), query, update , e -> {
            if (e.succeeded()) handler.handle(Future.succeededFuture());
            else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;
    }

    /**
     *
     * @param clazz
     * @param object
     * @param asyncResultHandler
     * @return
     */
    public Query delete(Class clazz, Object object, Handler<AsyncResult<JsonObject>> asyncResultHandler){
        return this;
    }

    /**
     *
     * @param clazz
     * @param object
     * @param asyncResultHandler
     * @return
     */
    public Query deleteBulk(Class clazz, Object object, Handler<AsyncResult<JsonObject>> asyncResultHandler){
        return this;
    }

    /**
     * Check Clazz exists within Mongi Context and return
     * @param clazz
     * @return
     */
    private Collection getClazzCollection(Class clazz){

        Collection collection = mongiDb.getDatabaseCollections().stream().filter(
                x -> x.getCollectionClazz().equals(clazz)
        ).findFirst().get();

        if(collection==null) return null;
        else return collection;

    }

}
