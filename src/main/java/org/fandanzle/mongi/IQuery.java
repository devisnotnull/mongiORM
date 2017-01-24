package org.fandanzle.mongi;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Pass in type of return,
 * Used to handle call backs and promises
 */
public interface IQuery {

    <T> IQuery findOne(T clazz, Object id, Handler<AsyncResult<T>> handler);
    <T> IQuery insert(Class<T> clazz, Object object, Handler<AsyncResult<String>> handler);

    /**
    <T> IQuery query(Class clazz, JsonObject object, R hadler);
    <T> IQuery insert(Class clazz, Object object, R asyncResultHandler);
    <T> IQuery insertBulk(Class clazz, List<?> T, R asyncResultHandler);
    <T> IQuery update(Class clazz, Object id, Object object, R asyncResultHandler);
    <T> IQuery delete(Class clazz, Object object, R asyncResultHandler);
    <T> IQuery deleteBulk(Class clazz, Object object, R asyncResultHandler);
     **/

}
