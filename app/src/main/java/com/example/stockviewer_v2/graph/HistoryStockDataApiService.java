package com.example.stockviewer_v2.graph;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HistoryStockDataApiService {
    @GET("time_series")
    Call<StockHistoryData> getStockHistoryData(
            @Query("symbol") String symbol,
            @Query("interval") String interval,
            @Query("outputsize") int outputSize,
            @Query("apikey") String apiKey
    );
}
