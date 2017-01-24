package org.fandanzle.mongi.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarTypeAdapter extends TypeAdapter<Calendar> implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

    private static final Gson gson = new GsonBuilder().create();
    private static final TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
    private static final String MONGO_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * @param src
     * @param type
     * @param context
     * @return
     */
    public JsonElement serialize(Calendar src, Type type,
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
    public Calendar deserialize(JsonElement json, Type type,
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
    public void write(JsonWriter out, Calendar value) throws IOException {
        dateTypeAdapter.write(out, value.getTime());
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    @Override
    public Calendar read(JsonReader in) throws IOException {
        Date read = dateTypeAdapter.read(in);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(read);
        return gregorianCalendar;
    }

}