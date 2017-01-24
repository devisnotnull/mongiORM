package org.fandanzle.mongi.vertx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import io.vertx.core.cli.converters.BooleanConverter;
import org.fandanzle.mongi.IMongi;
import org.fandanzle.mongi.annotation.*;

import org.fandanzle.mongi.entity.Collection;
import org.fandanzle.mongi.entity.CollectionField;
import org.fandanzle.mongi.entity.CollectionIndex;
import org.fandanzle.mongi.entity.Database;
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
import java.time.LocalTime;
import java.util.*;

/**
 *
 * Created by alexb on 11/02/2016.
 *
 */
public class Mongi implements IMongi {

    private static Logger logger = Logger.getLogger(Mongi.class);

    // Boolean check, If set to true the schema will dropped and rebuild everytime
    // an new instance of Mongi is created
    private Boolean rebuildOnRun = false;

    //
    private final Set<Class> grantedClazz = new HashSet<>();

    // List of type adapters for GSON
    // Add in jackson equiv
    private HashMap<Class, TypeAdapter> registeredTypeAdapters = new HashMap<>();

    //
    private Gson jsonParser = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    //
    private MongoClient mongoClient;

    //
    private Database mongiDb = new Database();

    public Vertx vertx = null;

    /**
     *
     * @param vertx
     */
    public Mongi(Vertx vertx) {

        this.vertx = vertx;

        /*
         * We need a list of classes, These will need to be linked to TypeAdapters
         */
        Class[] allow = {
                String.class,
                Integer.class,
                Boolean.class,
                Double.class,
                Date.class,
                Calendar.class,
                LocalTime.class
        };

        mongoClient = MongoClient.createShared(vertx, new JsonObject());

    }

    /**
     *
     * @param vertx
     * @param config
     */
    public Mongi(Vertx vertx, JsonObject config) {
        mongoClient = MongoClient.createShared(vertx, config);
    }

    /**
     * Fetch an instance of the mongo client
     * @return
     */
    public MongoClient getMongoClient(){
        return mongoClient;
    }

    /**
     * Get the current mongodatabase
     * @return
     */
    public Database getMongoDatabase(){
        return mongiDb;
    }

    /**
     *
     * @param adapter
     */
    public void registerTypeAdapter(Class clazz, TypeAdapter adapter){

    }

    /**
     * Rebuild solution on rebuild
     * @param rebuild
     * @return
     */
    public Mongi setRebuild(Boolean rebuild){
        rebuildOnRun = rebuild;
        return this;
    }

    /**
     *
     *
     * This function takes a package name to scan and profiles the schema to insert and the indexs to ensure.
     *
     * @param packageName
     * @return
     */
    public Mongi buildOrmSolution(String packageName) {

        logger.info("Building ORM Solution !");
        // Store our collections
        List<Collection> collectionsList = new ArrayList<>();
        // HashMap to store all indexes
        HashMap<String, HashMap<String, String>> collectionIndex = new HashMap<String, HashMap<String, String>>();
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

                    // Create base object for the collection
                    // Used to generate the base
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

                try{

                    // Fields
                    HashMap<String, Field> fieldsColl = new HashMap<>();
                    //HashMap<String, >

                    /**
                     *

                     Implement SPI interface to dynamically load in new modules for annotations

                     How to store Type adapters

                     */
                    /**
                     [
                        {
                            'name': 'cars',
                            'clazz': 'com.secdata.mongi.entity.Cars.class'
                            'idField' : '_id',
                            'unique' : [
                            ],
                            'fields': [
                                '_id' : 'String',
                                'vinNumber' : 'Integer',

                            ]
                        }
                     ]

                     */

                    // Fetch the individual methods from collection
                    //
                    HashMap<String,String> collectIndex = new HashMap<>();
                    CollectionDefinition myAnnotation = (CollectionDefinition) collectionDefinition;
                    Method[] methods = ii.getDeclaredMethods();
                    Field[] fields = ii.getDeclaredFields();

                    // Iterate all the base fields withing the class
                    logger.info("Class fields : ");
                    for(Field field : fields){

                        logger.info("Field to process - " + field.getName());
                        // Add annotations to array
                        Annotation[] fieldAnnotations = field.getAnnotations();
                        //
                        for( int i = 0; i < fieldAnnotations.length - 1; i++)
                        {
                            //
                            String clazz = fieldAnnotations[i].annotationType().getClass().getCanonicalName();
                            logger.info("=============================");
                            logger.info("Processing new annotation");
                            logger.info("=============================");
                            logger.info(clazz);

                        }

                        /*
                         * Document field
                         */
                        DocumentField documentField = field.getAnnotation(DocumentField.class);
                        if(documentField != null){

                            if(!documentField.name().equals("")){
                                fieldsColl.putIfAbsent(documentField.name(), field);
                            }else{
                                fieldsColl.put(field.getName(), field);
                            }

                        }

                        /*
                         * Unique index handler
                         */
                        UniqueIndex unique = field.getAnnotation(UniqueIndex.class);
                        if(unique != null){

                            logger.info("Index to process");
                            logger.info(field.getName());
                            logger.info(unique.indexName());
                            collectIndex.put(field.getName() , unique.indexName());

                        }

                        /*
                         * References handler
                         */
                        Reference reference = field.getAnnotation(Reference.class);
                        if(reference != null){

                            logger.info("Referenced field to process");

                        }


                        // Add collection to the list
                        collectionIndex.put(myAnnotation.collectionName(), collectIndex);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            System.out.println("Finished processing entity " + ii.getCanonicalName());
        }

        database.setDatabaseCollections(mappedCollections);
        database.setDatabaseEntities(definedClass);
        mongiDb = database;

        // Iterate the collection annotations set
        createBulkUniqueIndexes(collectionIndex);

        return this;

    }

    /**
     *
     * This function will fetch all defined indexes from the annotated class
     * The returned info will then be applied to mongo
     * @param collectionClass
     *
     */
    public List<CollectionIndex> getCollectionIndexes(Class collectionClass){

        logger.info("Fetching Collection indexes + " + collectionClass);
        // Get our Annotation and type check
        Annotation ano = collectionClass.getAnnotation(CollectionDefinition.class);
        // Check annotation is instance of ProviderTypeAnnotation.class
        HashMap<String, String> collectIndex = new HashMap<String, String>();
        //
        List<CollectionIndex> indexList = new ArrayList<>();
        //
        logger.info("Collection Definition " + ano);
        logger.info("=============================");

        if (ano instanceof CollectionDefinition) {

            Field[] fields = collectionClass.getDeclaredFields();

            for (Field field : fields) {

                UniqueIndex unique = field.getAnnotation(UniqueIndex.class);

                if (unique != null) {

                    CollectionIndex colIndex = new CollectionIndex();
                    colIndex.setIndexField(field.getName());
                    colIndex.setIndexClazz(field.getType().toString());
                    colIndex.setIndexName(unique.indexName());
                    logger.info( jsonParser.toJson( colIndex ) );
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
    public List<CollectionField> getCollectionFields(Class collectionClass){

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

                }

            }

        }
        return fieldList;

    }


    /**
     * Create all of our bulk indexes
     * @param indexMap
     */
    public void createBulkUniqueIndexes(HashMap<String, HashMap<String, String>> indexMap) {

        for (Map.Entry<String, HashMap<String, String>> entry : indexMap.entrySet()) {
            String key = entry.getKey();
            HashMap<String, String> value = entry.getValue();

            System.out.println("=========================================");
            System.out.println("Bulk Collection index " + key);
            System.out.println("=========================================");

            for (Map.Entry<String, String> index : value.entrySet()) {
                String field = index.getKey();
                String indexName = index.getValue();
        
                // Create our indexes using pass through commands
                mongoClient.runCommand("createIndexes",
                    new JsonObject()
                            .put("createIndexes", key)
                            .put("indexes", new JsonArray()
                                .add(
                                    new JsonObject()
                                        .put("name", indexName)
                                        .put("key", new JsonObject().put(field, 1))
                                        .put("unique", true)
                                        .put("sparse", true)
                                    )
                            ),
                    cr -> {

                        System.out.println(
                                "Creating a new index !!!!"
                        );

                        System.out.print(
                                Json.encodePrettily(
                                        new JsonObject()
                                                .put("createIndexes", key)
                                                .put("indexes", new JsonArray()
                                                                .add(
                                                                        new JsonObject()
                                                                                .put("name", indexName)
                                                                                .put("key", new JsonObject().put(field, 1))
                                                                                .put("unique", true)
                                                                                .put("sparse", true)
                                                                )
                                                )
                                )
                        );

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
     *   Get all collection indexes
     */
    public void listCollectionIndexes() {

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

    }

}