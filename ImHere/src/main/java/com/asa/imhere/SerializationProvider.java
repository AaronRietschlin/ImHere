package com.asa.imhere;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class SerializationProvider<T> {
    private static final String TAG = "SerializationProvider";

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
