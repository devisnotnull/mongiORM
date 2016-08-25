package com.stump201.mongi.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexb on 15/04/2016.
 */
public class Database {

    @Expose
    private String databaseName = "";

    @Expose
    private List<Collection> databaseCollections = new ArrayList();

    private List<Class> databaseEntities = new ArrayList<>();

    public List<Class> getDatabaseEntities() {
        return databaseEntities;
    }

    public void setDatabaseEntities(List<Class> databaseEntities) {
        this.databaseEntities = databaseEntities;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<Collection> getDatabaseCollections() {
        return databaseCollections;
    }

    public void setDatabaseCollections(List<Collection> databaseCollections) {
        this.databaseCollections = databaseCollections;
    }
}
