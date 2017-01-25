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
public class Group {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @Expose
    @DocumentField
    @UniqueIndex(
            indexName = "group_name_unique_index"
    )
    private String name;

    @Expose
    @DocumentField
    private String description;

    @DocumentField
    @Reference(linkedCollection = Group.class)
    private Set<Scope> scopes = new HashSet<>();

    public UUID get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Group setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public Group setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
        return this;
    }
}