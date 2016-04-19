package com.secdata.mongi.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexb on 15/04/2016.
 */
public class Collection {

    @Expose
    private String collectionName;

    @Expose
    private String collectionPrimaryId;

    @Expose
    private List<CollectionField> collectionFields = new ArrayList<>();

    @Expose
    private List<CollectionIndex> collectionIndexes = new ArrayList<>();

    @Expose String collectionClass;

    private Class collectionClazz;

    public String getCollectionClass() {
        return collectionClass;
    }

    public void setCollectionClass(String collectionClass) {
        this.collectionClass = collectionClass;
    }

    public Class getCollectionClazz() {
        return collectionClazz;
    }

    public void setCollectionClazz(Class collectionClazz) {
        this.collectionClazz = collectionClazz;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionPrimaryId() {
        return collectionPrimaryId;
    }

    public void setCollectionPrimaryId(String collectionPrimaryId) {
        this.collectionPrimaryId = collectionPrimaryId;
    }

    public List<CollectionField> getCollectionRows() {
        return collectionFields;
    }

    public void setCollectionRows(List<CollectionField> collectionRows) {
        this.collectionFields = collectionRows;
    }

    public List<CollectionField> getCollectionFields() {
        return collectionFields;
    }

    public void setCollectionFields(List<CollectionField> collectionFields) {
        this.collectionFields = collectionFields;
    }

    public List<CollectionIndex> getCollectionIndexes() {
        return collectionIndexes;
    }

    public void setCollectionIndexes(List<CollectionIndex> collectionIndexes) {
        this.collectionIndexes = collectionIndexes;
    }
}

