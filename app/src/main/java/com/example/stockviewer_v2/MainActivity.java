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
import com.example.stockviewer_v2.watchList.ClosePriceForDailyChangeApiService;
import com.example.stockviewer_v2.watchList.ClosePriseForDailyChange;
import com.example.stockviewer_v2.watchList.StockPriceRealTime;
import com.example.stockviewer_v2.watchList.StockPriceTickerApiService;
import com.example.stockviewer_v2.watchList.FileWriterReaderSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private List<StockMetadata> stockMetadataList; //metadata for search (company name, symbol...)
    private AutoCompleteTextView searchbar;

    private List<StockPriceRealTime> selectedStocksList;//watchlist, restored from file

    private RecyclerView watchListRecyclerView;

    private UIWatchListAdapter watchListAdapter; //adapter to update price in realtime

    private Handler handler; //realtime price updater thread handler
    private Runnable stockPriceUpdater;; //realtime price updater thread

    private FileWriterReaderSingleton fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(); //stock price update thread handler
        fileAdapter = FileWriterReaderSingleton.getInstance(); //file writer/reader
        selectedStocksList = fileAdapter.restoreWatchListFromFile(this); //restore watchlist from file
        init_search();
        init_listeners();
        initStockPriceUpdater();
        updateClosePrices();
    }


    private void initStockPriceUpdater() {
        handler = new Handler();
        stockPriceUpdater = new Runnable() {
            @Override
            public void run() {
                //if list is empty call thread again in 0.5 sec to prevent "busywaiting"
                if(selectedStocksList.size()==0)
                    handler.postDelayed(this, 500);
                else{
                    updateStockPrices();
            // Update stock prices every 15 seconds, each stock adds 15 seconds because of api restrictions
                    int delay = selectedStocksList.size() * 15000;
                    handler.postDelayed(this, delay);
                }
            }

        };
        startStockPriceUpdater(); //start the stock price updater
    }


    private void init_listeners() {
        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(v -> addSelectedStock());

        watchListRecyclerView = findViewById(R.id.watchListRecyclerView);
        watchListAdapter = new UIWatchListAdapter(this, selectedStocksList);
        watchListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        watchListRecyclerView.setAdapter(watchListAdapter);
    }

    private void addSelectedStock() {
        String symbol = searchbar.getText().toString().trim();
        if (stockMetadataList != null && !stockMetadataList.isEmpty()) {
            if (!symbol.isEmpty()) {
                for (StockMetadata stock : stockMetadataList) {
                    if (stock.getSymbol().equals(symbol)) {
                        // Check if the stock with the symbol already exists in selectedStocksList
                        if (isStockAlreadySelected(symbol)) {
                            Toast.makeText(this, "Stock already added", Toast.LENGTH_SHORT).show();
                            searchbar.setText("");
                            return;
                        }
                        StockPriceRealTime newStock = new StockPriceRealTime(0.0, 0.0, symbol);
                        selectedStocksList.add(newStock);
                        //rewrite file
                        fileAdapter.saveWatchListToFile(this,selectedStocksList);
                        //
                        searchbar.setText("");
                        // Update the stock price immediately after adding
                        fetchStockPrice(symbol, selectedStocksList.indexOf(newStock));
                        fetchStockClosePrice(symbol, selectedStocksList.indexOf(newStock));
                        watchListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
            Toast.makeText(this, "Invalid stock symbol", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isStockAlreadySelected(String symbol) {
        for (StockPriceRealTime selectedStock : selectedStocksList) {
            if (selectedStock.getSymbol().equals(symbol))
                return true;
        }
        return false;
    }
    private void init_search() {
        searchbar = findViewById(R.id.searchbar);
        searchbar.setText("Loading list...");
        searchbar.setEnabled(false);

        stockMetadataList = new ArrayList<>();

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
        startStockPriceUpdater();
    }
    private void startStockPriceUpdater() {
        handler.postDelayed(stockPriceUpdater, 0); // Start the stock price updater immediately
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
                        StockSearchAdapter adapter = new StockSearchAdapter(MainActivity.this,
                                R.layout.search_result_item, stockMetadataList);
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


    private synchronized void  updateStockPrices() {
        if (selectedStocksList != null && !selectedStocksList.isEmpty()) {
            for (int i = 0; i < selectedStocksList.size(); i++) {
                StockPriceRealTime stock = selectedStocksList.get(i);
                fetchStockPrice(stock.getSymbol(), i);
            }
        }
    }
    private void fetchStockPrice(String symbol, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StockPriceTickerApiService stockPriceApiService = retrofit.create(StockPriceTickerApiService.class);

        String apiKey = BuildConfig.API_KEY;

        Call<StockPriceRealTime> call = stockPriceApiService.getStockRealTimePrice(symbol, apiKey);
        call.enqueue(new Callback<StockPriceRealTime>() {
            @Override
            public void onResponse(Call<StockPriceRealTime> call, Response<StockPriceRealTime> response) {
                if (response.isSuccessful()) {
                    StockPriceRealTime stockResponse = response.body();
                    if (stockResponse != null) {
                        double price = stockResponse.getPrice();
                        // Update the stock price in the selectedStocksList
                        StockPriceRealTime stock = selectedStocksList.get(position);
                        stock.setPrice(price);
                        stock.setDailyChange(((price - stock.getClose())/stock.getClose())*100);
                        watchListAdapter.notifyItemChanged(position);
                        Log.d("api", "Stock price update success: " + symbol);
                    }
                } else {
                    Log.e("api", "Stock price update error: " + symbol);
                }
            }

            @Override
            public void onFailure(Call<StockPriceRealTime> call, Throwable t) {
                Log.e("api", "Stock price update error: " + symbol + ", " + t);
            }
        });
    }

    private synchronized void updateClosePrices(){
        if (selectedStocksList != null && !selectedStocksList.isEmpty()) {
            for (int i = 0; i < selectedStocksList.size(); i++) {
                StockPriceRealTime stock = selectedStocksList.get(i);
                fetchStockClosePrice(stock.getSymbol(), i);
            }
        }
    }
    private void fetchStockClosePrice(String symbol,  int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ClosePriceForDailyChangeApiService closePriceApiService = retrofit.create(ClosePriceForDailyChangeApiService.class);

        String apiKey = BuildConfig.API_KEY;

        Call<ClosePriseForDailyChange> call = closePriceApiService.getEndOfDayData(symbol, apiKey);
        call.enqueue(new Callback<ClosePriseForDailyChange>() {
            @Override
            public void onResponse(Call<ClosePriseForDailyChange> call, Response<ClosePriseForDailyChange> response) {
                if (response.isSuccessful()) {
                    ClosePriseForDailyChange closePriceResponse = response.body();
                    if (closePriceResponse != null) {
                        double closePrice = closePriceResponse.getClose();
                        // Update the close price in the selectedStocksList
                        StockPriceRealTime stock = selectedStocksList.get(position);
                        stock.setClose(closePrice);
                        stock.setDailyChange(((stock.getPrice() - closePrice)/stock.getPrice())*100);
                        watchListAdapter.notifyItemChanged(position);
                        Log.d("api", "Close price update success: " + symbol);
                    }
                } else {
                    Log.e("api", "Close price update error: " + symbol);
                }
            }

            @Override
            public void onFailure(Call<ClosePriseForDailyChange> call, Throwable t) {
                Log.e("api", "Close price update error: " + symbol + ", " + t);
            }
        });
    }


}
