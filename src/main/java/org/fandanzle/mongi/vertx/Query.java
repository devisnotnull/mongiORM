package org.fandanzle.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import io.vertx.core.Context;
import io.vertx.core.json.JsonArray;
import org.apache.log4j.net.SyslogAppender;
import org.bson.Document;
import org.fandanzle.mongi.IMongi;
import org.fandanzle.mongi.IQuery;
import org.fandanzle.mongi.annotation.Embedded;
import org.fandanzle.mongi.annotation.LinkedCollection;
import org.fandanzle.mongi.annotation.Reference;
import org.fandanzle.mongi.entity.Collection;
import org.fandanzle.mongi.entity.Database;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.fandanzle.mongi.gson.JsonTransform;

import javax.management.AttributeList;
import javax.print.attribute.standard.JobSheets;
import javax.xml.ws.AsyncHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.FileSystemNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Created by alexb on 19/04/2016.
 *
 */
public class Query implements IQuery {

    private static Logger logger = Logger.getLogger(Query.class);

    private MongoClient mongoClient = null;
    private Database mongiDb = null;
    private Mongi mongi = null;

    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public Query(Mongi mongi){
        this.mongi = mongi;
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
    public <FT> Query findOne( Class<FT> clazz, Object id, Handler<AsyncResult<FT>> handler){

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

                JsonObject result = e.result();

                for (Field field : ((Class) clazz).getDeclaredFields()) {

                    System.out.println("GENERIC FIELD NAME " + field.getName() );

                    Embedded embedded = field.getAnnotation(Embedded.class);
                    if( embedded !=null ){

                        System.out.println("EMBED FIELD NAME " + field.getName() );

                    }

                    Reference reference = field.getAnnotation(Reference.class);
                    if( reference !=null ){

                        JsonArray proc = result.getJsonArray(field.getName());

                        JsonArray newArr = new JsonArray();

                        for(Object ite : proc.getList() ){
                            try {
                                newArr
                                        .add(
                                                new JsonObject(
                                                        Json.encodePrettily(
                                                                reference
                                                                        .linkedCollection()
                                                                        .newInstance()
                                                        )
                                                )
                                        );

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                handler.handle(Future.failedFuture("Referenced entities do not match !, Schema error"));
                            }
                        }

                        //
                        // Here is our new ARR
                        result.getJsonArray(field.getName()).clear();
                        result.put(field.getName(), newArr);

                        System.out.println(result.encodePrettily());

                    }

                }

                JsonTransform.jsonToObject(clazz, result, transformHandler -> {
                    if (transformHandler.succeeded()) handler.handle(Future.succeededFuture( (FT) transformHandler.result() ));
                    else handler.handle(Future.failedFuture(transformHandler.cause()));
                });

            }
            else if (e.failed()) handler.handle(Future.failedFuture(e.cause()));
            else logger.info("No Callback");
        });

        return this;

    }


    /**
     * Find a collection by is ID param
     * @param clazz
     * @param id
     * @param handler
     * @return
     */
    public <FT> Query findMany( Class<FT> clazz, List<String> id, Handler<AsyncResult<List<FT>>> handler){

        Collection collection = getClazzCollection( (Class) clazz);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        JsonObject jsonObject = new JsonObject();

        id.forEach( each -> {
            jsonObject.put("_id", each);
        });

        mongoClient.find(collection.getCollectionName(), jsonObject, ex -> {
            if(ex.succeeded()){
                JsonTransform.JsonObjectListToObjectList(clazz, ex.result(), transformHandler -> {
                    if(transformHandler.succeeded()){
                        handler.handle(Future.succeededFuture(transformHandler.result()));
                    }
                    else handler.handle(Future.failedFuture(transformHandler.cause()));
                });
            }else {
                handler.handle(Future.failedFuture(ex.cause()));
            }
        });

        return this;

    }


    /**
     *
     * @param clazz
     * @param handler
     * @param <FT>
     * @return
     */
    public <FT> Query query( Class<FT> clazz, Handler<AsyncResult<JsonObject>> handler){


        return this;
    }

    /**
     *
     * @param ftClass
     * @param id
     * @param handler
     * @return
     */
    public <FT> Query query( Class<FT> ftClass, JsonObject id, Handler<AsyncResult<JsonObject>> handler){

        return this;
    }

    /**
     * Bulk insert, Using generic commands
     * @param ftClass
     * @param
     * @param handler
     * @param <FT>
     * @return
     */
    public <FT> Query bulkInsert(Class<FT> ftClass, Set<FT> list, Handler<AsyncResult<JsonObject>> handler) {

        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            System.out.println("NO SUCH COLLECTION");
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        List<Document> doc = new ArrayList<>();

        for(FT item : list){
            String dd = Json.encode(item);
            doc.add(Document.parse(dd));
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("insert", collection.getCollectionName())
                .put("documents",doc);

        mongoClient.runCommand("insert", jsonObject, handlerCom -> {
            if (handlerCom.succeeded()) {
                System.out.println("SAVED !!!!!!!!!!!!!!!!");
                System.out.println(handlerCom.result());
                handler.handle(
                        Future.succeededFuture(handlerCom.result())
                );
            }
            else {
                System.out.println("FAILED !!!!!!!!!!!!!!!!");
                handler.handle(Future.failedFuture(handlerCom.cause()));
            }
        });

        return this;

    }


    /**
     *
     * @param ftClass
     * @param object
     * @param
     * @return
     */
    public <FT> CompletableFuture<String> insert(Class<FT> ftClass, Object object) {

        CompletableFuture<String> promise = new CompletableFuture<>();

        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            promise.completeExceptionally(new Throwable("No such collection"));
        }else {

            mongoClient.save(collection.getCollectionName(), new JsonObject( Json.encodePrettily(object)), (AsyncResult<String> e)-> {

                if (e.succeeded()) {
                    System.out.println("SAVED !!!!!!!!!!!!!!!!");
                    System.out.println(e.result());
                    promise.complete(e.result());
                } else {
                    System.out.println("FAILED !!!!!!!!!!!!!!!!");
                    promise.completeExceptionally(e.cause());
                }

            });
        }

        return promise;
    }


    /**
     *
     * @param ftClass
     * @param object
     * @param
     * @return
     */
    public <FT> CompletableFuture<Void> in(Class<FT> ftClass, Object object) {

        List<JsonObject> aa = new ArrayList<>();
        aa.add(new JsonObject().put("aaa", "aaa"));
        aa.add(new JsonObject().put("bbb", "bbb"));
        aa.add(new JsonObject().put("ccc", "ccc"));
        aa.add(new JsonObject().put("ddd", "ddd"));

        CompletableFuture<Void> promise = new CompletableFuture<>();

        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            promise.completeExceptionally(new Throwable("No such collection"));
        }else {

            mongoClient.save(collection.getCollectionName(), new JsonObject( Json.encodePrettily(object)), (AsyncResult<String> e)-> {

                if (e.succeeded()) {
                    System.out.println("SAVED !!!!!!!!!!!!!!!!");
                    System.out.println(e.result());
                    promise.complete(null);
                } else {
                    System.out.println("FAILED !!!!!!!!!!!!!!!!");
                    promise.completeExceptionally(e.cause());
                }

            });
        }

        return promise;
    }




        /**
         *1
         * @param ftClass
         * @param object
         * @param
         * @return
         */
    public <FT> CompletableFuture<Void> insert1(Class<FT> ftClass, Object object) {

        List<JsonObject> aa = new ArrayList<>();
        aa.add(new JsonObject().put("_id", UUID.randomUUID().toString() ));
        aa.add(new JsonObject().put("_id", UUID.randomUUID().toString() ));
        aa.add(new JsonObject().put("_id", UUID.randomUUID().toString() ));
        aa.add(new JsonObject().put("_id", UUID.randomUUID().toString() ));

        CompletableFuture<Void> promise = new CompletableFuture<>();


        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            promise.completeExceptionally(new Throwable("No such collection"));
        }else {

            for(JsonObject ii : aa){
                promise.thenCompose( c -> in(ftClass, ii));
            }

            promise.complete(null);
        }

        return promise;
    }

    /**
     *
     * @param ftClass
     * @param object
     * @param handler
     * @return
     */
    public <FT> Query insert(Class<FT> ftClass, Object object, Handler<AsyncResult<String>> handler){

        Collection collection = getClazzCollection(ftClass);

        if(collection==null){
            handler.handle(Future.failedFuture("Collection does not exist"));
            return this;
        }

        FT objectMatch = null;

        try{
            objectMatch = (FT) object;
        }catch (Exception e){
            handler.handle(Future.failedFuture(e.getCause()));
            return this;
        }

        JsonObject saveJson = new JsonObject(
                Json.encode(object)
        );


        HashMap<Class, List<JsonObject>> required = new HashMap<>();

        for (Field field : objectMatch.getClass().getDeclaredFields()) {

            Embedded embedded = field.getAnnotation(Embedded.class);
            if( embedded != null ){

                System.out.println("EMBED FIELD NAME " + field.getName() );
            }

            Reference reference = field.getAnnotation(Reference.class);
            if( reference !=null ){

                try {

                    JsonArray array = saveJson.getJsonArray(field.getName());
                    JsonArray strArr = new JsonArray();

                    Iterator itr = array.iterator();

                    required.put(reference.linkedCollection(),new ArrayList<>());

                    while (itr.hasNext()) {
                        JsonObject element = (JsonObject) itr.next();
                        strArr.add(element.getString("_id"));
                        // We need to build a list of items to insert for this document
                        required.get(reference.linkedCollection()).add(element);
                    }

                    saveJson.put(field.getName(), strArr);

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("BAD");
                    handler.handle(Future.failedFuture(e.getCause()));
                    return this;
                }

            }

        }

        mongoClient.save(collection.getCollectionName(), saveJson, e -> {

            if (e.succeeded()) {
                System.out.println("SAVED !!!!!!!!!!!!!!!!");
                System.out.println(e.result());
                handler.handle(
                        Future.succeededFuture(e.result())
                );
            }
            else {
                System.out.println("FAILED !!!!!!!!!!!!!!!!");
                handler.handle(Future.failedFuture(e.cause()));
            }
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
    public <FT> Query insertBulk(Class<FT> ftClass, List<?> object, Handler<AsyncResult<JsonObject>> handler){

        Collection collection = getClazzCollection(ftClass);

        if(object.size() == 0) {
            handler.handle(Future.failedFuture("A list with no items was passed"));
            return this;
        }

        String canName = object.get(0).getClass().getCanonicalName();
        String canCompareName = ftClass.getCanonicalName();

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
    public <FT> Query update(Class<FT> clazz, Object id, Object object, Handler<AsyncResult<JsonObject>> handler){

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
     * @param
     * @param object
     * @param asyncResultHandler
     * @return
     */
    public <FT> Query delete(Class<FT> tClass, Object object, Handler<AsyncResult<JsonObject>> asyncResultHandler){
        return this;
    }

    /**
     *
     * @param
     * @param object
     * @param asyncResultHandler
     * @return
     */
    public <FT> Query deleteBulk(Class<FT> tClass, Object object, Handler<AsyncResult<JsonObject>> asyncResultHandler){
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
