package org.fandanzle.mongi.entity;


import com.google.gson.annotations.Expose;
import org.fandanzle.mongi.annotation.CollectionDefinition;
import org.fandanzle.mongi.annotation.DocumentField;
import org.fandanzle.mongi.annotation.Id;
import org.fandanzle.mongi.annotation.UniqueIndex;

import java.util.UUID;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_cars_collection"
)
public class Resource {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @Expose
    @DocumentField
    @UniqueIndex(
            indexName = "resource_name_unique_index"
    )
    private String name;

    @Expose
    @DocumentField
    private String url;

    @Expose
    @DocumentField
    private String description;

    public UUID get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Resource setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Resource setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Resource setDescription(String description) {
        this.description = description;
        return this;
    }
}