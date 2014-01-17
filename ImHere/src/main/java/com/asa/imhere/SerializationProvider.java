package com.asa.imhere;

import com.asa.imhere.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Aaron on 1/16/14.
 */
public class SerializationProvider<T> {
    private static final String TAG = LogUtils.makeLogTag("DhSerializeProvider");

    private Gson mGson;

    public SerializationProvider() {
        mGson = new Gson();
    }

    /**
     * Serializes the given JSON in String format to the given class type.
     *
     * @param json
     * @param clss
     * @return
     */
    public T serialize(String json, Class<T> clss) throws JsonSyntaxException {
        return mGson.fromJson(json, clss);
    }

    /**
     * Serializes the given JSON in Google's GSON {@link com.google.gson.JsonElement} format to
     * the given class type.
     *
     * @param json
     * @param clss
     * @return
     */
    public T serialize(JsonElement json, Class<T> clss) throws JsonSyntaxException {
        return mGson.fromJson(json, clss);
    }

}
