package org.fandanzle.mongi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import org.fandanzle.mongi.annotation.CollectionDefinition;
import org.fandanzle.mongi.annotation.DocumentField;
import org.fandanzle.mongi.annotation.TTLIndex;
import org.fandanzle.mongi.annotation.UniqueIndex;
import java.util.Date;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_company_collection"
)
public class Company {

    // You need to declare a class varible as an @DocumentFiel
    @DocumentField(required = true)
    private String _id;

    @DocumentField(required = true)
    @UniqueIndex(indexName = "name_unique_index")
    private String name;

    @DocumentField(required = true)
    @UniqueIndex(indexName = "dob_unique_index")
    private Date dob;

    @DocumentField(required = true)
    @UniqueIndex(indexName = "height_unique_index")
    private String height;

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