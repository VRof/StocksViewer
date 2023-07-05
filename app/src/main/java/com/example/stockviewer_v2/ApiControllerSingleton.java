package com.example.stockviewer_v2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiControllerSingleton {
    private static ApiControllerSingleton instance;
    private Retrofit retrofit;

    private ApiControllerSingleton() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static ApiControllerSingleton getInstance() {
        if (instance == null) {
            synchronized (ApiControllerSingleton.class) {
                if (instance == null) {
                    instance = new ApiControllerSingleton();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
