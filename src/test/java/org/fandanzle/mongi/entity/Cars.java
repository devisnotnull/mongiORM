package org.fandanzle.mongi.entity;


import com.google.gson.annotations.Expose;
import org.fandanzle.mongi.annotation.*;

import java.util.UUID;

/**
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 */
@CollectionDefinition(
        collectionName = "test_cars_collection"
)
public class Cars {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @Expose
    @DocumentField
    @UniqueIndex(
            indexName = "car_numberplate_name_unique_index"
    )
    private String numberplate;

    @Expose
    @DocumentField(
            required = true
    )
    private String vinNumber;

    @Expose
    @DocumentField
    private String color;

    public UUID get_id() {
        return _id;
    }

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