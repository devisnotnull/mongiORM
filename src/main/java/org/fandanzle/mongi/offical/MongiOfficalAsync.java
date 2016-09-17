package com.stump201.mongi.offical;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.async.client.*;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.connection.ClusterSettings;
import com.stump201.mongi.annotation.CollectionDefinition;
import com.stump201.mongi.annotation.UniqueIndex;
import io.vertx.core.json.Json;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexb on 11/02/2016.
 */
public class MongiOfficalAsync {

    private static Logger logger = Logger.getLogger(MongiOfficalAsync.class);

    private MongoClient mongoClient;
    private String database;
    private Integer port;

    /**
     *
     * @param db
     * @param host
     */
    public MongiOfficalAsync(String db , String host , Integer pt){

        port = pt;
        database = db;
        mongoClient = MongoClients.create( "mongodb://" + host );

    }

    /**
     *
     * @param db
     * @param clusterSettings
     */
    public MongiOfficalAsync(String db , ClusterSettings clusterSettings){

        database = db;
        MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();
        mongoClient = MongoClients.create(settings);

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
     * @param packageName
     * @return
     */
    public MongiOfficalAsync buildOrmSolution(String packageName){

        HashMap<String, HashMap<String,String>> collectionIndex = new HashMap<String, HashMap<String, String>>();
        // TODO create IDP providers and store on verticle creation
        // Hashmap to store IDP providers
        // Java reflections, We loads the IDP providers via generics
        Reflections reflections = new Reflections(packageName);
        // Fetch all classes that have the ProviderTypeAnnotation.class annotation
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith( CollectionDefinition.class
        );
        // Iterate
        for(Class ii : annotated) {
            logger.info(ii.getCanonicalName());
            // Get our Annotation and type check
            Annotation ano = ii.getAnnotation(CollectionDefinition.class);
            // Check annotation is instance of ProviderTypeAnnotation.class
            if (ano instanceof CollectionDefinition) {

                HashMap<String,String> collectIndex = new HashMap<String, String>();
                CollectionDefinition myAnnotation = (CollectionDefinition) ano;
                Method[] methods = ii.getDeclaredMethods();
                Field[] fields = ii.getDeclaredFields();

                logger.info("Class fields : ");
                for(Field field : fields){
                    logger.info(field.getName());
                    UniqueIndex unique = field.getAnnotation(UniqueIndex.class);
                    if(unique != null){
                        logger.info("Index to process");
                        logger.info(field.getName());
                        logger.info(unique.indexName());
                        collectIndex.put(field.getName() , unique.indexName());
                    }
                    collectionIndex.put(myAnnotation.collectionName(), collectIndex);
                }
            }
        }

        // Iterate the collection annotations set
        createBulkIndexes( collectionIndex );

        return this;

    }

    /**
     *
     *
     */
    private void createBulkIndexes( HashMap<String, HashMap<String,String>> indexMap){

        Gson gson = new GsonBuilder().serializeNulls().create();

        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

        List<IndexModel> indexModels = new ArrayList<>();

        // Iterate the collection annotations set
        for (Map.Entry<String, HashMap<String,String>> entry : indexMap.entrySet() ) {

            String collection  = entry.getKey();
            HashMap<String,String> value = entry.getValue();

            mongoDatabase.createCollection(collection , ( ss, ws )-> {

            });

            for(Map.Entry<String,String> index : value.entrySet()){

                String field = index.getKey();
                String indexName = index.getValue();

                Document indexDocument = new Document().append(field, 1);
                IndexOptions indexOptions = new IndexOptions().unique(true).name(indexName).expireAfter(60L, TimeUnit.SECONDS);

                mongoDatabase.getCollection( collection ).createIndex( indexDocument , indexOptions , (ss, uu) -> {

                    System.out.println("Adding new index ======");
                    System.out.println(gson.toJson(ss));
                    uu.printStackTrace();

                });

                ListIndexesIterable<Document> iter = mongoDatabase.getCollection(collection).listIndexes();
                iter.batchCursor( (result, ee ) -> {
                    if (ee != null) {
                        ee.printStackTrace();
                    }
                    result.next((rr,re) -> {
                        System.out.println("Index Result");
                        System.out.println("=================================");
                        System.out.println(Json.encode(rr));
                    });
                });


                System.out.println(Json.encode(indexDocument));
                System.out.println("INDEX LIST");
            }

        }

    }

    /**
     *
     *
     */
    private void createSigularIndex(){

    }

    /**
     *
     *
     */
    public void listCollectionIndexes(){

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
