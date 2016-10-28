package org.fandanzle.mongi;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Pass in type of return,
 * Used to handle call backs and promises
 */
public interface IQuery<T> {


    IQuery findOne(Class clazz, Object id, T hadler);

    IQuery query(Class clazz, JsonObject object, T hadler);

    IQuery insert(Class clazz, Object object, T asyncResultHandler);
    IQuery insertBulk(Class clazz, List<?> T, T asyncResultHandler);

    IQuery update(Class clazz, Object id, Object object, T asyncResultHandler);

    IQuery delete(Class clazz, Object object, T asyncResultHandler);
    IQuery deleteBulk(Class clazz, Object object, T asyncResultHandler);


}
