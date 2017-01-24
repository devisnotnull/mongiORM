package org.fandanzle.mongi.entity;

import org.fandanzle.mongi.annotation.*;

import java.util.*;

/**
 *
 * Java pojo class for clients
 * Created by alexb on 18/11/2015.
 *
 */
@CollectionDefinition(
        collectionName = "test_person_collection"
)
public class Person {

    @Id(indexName = "_id")
    private UUID _id = UUID.randomUUID();

    @DocumentField(
            expose = true
    )
    @UniqueIndex(indexName = "name_unique_index")
    private String name;

    @DocumentField(
            expose = true
    )
    private Date dob;

    @DocumentField
    private String height;

    @DocumentField
    @Embedded(linkedCollection = Cars.class)
    private Set<Cars> carsEmbed = new HashSet<>();

    @DocumentField
    @Reference(linkedCollection = Cars.class)
    private Set<Cars> carsReference = new HashSet<>();

    @DocumentField
    @Embedded(linkedCollection = Phones.class)
    private Set<Phones> phonesEmbed = new HashSet<>();

    @DocumentField
    @Reference(linkedCollection = Phones.class)
    private Set<Phones> phonesReference = new HashSet<>();

    public UUID get_id() {
        return _id;
    }

    public Set<Phones> getPhonesEmbed() {
        return phonesEmbed;
    }

    public Person setPhonesEmbed(Set<Phones> phonesEmbed) {
        this.phonesEmbed = phonesEmbed;
        return this;
    }

    public Set<Phones> getPhonesReference() {
        return phonesReference;
    }

    public Person setPhonesReference(Set<Phones> phonesReference) {
        this.phonesReference = phonesReference;
        return this;
    }

    public Set<Cars> getCarsEmbed() {
        return carsEmbed;
    }

    public Person setCarsEmbed(Set<Cars> carsEmbed) {
        this.carsEmbed = carsEmbed;
        return this;
    }

    public Set<Cars> getCarsReference() {
        return carsReference;
    }

    public Person setCarsReference(Set<Cars> carsReference) {
        this.carsReference = carsReference;
        return this;
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