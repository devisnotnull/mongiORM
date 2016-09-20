package org.fandanzle.mongi.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexb on 15/04/2016.
 */
public class CollectionIndex {

    @Expose
    private String indexField;

    @Expose
    private String indexName;

    @Expose
    private String indexClazz;

    public String getIndexField() {
        return indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField = indexField;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexClazz() {
        return indexClazz;
    }

    public void setIndexClazz(String indexClazz) {
        this.indexClazz = indexClazz;
    }
}
