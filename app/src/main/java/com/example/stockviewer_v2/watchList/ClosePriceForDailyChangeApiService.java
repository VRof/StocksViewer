package com.example.stockviewer_v2.watchList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClosePriceForDailyChangeApiService {
    @GET("eod")
    Call<ClosePriseForDailyChange> getEndOfDayData(
            @Query("symbol") String symbol,
            @Query("apikey") String apiKey
    );
}
