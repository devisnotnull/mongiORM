package com.stump201.mongi.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by alexb on 15/04/2016.
 */
public class CollectionField {

    @Expose
    public boolean fieldinternal = true;

    @Expose
    public Class linkedFieldClass;

    @Expose
    private String fieldName;

    @Expose
    private boolean fieldRequired;

    @Expose
    private String fieldClazz;

    public boolean isFieldinternal() {
        return fieldinternal;
    }

    public void setFieldinternal(boolean fieldinternal) {
        this.fieldinternal = fieldinternal;
    }

    public Class getLinkedFieldClass() {
        return linkedFieldClass;
    }

    public void setLinkedFieldClass(Class linkedFieldClass) {
        this.linkedFieldClass = linkedFieldClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isFieldRequired() {
        return fieldRequired;
    }

    public void setFieldRequired(boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    public String getFieldClazz() {
        return fieldClazz;
    }

    public void setFieldClazz(String fieldClazz) {
        this.fieldClazz = fieldClazz;
    }
}
