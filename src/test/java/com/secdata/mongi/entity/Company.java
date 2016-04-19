package com.secdata.mongi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.secdata.mongi.annotation.CollectionDefinition;
import com.secdata.mongi.annotation.DocumentField;
import com.secdata.mongi.annotation.TTLIndex;
import com.secdata.mongi.annotation.UniqueIndex;

import java.util.Date;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_company_collection"
)
public class Company {

    @Expose(serialize = false, deserialize = true)
    private String _id;

    @Expose
    @DocumentField
    @UniqueIndex(indexName = "name_unique_index")
    private String name;

    @Expose
    @DocumentField
    @UniqueIndex(indexName = "dob_unique_index")
    private Date dob;

    @Expose
    @DocumentField
    @UniqueIndex(indexName = "height_unique_index")
    private String height;

    /**
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
     **/

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