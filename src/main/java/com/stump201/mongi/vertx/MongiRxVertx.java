package com.stump201.mongi.vertx;

import com.stump201.mongi.annotation.CollectionDefinition;
import com.stump201.mongi.annotation.UniqueIndex;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import rx.Observable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by alexb on 11/02/2016.
 */
public class MongiRxVertx {

    private static Logger logger = Logger.getLogger(MongiRxVertx.class);

    private MongoClient mongoClient;

    /**
     *
     * @param vertx
     */
    public MongiRxVertx(Vertx vertx) {
        mongoClient = MongoClient.createShared(vertx, new JsonObject());
    }

    /**
     *
     * @param vertx
     * @param config
     */
    public MongiRxVertx(Vertx vertx , JsonObject config) {
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
    public MongiRxVertx buildOrmSolution(String packageName) {

        HashMap<String, HashMap<String, String>> collectionIndex = new HashMap<String, HashMap<String, String>>();
        // TODO create IDP providers and store on verticle creation
        // Hashmap to store IDP providers
        // Java reflections, We loads the IDP providers via generics
        Reflections reflections = new Reflections(packageName);
        // Fetch all classes that have the ProviderTypeAnnotation.class annotation
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CollectionDefinition.class);
        // Iterate
        for (Class ii : annotated) {
            logger.info(ii.getCanonicalName());
            // Get our Annotation and type check
            Annotation ano = ii.getAnnotation(CollectionDefinition.class);
            // Check annotation is instance of ProviderTypeAnnotation.class
            if (ano instanceof CollectionDefinition) {

                HashMap<String, String> collectIndex = new HashMap<String, String>();
                CollectionDefinition myAnnotation = (CollectionDefinition) ano;
                Method[] methods = ii.getDeclaredMethods();
                Field[] fields = ii.getDeclaredFields();

                logger.info("Class fields : ");
                for (Field field : fields) {
                    logger.info(field.getName());
                    UniqueIndex unique = field.getAnnotation(UniqueIndex.class);
                    if (unique != null) {
                        logger.info("Index to process");
                        logger.info(field.getName());
                        logger.info(unique.indexName());
                        collectIndex.put(field.getName(), unique.indexName());
                    }
                    collectionIndex.put(myAnnotation.collectionName(), collectIndex);
                }
            }
        }


        // Iterate the collection annotations set
        createBulkIndexes(collectionIndex);

        return this;

    }

    /**
     *
     *
     */
    private void createBulkIndexes(HashMap<String, HashMap<String, String>> indexMap) {

        List<Observable<JsonObject>> indexListObs  = new ArrayList<>();

        // Iterate the collection annotations set
        for (Map.Entry<String, HashMap<String, String>> entry : indexMap.entrySet()) {
            String collection  = entry.getKey();
            HashMap<String, String> value = entry.getValue();

            for (Map.Entry<String, String> index : value.entrySet()) {
                String field = index.getKey();
                String indexName = index.getValue();

                // Construct json object
                JsonObject indexJson =
                        new JsonObject()
                            .put("createIndexes", collection)
                            .put("indexes", new JsonArray()
                                            .add(
                                                    new JsonObject()
                                                            .put("name", indexName)
                                                            .put("key",
                                                                    new JsonObject().put(field, 1)

                                                            ).put("unique", true)
                                                            .put("sparse", true)
                                                            .put("expireAfterSeconds", 60)

                                            )
                            );

                Observable<JsonObject> json = mongoClient.runCommandObservable("createIndexes", indexJson);
                indexListObs.add(json);

            }
        }

        Observable.from(indexListObs).subscribe(urls -> {


        });


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