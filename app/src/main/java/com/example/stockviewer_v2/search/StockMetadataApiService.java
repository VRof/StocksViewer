package com.example.stockviewer_v2.search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockMetadataApiService {
    @GET("stocks")
    Call<StockMetadataApiResponse> getStocks(@Query("type") String stockType,
                                             @Query("country") String country,
                                             @Query("apikey") String apiKey);
}

