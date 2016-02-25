package com.secdata.mongi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.secdata.mongi.CollectionDefinition;
import com.secdata.mongi.UniqueIndex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_collection"
)
public class Person {

    @Since(1.0)
    @Expose(serialize = false, deserialize = true)
    private String _id;

    @Since(1.0)
    @Expose
    @UniqueIndex(indexName = "name_unique_index")
    private String name;

    @Since(1.0)
    @Expose
    @UniqueIndex(indexName = "dob_unique_index")
    private Date dob;

    @Since(1.0)
    @Expose
    @UniqueIndex(indexName = "height_unique_index")
    private String height;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}