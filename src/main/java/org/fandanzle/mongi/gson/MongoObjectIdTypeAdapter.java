package org.fandanzle.mongi.gson;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by alexb on 12/05/15.
 */
public class MongoObjectIdTypeAdapter extends TypeAdapter<ObjectId> implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId>  {

    private static final Logger logger = Logger.getLogger(MongoObjectIdTypeAdapter.class);

    /**
     *
     */
    private static final Gson gson = new GsonBuilder().create();

    private static final TypeAdapter<ObjectId> dateTypeAdapter = gson.getAdapter(ObjectId.class);
    private static final TypeAdapter<String> dateTypeAdapterString = gson.getAdapter(String.class);


    /**
     *
     * @param src Objectid we wish to transform into json
     * @param type The object Type we to use
     * @param context The context of the adapter
     * @return Json element to be returned
     */
    public JsonElement serialize(ObjectId src, Type type,
                                 JsonSerializationContext context) {

        logger.info("serialize");
        if (src == null) {

            return null;
        } else {
            JsonObject jo = new JsonObject();
            jo.addProperty("$oid", src.toString() );
            return jo;
        }
    }


    /**
     *
     * @param json The Json element that will be appended too
     * @param typeOfT The Java type of the returning json
     * @param context The context of the json we will to return
     * @return ObjectId
     * @throws JsonParseException is thrown in the data can be not be deserialized
     */
    @Override
    public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {

        logger.info("deserialize");
        try
        {
            logger.info(json.getAsJsonObject().get("$oid").getAsString());
            logger.info(json.getAsJsonObject().get("$_id").getAsString());
            return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     *
     * @param out The json writer that Gson will use to output the new Json
     * @param value The ObjectId that will be transformed
     * @throws IOException if the json cannot be written
     */
    @Override
    public void write(final JsonWriter out, final ObjectId value) throws IOException {

        logger.info("Write");
        logger.info(value);
        logger.info("-----");

        if(value == null){
            out.nullValue();
            return;
        }

        dateTypeAdapter.write(out, value);
        logger.info(value.toString());
        logger.info("----");

    }

    /**
     *
     * @param in the JsonReader to use for this context
     * @return Objectid The Objectid
     * @throws IOException if the objectId cannot be processed
     */
    @Override
    public ObjectId read(final JsonReader in) throws IOException {

        logger.info("READ");
        String adapter = dateTypeAdapter.read(in).toHexString();
        logger.info(adapter);
        logger.info("----");
        ObjectId obj = new ObjectId(adapter);
        logger.info(in);
        logger.info(obj);

        return obj;



    }

}
