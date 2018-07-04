package com.dcdroid.chatapp.ApiClient;

import com.google.gson.Gson;
import com.dcdroid.chatapp.Configuration.Config;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;


/**
 * Created by admin on 07-Mar-17.
 */

public class ApiHandler
{
    private static final String BASE_URL_COMMON = "xxxxx" ;
    private static final String BASE_URL_IM = "xxxxxxxx" ;
    private static final String BASE_URL_OPENFIRE =  "http://"+ Config.openfire_host_server_IP+":"+Config.openfire_host_server_PORT+"/plugins/restapi/v1/" ;
    private static final String BASE_URL_OPENFIRE_USER_SERVICE =  "http://"+ Config.openfire_host_server_IP+":"+Config.openfire_host_server_PORT+"/plugins/userService/" ;
    private static final long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    private static Webservices apiServiceCommon,apiServiceIM,apiServiceopenFire,apiServiceopenFireUserService;


    public static Webservices getCommonApiService() {

        if (apiServiceCommon == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(BASE_URL_COMMON)
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new GsonConverter(new Gson()))
                    .build();


            apiServiceCommon = restAdapter.create(Webservices.class);
            return apiServiceCommon;
        }
        else {
            return apiServiceCommon;
        }
    }


    public static Webservices getOpenfireApiUserService() {

        if (apiServiceopenFireUserService == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);


            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(BASE_URL_OPENFIRE_USER_SERVICE)
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new LenientGsonConverter(new Gson()))
                    .build();


            apiServiceopenFireUserService = restAdapter.create(Webservices.class);
            return apiServiceopenFireUserService;
        }
        else {
            return apiServiceopenFireUserService;
        }
    }

    public static Webservices getOpenfireApiService() {

        if (apiServiceopenFire == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);


            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(BASE_URL_OPENFIRE)
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new LenientGsonConverter(new Gson()))
                    .build();


            apiServiceopenFire = restAdapter.create(Webservices.class);
            return apiServiceopenFire;
        }
        else {
            return apiServiceopenFire;
        }
    }


    public static Webservices getIMApiService() {

        if (apiServiceIM == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);


            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(BASE_URL_IM)
                    .setClient(new OkClient(okHttpClient))
                    .setConverter(new LenientGsonConverter(new Gson()))
                    .build();


            apiServiceIM = restAdapter.create(Webservices.class);
            return apiServiceIM;
        }
        else {
            return apiServiceIM;
        }
    }
}
