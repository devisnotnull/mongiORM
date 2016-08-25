package com.stump201.mongi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //can use in method only.
public @interface UniqueIndex {

    //should ignore this test?
    boolean ensureUniqueIndex() default true;
    String indexName();

}