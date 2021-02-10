package com.example.apnisavari.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FCMClient {

    private static Retrofit retrofit=null;
    public static Retrofit getClient(String baseURL)
    {
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder().baseUrl("https://fcm.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}