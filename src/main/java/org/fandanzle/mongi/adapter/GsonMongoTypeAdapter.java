package org.fandanzle.mongi.adapter;

import com.google.gson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a type adapter for Googles GSON, It is an adapter that is widely used in this application to serialize
 * data to and from MongoDB records and there corosponding java models.
 * @author alexb
 */
public class GsonMongoTypeAdapter
{
    public static enum GsonAdapterType
    {
        DESERIALIZER,
        SERIALIZER
    }

    /**
     *
     * @param g this param contains the the adapter type we wish to use.
     * @return gb this is an instance of the GsonBuilder
     */
    public static GsonBuilder getGsonBuilder(GsonAdapterType g)
    {
        GsonBuilder gb = new GsonBuilder();
        switch (g)
        {
            case DESERIALIZER:
                gb.registerTypeAdapter(ObjectId.class, new ObjectIdDeserializer());
                gb.registerTypeAdapter(Date.class, new DateDeserializer());
                break;
            case SERIALIZER:
                gb.registerTypeAdapter(ObjectId.class, new ObjectIdSerializer());
                gb.registerTypeAdapter(Date.class, new DateSerializer());
                break;
            default:
                return null;
        }
        return gb;
    }


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

    /**
     *
     */
    public static class ObjectIdSerializer implements JsonSerializer<ObjectId>
    {
        @Override
        public JsonElement serialize(ObjectId id, Type typeOfT, JsonSerializationContext context)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("$oid", id.toStringMongod());
            return jo;
        }
    }


    /**
     *
     */
    public static class DateDeserializer implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            Date d = null;
            SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try
            {
                d = f2.parse(json.getAsJsonObject().get("$date").getAsString());
            }
            catch (ParseException e)
            {
                d = null;
            }
            return d;
        }
    }


    /**
     *
     */
    public static class DateSerializer implements JsonSerializer<Date>
    {
        @Override
        public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context)
        {
            Date d = (Date)date;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            JsonObject jo = new JsonObject();
            jo.addProperty("$date", format.format(d));
            return jo;
        }
    }
}