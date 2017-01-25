package org.fandanzle.mongi.entity;


import com.google.gson.annotations.Expose;
import org.fandanzle.mongi.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_scope_collection"
)
public class Scope {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @Expose
    @DocumentField
    @UniqueIndex(
            indexName = "scope_name_unique_index"
    )
    private String name;

    @Expose
    @DocumentField
    private String description;

    @DocumentField
    @Reference(linkedCollection = Scope.class)
    private Set<Scope> composites = new HashSet<>();

    public UUID get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Scope setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Scope setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<Scope> getComposites() {
        return composites;
    }

    public Scope setComposites(Set<Scope> composites) {
        this.composites = composites;
        return this;
    }

}