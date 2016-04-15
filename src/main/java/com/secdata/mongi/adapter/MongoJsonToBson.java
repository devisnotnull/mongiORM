package com.secdata.mongi.adapter;

import com.google.gson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author alexb
 */
public class MongoJsonToBson {


    /**
     *
     */
    public static JsonSerializer<Date> dateToBson = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                context) {

            if(src == null) return null;
            else {
                JsonObject obj = new JsonObject();
                obj.addProperty("$date", src.getTime());
                return obj;
            }

        }
    };


    /**
     *
     */
    public static JsonSerializer<ObjectId> oidToBson = new JsonSerializer<ObjectId>() {
        @Override
        public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext
                context) {

            if(src == null) return null;
            else {
                JsonObject obj = new JsonObject();
                obj.addProperty("$oid", src.toString());
                return obj;
            }
        }
    };

    /**
     *
     */
    public static class ObjectIdDeserializer implements JsonDeserializer<ObjectId>
    {
        @Override
        public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            try
            {
                return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }


}
