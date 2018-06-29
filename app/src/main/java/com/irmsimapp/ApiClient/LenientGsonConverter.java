package com.irmsimapp.ApiClient;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

/**
 * Created by darshit on 16/5/17.
 */
public class LenientGsonConverter extends GsonConverter {
    private Gson mGson;

    public LenientGsonConverter(Gson gson) {
        super(gson);
        mGson = gson;
    }

    public LenientGsonConverter(Gson gson, String charset) {
        super(gson, charset);
        mGson = gson;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        boolean willCloseStream = false; // try to close the stream, if there is no exception thrown using tolerant  JsonReader
        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(body.in()));
            jsonReader.setLenient(true);
            Object o = mGson.fromJson(jsonReader,type);
            willCloseStream = true;
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(willCloseStream) {
                closeStream(body);
            }
        }

        return super.fromBody(body, type);
    }

    private void closeStream(TypedInput body){
        try {
            InputStream in = body.in();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}