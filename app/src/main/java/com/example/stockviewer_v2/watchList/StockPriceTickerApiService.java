package com.example.stockviewer_v2.watchList;

import com.example.stockviewer_v2.search.StockMetadataApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockPriceTickerApiService {
        @GET("price")
        Call<StockPriceRealTime> getStockRealTimePrice(@Query("symbol") String symbol,
                                                             @Query("apikey") String apiKey);
}
