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
public class Phones {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @Expose
    @DocumentField
    @UniqueIndex(
            indexName = "phone_imei_unique_index"
    )
    private String imei;

    @Expose
    @DocumentField
    private String make;

    @Expose
    @DocumentField
    private String color;

    public UUID get_id() {
        return _id;
    }

    public Phones set_id(UUID _id) {
        this._id = _id;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public Phones setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getMake() {
        return make;
    }

    public Phones setMake(String make) {
        this.make = make;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Phones setColor(String color) {
        this.color = color;
        return this;
    }
}

