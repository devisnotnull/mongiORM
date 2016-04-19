package com.secdata.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.secdata.mongi.annotation.*;

import com.secdata.mongi.entity.Collection;
import com.secdata.mongi.entity.CollectionField;
import com.secdata.mongi.entity.CollectionIndex;
import com.secdata.mongi.entity.Database;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by alexb on 11/02/2016.
 */
public class MongiVertx {

    private static Logger logger = Logger.getLogger(MongiVertx.class);
    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();
    private String database;
    private MongoClient mongoClient;

    public Database mongiDb = new Database();

    /**
     * @param vertx
     */
    public MongiVertx(Vertx vertx) {
        mongoClient = MongoClient.createShared(vertx, new JsonObject());
    }

    public MongiVertx(Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config);
    }

    /**
     *
     * @return
     */
    public MongoClient getMongoClient(){
        return mongoClient;
    }

    /**
     * This function takes a package name to scan and profiles the schema to insert and the indexs to ensure.
     *
     * @param packageName
     * @return
     */
    public MongiVertx buildOrmSolution(String packageName) {

        // Store our collections
        List<Collection> collectionsList = new ArrayList<>();
        //
        HashMap<String, HashMap<String, String>> collectionIndex = new HashMap<String, HashMap<String, String>>();
        // TODO create IDP providers and store on verticle creation
        // Hashmap to store IDP providers
        // Java reflections, We loads the IDP providers via generics
        Reflections reflections = new Reflections(packageName);
        // Fetch all classes that have the ProviderTypeAnnotation.class annotation
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CollectionDefinition.class);
        // Iterate
        Database database = new Database();

        List<Class> definedClass = new ArrayList<>();
        List<Collection> mappedCollections = new ArrayList<>();

        for (Class ii : annotated) {

            System.out.println(ii.getCanonicalName());

            Annotation collectionDefinition = ii.getAnnotation(CollectionDefinition.class);

            if (collectionDefinition instanceof CollectionDefinition) {

                try {
                    Collection collection = new Collection();
                    collection.setCollectionName(((CollectionDefinition) collectionDefinition).collectionName());
                    collection.setCollectionClass(ii.getCanonicalName());
                    collection.setCollectionClazz(Class.forName(ii.getCanonicalName()));
                    collection.setCollectionFields(getCollectionFields(ii));
                    collection.setCollectionIndexes(getCollectionIndexes(ii));

                    definedClass.add(Class.forName(ii.getCanonicalName()));

                    mappedCollections.add(collection);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }

        database.setDatabaseCollections(mappedCollections);
        database.setDatabaseEntities(definedClass);

        mongiDb = database;

        // Iterate the collection annotations set
        //createBulkIndexes(collectionIndex);

        return this;

    }

    /**
     *
     * This function will fetch all defined indexes from the annotated class
     * The returned info will then be applied to mongo
     * @param collectionClass
     *
     */
    private List<CollectionIndex> getCollectionIndexes(Class collectionClass){

        // Get our Annotation and type check
        Annotation ano = collectionClass.getAnnotation(CollectionDefinition.class);
        // Check annotation is instance of ProviderTypeAnnotation.class
        HashMap<String, String> collectIndex = new HashMap<String, String>();

        List<CollectionIndex> indexList = new ArrayList<>();

        if (ano instanceof CollectionDefinition) {


            Field[] fields = collectionClass.getDeclaredFields();

            for (Field field : fields) {

                UniqueIndex unique = field.getAnnotation(UniqueIndex.class);

                if (unique != null) {

                    CollectionIndex colIndex = new CollectionIndex();
                    colIndex.setIndexField(field.getName());
                    colIndex.setIndexClazz(field.getType().toString());
                    colIndex.setIndexName(unique.indexName());

                    System.out.println( jsonParser.toJson( colIndex ) );


                    indexList.add( colIndex );

                    collectIndex.put(field.getName(), unique.indexName());
                }

            }

        }

        return indexList;
    }

    /**
     *
     * Get all fields defined within an entity
     * @param collectionClass
     *
     */
    private List<CollectionField> getCollectionFields(Class collectionClass){

        // Get our Annotation and type check
        Annotation ano = collectionClass.getAnnotation(CollectionDefinition.class);
        // Check annotation is instance of ProviderTypeAnnotation.class
        HashMap<String, String> collectIndex = new HashMap<String, String>();

        List<CollectionField> fieldList = new ArrayList<>();

        if (ano instanceof CollectionDefinition) {

            CollectionDefinition myAnnotation = (CollectionDefinition) ano;
            Method[] methods = collectionClass.getDeclaredMethods();
            Field[] fields = collectionClass.getDeclaredFields();

            for (Field field : fields) {

                DocumentField documentField = field.getAnnotation(DocumentField.class);
                LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);

                if (documentField != null) {

                    CollectionField collectionField = new CollectionField();
                    collectionField.setFieldClazz(field.getType().toString());
                    collectionField.setFieldName( field.getName());
                    collectionField.setFieldRequired( documentField.required() );

                    if(linkedCollection != null){
                        collectionField.setFieldinternal(false);
                    }

                    fieldList.add(collectionField);

                    System.out.println( jsonParser.toJson( collectionField ) );

                }

            }

        }
        return fieldList;

    }


    /**
     *
     *
     */
    private void createBulkUniqueIndexes(HashMap<String, HashMap<String, String>> indexMap) {

        // Iterate the collection annotations set
        for (Map.Entry<String, HashMap<String, String>> entry : indexMap.entrySet()) {
            String key = entry.getKey();
            HashMap<String, String> value = entry.getValue();

            for (Map.Entry<String, String> index : value.entrySet()) {
                String field = index.getKey();
                String indexName = index.getValue();

                mongoClient.runCommand("createIndexes",
                    new JsonObject()
                            .put("createIndexes", key)
                            .put("indexes", new JsonArray()
                                            .add(
                                                    new JsonObject()
                                                            .put("name", indexName)
                                                            .put("key",
                                                                    new JsonObject().put(field, 1)

                                                            ).put("unique", true)
                                                            .put("sparse", true)
                                                    .put("expireAfterSeconds",60)
                                            )
                            ),
                    cr -> {
                        if (cr.succeeded()) {
                            JsonObject result = cr.result();
                            logger.info("Collection : " + key);
                            logger.info("DocumentField : " + field);
                            logger.info("IndexName : " + indexName);

                            logger.info("CreateIndexes succeeded result >" + result.encodePrettily());
                        } else {
                            logger.warn("CreateIndexes failed", cr.cause());
                        }
                    });
            }
        }

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
     * @param base
     * @param item
     * @param sub
     * @param subId
     * @param resultHandler
     */
    public void linkDocument(Class base, String item, Class sub , String subId ,Handler<AsyncResult<String>> resultHandler){

        Collection collectionBase = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(base)).findFirst().get();

        if( !item.getClass().equals(base) )  resultHandler.handle(Future.failedFuture("Base document doesnt match"));;

        Collection collectionSub = mongiDb.getDatabaseCollections().stream().filter(x -> x.getCollectionClazz().equals(sub)).findFirst().get();

        if( !item.getClass().equals(base) )  resultHandler.handle(Future.failedFuture("Sub document doesnt match"));

        boolean doesExist = false;

        System.out.println(base.getCanonicalName());
        System.out.println(Json.encode( base.getFields() ));

        for (Field field : base.getFields()) {

            System.out.println("===================================");
            System.out.println("HERE ARE OUR FIELDS");
            System.out.println(field.getName());
            LinkedCollection linkedCollection = field.getAnnotation(LinkedCollection.class);
            if( linkedCollection !=null ){
                if(linkedCollection.linkedCollection().equals(sub)) doesExist = true;
            }

        }

        if(!doesExist){
            System.out.println("===================================");
            System.out.println("CLAZZ NOT FOUND");
            System.out.println("Base clazz : " + base.getCanonicalName());
            System.out.println("SUB clazz : " + sub.getCanonicalName());
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

    /**
     *
     *
     */
    private void createSigularIndex() {

    }

    /**
     *
     *
     */
    public void listCollectionIndexes() {

        /**
        mongoClient.runCommand("getIndexes",
            new JsonObject(),
            cr -> {
                if (cr.succeeded()) {
                    JsonObject result = cr.result();
                    logger.info("CreateIndexes succeeded result >" + result.encodePrettily());
                } else {
                    logger.warn("CreateIndexes failed", cr.cause());
                }
            });
         **/

    }

}