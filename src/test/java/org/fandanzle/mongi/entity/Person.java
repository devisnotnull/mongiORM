package org.fandanzle.mongi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import org.fandanzle.mongi.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_person_collection"
)
public class Person {

    @Id(indexName = "_id")
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

    @DocumentField
    @LinkedCollection(linkedCollection = Cars.class)
    private List<String> cars = new ArrayList<>();

    public List<String> getCars() {
        return cars;
    }

    public void setCars(List<String> cars) {
        this.cars = cars;
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