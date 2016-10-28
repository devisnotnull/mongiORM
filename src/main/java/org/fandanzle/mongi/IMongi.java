package org.fandanzle.mongi;

import org.fandanzle.mongi.entity.CollectionField;
import org.fandanzle.mongi.entity.CollectionIndex;
import org.fandanzle.mongi.entity.Database;

import java.util.*;

/**
 * Created by alexb on 19/04/2016.
 */
public interface IMongi {

    /**
     *
     * @return
     */
    Database getMongoDatabase();

    /**
     *
     * @param rebuild
     * @return
     */
    IMongi setRebuild(Boolean rebuild);

    /**
     *
     * @param packageName
     * @return
     */
    IMongi buildOrmSolution(String packageName);

    /**
     *
     * This function will fetch all defined indexes from the annotated class
     * The returned info will then be applied to mongo
     * @param collectionClass
     */
     List<CollectionIndex> getCollectionIndexes(Class collectionClass);

    /**
     *
     * Get all fields defined within an entity
     * @param collectionClass
     */
    List<CollectionField> getCollectionFields(Class collectionClass);

    /**
     * Create all of our bulk indexes
     * @param indexMap
     */
     void createBulkUniqueIndexes(HashMap<String, HashMap<String, String>> indexMap);

    /**
     *
     *   Get all collection indexes
     */
     void listCollectionIndexes();

}
