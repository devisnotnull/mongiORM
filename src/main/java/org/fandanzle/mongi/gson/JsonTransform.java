package org.fandanzle.mongi.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * http://howtodoinjava.com/best-practices/google-gson-tutorial-convert-java-object-to-from-json/
 *
 * Created by alexb on 17/01/2017.
 */
public class JsonTransform {

    private static Logger logger = Logger.getLogger(JsonTransform.class);

    public static Gson builder = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     *
     * @return
     */
    public static Gson builder(){
        return builder;
    }


    /**
     * Clazz
     * @param s
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> stringToArray(Class<T[]> clazz, String s) {
        T[] arr = builder.fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    /**
     * Clazz
     * @param s
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToObject(Class<T> clazz,String s) {
        return (T) builder.fromJson(s, clazz);
    }

    /**
     * Transform JSONOBJECT into array
     * @param type
     * @param json
     * @param
     * @return
     */
    public static <T> T[] jsonToArr(Class<T[]> type, List<JsonObject> json){

        try {
            T[] dd = JsonTransform.builder.fromJson(
                    Json.encodePrettily( json ),
                    type
            );
            return dd;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Transform JSONOBJECT into list
     * @param type
     * @param json
     * @param
     * @return
     */
    public static <T> List<T> jsonToList(Class<T[]> type, List<JsonObject> json){

        try {
            return Arrays.asList(
                    JsonTransform.builder.fromJson(
                            Json.encodePrettily( json ),
                            type
                    )
            );

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Transform JSON into list and use async handlers
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> void jsonToList(Class<T[]> type, String json, Handler<AsyncResult<List<T>>> resultHandler){
        try {
            T[] parseList = builder.fromJson(json, type);
            resultHandler.handle(Future.succeededFuture(Arrays.asList(parseList)));
        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }

    /**
     * Transform JSONObject into list and use async handlers
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> void jsonToList(Class<T[]> type, List<JsonObject> json, Handler<AsyncResult<List<T>>> resultHandler){
        try {
            T[] parseList = builder.fromJson( Json.encode(json) ,type);
            resultHandler.handle(Future.succeededFuture(Arrays.asList(parseList)));

        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }


    /**
     * Take a list and transform into a string
     * @param json
     * @param resultHandler
     */
    public static <T> void listToJson(Class<T[]> type, List<T> json, Handler<AsyncResult<String>> resultHandler){
        try {
            String decode = builder.toJson(json, type);
            resultHandler.handle(Future.succeededFuture(decode));

        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }


    /**
     * Take a list and transform into a JsonObject
     * @param json
     * @param resultHandler
     */
    public static <T> void listToJsonObject(T type, List<T> json, Handler<AsyncResult<JsonObject>> resultHandler){
        try {
            Type listType = new TypeToken<List<T>>() {}.getType();
            String decode = builder.toJson(json, listType);
            resultHandler.handle(Future.succeededFuture( new JsonObject(decode) ) );

        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }


    /**
     * Transform JSONOBJECT into class
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> void jsonToObject(T type, JsonObject json, Handler<AsyncResult<T>> resultHandler){
        try{
            Type listType = new TypeToken<T>() {}.getType();
            resultHandler.handle(Future.succeededFuture(builder.fromJson(json.encodePrettily(), listType)));
        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }


    /**
     * Transform JsonObject into a java POJO
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(Class<T> type, JsonObject json) throws Exception{
        return builder.fromJson( json.encodePrettily(),type);
    }


    /**
     * Transform a String into a java POJO
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(Class<T> type, String json) throws Exception{
        return builder.fromJson( json,type);
    }


    /**
     * Transform JSONOBJECT into class
     * @param type
     * @param json
     * @param <T>
     * @return
     */
    public static <T> void jsonToObject(Class<T> type, String json, Handler<AsyncResult<T>> resultHandler){
        try{
            resultHandler.handle(Future.succeededFuture(builder.fromJson(json, type)));
        }catch (Exception e){
            e.printStackTrace();
            resultHandler.handle(Future.failedFuture(e.getCause()));
        }
    }


    /**
     *
     * @param type
     * @param json
     * @param <T>
     */
    public static <T> String listToString(Class<T> type, List<T> json){
        try {
            return builder.toJson(json, type);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Object into string
     * @param type
     * @param json
     * @param <T>
     */
    public static <T> String objectToString(Class<T> type, T json){
        try {
            return builder.toJson(json, type);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Object into JsonObject
     * @param type
     * @param json
     * @param <T>
     */
    public static <T> JsonObject objectToJsonObject(Class<T> type, T json){
        try {
            return new JsonObject( builder.toJson(json, type) );
        }catch (Exception e){
            e.printStackTrace();
            return new JsonObject();
        }
    }

    /**
     *
     * @param type
     * @param json
     * @return
     */
    public static <T> JsonObject listToJsonObject(Class<T> type, List<T> json){

        try {
            return new JsonObject( builder.toJson(json, getType(List.class, type) ));
        }catch (Exception e){
            e.printStackTrace();
            return new JsonObject();
        }
    }


    /**
     * Fetch types for parsing
     * @param rawClass
     * @param parameter
     * @return
     */
    private static Type getType(Class<?> rawClass, Class<?> parameter) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {parameter};
            }
            @Override
            public Type getRawType() {
                return rawClass;
            }
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

}

