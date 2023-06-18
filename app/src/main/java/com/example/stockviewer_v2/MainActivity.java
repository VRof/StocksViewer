package com.example.stockviewer_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockviewer_v2.UIHelpers.UIWatchListAdapter;
import com.example.stockviewer_v2.search.StockMetadata;
import com.example.stockviewer_v2.search.StockMetadataApiResponse;
import com.example.stockviewer_v2.search.StockMetadataApiService;
import com.example.stockviewer_v2.search.StockSearchAdapter;
import com.example.stockviewer_v2.watchList.StockPriceRealTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private List<StockMetadata> stockMetadataList;
    private AutoCompleteTextView searchbar;

    private List<StockPriceRealTime> selectedStocksList;

    private RecyclerView watchListRecyclerView;

    private UIWatchListAdapter watchListAdapter;

    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_search();
        init_buttons();
        //startPriceUpdateTimer();
    }



    private void addSelectedStock() {
        String symbol = searchbar.getText().toString().trim();
        if (stockMetadataList != null && !stockMetadataList.isEmpty()) {
            if (!symbol.isEmpty()) {
                for (StockMetadata stock : stockMetadataList) {
                    if (stock.getSymbol().equals(symbol)) {
                        selectedStocksList.add(fetchStockPrice(stock.getSymbol()));
                        watchListAdapter.notifyDataSetChanged();
                        searchbar.setText("");
                        return;
                    }
                }
            }
            Toast.makeText(this, "Invalid stock symbol", Toast.LENGTH_SHORT).show();
        }
    }

    private StockPriceRealTime fetchStockPrice(String symbol) {
        // Dummy implementation - replace with your actual implementation to fetch stock price
        double price = 100.0; // Replace with the actual stock price
        double dailyChange = 2.5; // Replace with the actual daily change
        return new StockPriceRealTime(price, dailyChange, symbol);
    }



    private void init_buttons() {
        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(v -> addSelectedStock());
        selectedStocksList = new ArrayList<>();

        watchListRecyclerView = findViewById(R.id.watchListRecyclerView);
        watchListAdapter = new UIWatchListAdapter(this, selectedStocksList);
        watchListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        watchListRecyclerView.setAdapter(watchListAdapter);
    }
    private void init_search() {
        searchbar = findViewById(R.id.searchbar);
        searchbar.setText("Loading list...");
        searchbar.setEnabled(false);

        stockMetadataList = new ArrayList<>();

        //TODO
        //!load from file part!
        //

        // Call the API to fetch stock metadata
        fetchAllStocksMetadataForSearchBar();

        searchbar.setThreshold(1);
        searchbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockMetadata selectedStock = (StockMetadata) parent.getItemAtPosition(position);
                if (selectedStock != null) {
                    String symbol = selectedStock.getSymbol();
                    String name = selectedStock.getName();
                    // Handle the selected stock here
                }
            }
        });
        searchbar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchbar.setText("");
                } else {
                    searchbar.setText("Search for stock...");
                }
            }
        });
        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Do nothing when the Enter button is clicked
                    return true;
                }
                return false;
            }
        });
    }

    // Call api to fetch stocks metadata for searchbar
    private void fetchAllStocksMetadataForSearchBar() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StockMetadataApiService stockApiService = retrofit.create(StockMetadataApiService.class);

        String stockType = "Common Stock";
        String country = "United States";
        String apiKey = BuildConfig.API_KEY;

        Call<StockMetadataApiResponse> call = stockApiService.getStocks(stockType, country, apiKey);
        call.enqueue(new Callback<StockMetadataApiResponse>() {
            @Override
            public void onResponse(Call<StockMetadataApiResponse> call, Response<StockMetadataApiResponse> response) {
                if (response.isSuccessful()) {
                    StockMetadataApiResponse stockResponse = response.body();
                    if (stockResponse != null) {
                        stockMetadataList = stockResponse.getData();
                        searchbar.setText("Search for stock...");
                        searchbar.setEnabled(true);
                        StockSearchAdapter adapter = new StockSearchAdapter(MainActivity.this, R.layout.search_result_item, stockMetadataList);
                        searchbar.setAdapter(adapter);
                        Log.d("api", "Stocks metadata fetch success");
                    }
                } else {
                    Log.e("api", "Stocks metadata error");
                }
            }

            @Override
            public void onFailure(Call<StockMetadataApiResponse> call, Throwable t) {
                Log.e("api", "Stocks metadata error" + t);
            }
        });
    }

    private boolean isStockAlreadySelected(String symbol) {
        for (StockPriceRealTime selectedStock : selectedStocksList) {
            if (selectedStock.getSymbol().equals(symbol))
                return true;
        }
        return false;
    }

}
