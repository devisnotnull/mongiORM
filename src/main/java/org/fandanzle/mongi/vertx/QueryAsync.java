package org.fandanzle.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.fandanzle.mongi.IQuery;
import org.fandanzle.mongi.annotation.LinkedCollection;
import org.fandanzle.mongi.entity.Collection;
import org.fandanzle.mongi.entity.Database;
import org.fandanzle.mongi.gson.JsonTransform;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * Created by alexb on 19/04/2016.
 *
 */
public class QueryAsync implements IQuery {

    private static Logger logger = Logger.getLogger(QueryAsync.class);

    MongoClient mongoClient = null;
    Database mongiDb = null;
    Mongi mongi = null;

    com.mongodb.async.client.MongoClient mongoClientAsyn = null;
    MongoDatabase database = null;

    MongoCollection<Document> coll = null;

    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    /**
     *
     * @param mongi
     */
    public QueryAsync(Mongi mongi){

        this.mongi = mongi;
        mongiDb = mongi.getMongoDatabase();
        mongoClient = mongi.getMongoClient();
        // Use a Connection String
        mongoClientAsyn = MongoClients.create("mongodb://localhost");
        database = mongoClientAsyn.getDatabase("testingMe");
        coll = database.getCollection("test");

    }

    /**
     * Find a collection by is ID param
     * @param clazz
     * @param id
     * @param handler
     * @return
     */
    public <FT> QueryAsync findOne(FT clazz, Object id, Handler<AsyncResult<FT>> handler){

        System.out.println(clazz);
        System.out.println(clazz.getClass());
        System.out.println(clazz.getClass().getTypeName());

        Collection collection = getClazzCollection( (Class) clazz);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        JsonObject jsonObject = new JsonObject().put("_id", id);

        mongoClient.findOne(collection.getCollectionName(), jsonObject, new JsonObject(), e -> {

            if (e.succeeded()){
                JsonTransform.jsonToObject(clazz, e.result(), transformHandler -> {

                    FT results = transformHandler.result();

                    if (transformHandler.succeeded()) handler.handle(Future.succeededFuture( (FT) transformHandler.result()));
                    else handler.handle(Future.failedFuture(transformHandler.cause()));
                });
            }
            else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;

    }


    /**
     *
     * @param ftClass
     * @param object
     * @param handler
     * @return
     */
    public <FT> QueryAsync insert(Class<FT> ftClass, Object object, Handler<AsyncResult<String>> handler){

        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        for (Field field : ftClass.getClass().getFields()) {

            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                linkedCollection.linkedCollection();
            }

        }

        Document doc = Document.parse( Json.encode( object ));
        System.out.println("STAGE 11111111");
        coll.insertOne(
                doc,
                (Void result, final Throwable t)
                        -> {
                    System.out.println("STAGE 22222222222");

                    if(t!=null) handler.handle(Future.failedFuture(t));
                    else handler.handle(Future.succeededFuture("ddd"));
                }
        );

        System.out.println("STAGE 333333333333");

        return this;
    }


    /**
     *
     * @param resultHandler
     * @param converter
     * @param <T>
     * @param <R>
     * @return
     */
    private <T, R> SingleResultCallback<T> convertCallback(Handler<AsyncResult<R>> resultHandler, Function<T, R> converter) {
        Context context = mongi.vertx.getOrCreateContext();
        return (result, error) -> {
            context.runOnContext(v -> {
                if (error != null) {
                    resultHandler.handle(Future.failedFuture(error));
                } else {
                    resultHandler.handle(Future.succeededFuture(converter.apply(result)));
                }
            });
        };
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
