package com.secdata.mongi.entity;

import com.google.gson.annotations.Expose;
import com.secdata.mongi.annotation.*;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_cars_collection"
)
public class Cars {


    @Expose(serialize = false, deserialize = true)
    private String _id;

    @Expose
    @DocumentField
    @UniqueIndex(indexName = "car_numberplate_name_unique_index")
    private String numberplate;

    @Expose
    @DocumentField(required = true)
    private String vinNumber;

    @Expose
    @DocumentField
    private String color;


    public String getNumberplate() {
        return numberplate;
    }

    public void setNumberplate(String numberplate) {
        this.numberplate = numberplate;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}