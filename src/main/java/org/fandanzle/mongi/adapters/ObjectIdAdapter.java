package org.fandanzle.mongi.adapters;

import java.util.Calendar;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarTypeAdapter extends TypeAdapter<ObjectId> implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

    private static final Gson gson = new GsonBuilder().create();
    private static final TypeAdapter<ObjectId> dateTypeAdapter = gson.getAdapter(ObjectId.class);
    private static final String MONGO_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * @param src
     * @param type
     * @param context
     * @return
     */
    public JsonElement serialize(ObjectId src, Type type,
                                 JsonSerializationContext context) {
        if (src == null) {
            return null;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(MONGO_UTC_FORMAT);
            JsonObject jo = new JsonObject();
            jo.addProperty("$date", format.format(src.getTime()));
            return jo;
        }
    }

    /**
     * @param json
     * @param type
     * @param context
     * @return
     * @throws JsonParseException
     */
    @Override
    public ObjectId deserialize(ObjectId json, Type type,
                                JsonDeserializationContext context) throws JsonParseException {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(MONGO_UTC_FORMAT);
        try {
            date = format.parse(json.getAsJsonObject().get("$date").getAsString());
        } catch (ParseException e) {
            date = null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar;
    }

    /**
     * @param out
     * @param value
     * @throws IOException
     */
    @Override
    public void write(JsonWriter out, ObjectId value) throws IOException {
        dateTypeAdapter.write(out, value.toString());
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    @Override
    public ObjectId read(JsonReader in) throws IOException {
        ObjectId read = dateTypeAdapter.read(in);
        return read;
    }

}