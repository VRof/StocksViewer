package com.example.stockviewer_v2.graph;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stockviewer_v2.BuildConfig;
import com.example.stockviewer_v2.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphActivity extends AppCompatActivity {
    private String symbol;
    private LineChart lineChart;
    private Button butt_1d, butt_5d, butt_1m, butt_6m, butt_ytd, butt_1y, butt_5y, butt_max;
    LineData lineData;
    LineDataSet dataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getIntent().getStringExtra("symbol");
        setContentView(R.layout.activity_graph);
        lineChart = findViewById(R.id.chart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            init_button_listeners();
        }
        getDataFor1D();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init_button_listeners(){
        butt_1d = findViewById(R.id.butt_1d);
        butt_5d = findViewById(R.id.butt_5d);
        butt_1m = findViewById(R.id.butt_1m);
        butt_6m = findViewById(R.id.butt_6m);
        butt_ytd = findViewById(R.id.butt_ytd);
        butt_1y = findViewById(R.id.butt_1y);
        butt_5y = findViewById(R.id.butt_5y);
        butt_max = findViewById(R.id.butt_max);

        //assuming there is ~8 days in month when market is closed
        butt_1d.setOnClickListener(v -> getDataFor1D());
        butt_5d.setOnClickListener(v -> getDataInterval1D(5));
        butt_1m.setOnClickListener(v -> getDataInterval1D(30 - 8));
        butt_6m.setOnClickListener(v -> getDataInterval1D(180-6*8));
        butt_ytd.setOnClickListener(v -> getDataForYTD());
        butt_1y.setOnClickListener(v -> getDataInterval1D(365 - 8*12));
        butt_5y.setOnClickListener(v -> getDataInterval1D(5*(365 - 8*12)));
        butt_max.setOnClickListener(v -> getDataForMAX());
    }

    private void showChart(List<Entry> entries) {
       // lineChart.getXAxis().setLabelCount(entries.size());

        dataSet = new LineDataSet(entries, symbol);
        lineChart.setAutoScaleMinMaxEnabled(false);
        lineChart.setBackgroundColor(Color.LTGRAY);
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.BLUE);  // Set color for the line
        dataSet.setFillColor(Color.parseColor("#66B2FF"));  // Set color for the area under the graph
        dataSet.setDrawFilled(true);  // Enable filling the area under the graph
        dataSet.setValueTextSize(12f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.1f);

        lineChart.getXAxis().resetAxisMaximum();
        lineChart.getXAxis().resetAxisMinimum();

        lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setTextColor(Color.MAGENTA);
        lineChart.getAxisLeft().setTextColor(Color.RED);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Set color for the area under the graph
        dataSet.setFillColor(Color.parseColor("#9dfab6"));
        dataSet.setDrawFilled(true);
        lineChart.getLegend().setEnabled(false); // Disable the legend

        Description description = new Description();
        description.setTextSize(14f);
        description.setTextColor(Color.rgb(255,0,0));
        description.setText(symbol);
        lineChart.setDescription(description);


        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Customize the label formatting based on your needs
                return String.format("%.1f$", value);
            }
        });

        lineChart.invalidate();
    }

    public void fetchDataAsync(String interval, int maxResults, Callback<StockHistoryData> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HistoryStockDataApiService historyStockDataApiService = retrofit.create(HistoryStockDataApiService.class);

        Call<StockHistoryData> call = historyStockDataApiService.getStockHistoryData(symbol, interval, maxResults, BuildConfig.API_KEY);
        call.enqueue(callback);
    }

    private void getDataFor1D() {
        int maxResults = 7;
        fetchDataAsync("1h", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    StockHistoryData historyData = response.body();
                    if (historyData != null) {
                        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                int index = (int) value;
                            if (index >= 0 && index <= historyData.getValues().size()) {
                                return  (index + 8) + ":30";
                            }
                            return "";
                            }
                        });
                        showChart(convertDataToEntries(historyData,maxResults));
                    }
                } else {
                    Log.e("API Error", "Request failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StockHistoryData> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getDataForYTD(){
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Get the number of days from the beginning of the year to the current month - ~ 8 days when market is closed in month
        int maxResults = currentDate.getDayOfYear()-8*currentDate.getMonthValue();
        fetchDataAsync("1day", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    StockHistoryData historyData = response.body();
                    if (historyData != null) {
                        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                int i = Math.round(value) ;
                                int listSize = historyData.getValues().size();
                                if (i > 0 && i <= listSize) {
                                    String date = historyData.getValues().get(listSize - i).getDatetime();
                                    return date;
                                }
                                return "";
                            }
                        });

                        showChart(convertDataToEntries(historyData, maxResults));
                    }
                } else {
                    Log.e("API Error", "Request failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StockHistoryData> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });
    }


    private void getDataForMAX(){
        int maxResults = 500;
        fetchDataAsync("1month", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    StockHistoryData historyData = response.body();
                    if (historyData != null) {
                        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                int i = Math.round(value) ;
                                int listSize = historyData.getValues().size();
                                listSize = Math.min(listSize,maxResults);
                                if (i > 0 && i <= listSize) {
                                    String date = historyData.getValues().get(listSize - i).getDatetime();
                                    return date;
                                }
                                return "";
                            }
                        });

                        showChart(convertDataToEntries(historyData, historyData.getValues().size()));
                    }
                } else {
                    Log.e("API Error", "Request failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StockHistoryData> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });
    }

    private void getDataInterval1D(int numOfDays){
        int maxResults = numOfDays;
        fetchDataAsync("1day", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    StockHistoryData historyData = response.body();
                    if (historyData != null) {
                        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                int i = Math.round(value) ;
                                int listSize = historyData.getValues().size();
                                if (i > 0 && i <= listSize) {
                                    String date = historyData.getValues().get(listSize - i).getDatetime();
                                    return date;
                                }
                                return "";
                            }
                        });

                        showChart(convertDataToEntries(historyData, maxResults));
                    }
                } else {
                    Log.e("API Error", "Request failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StockHistoryData> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });
    }
    private List<Entry> convertDataToEntries(StockHistoryData historyData, int numOfPoints) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < numOfPoints; i++) {
            float closePrice = Float.parseFloat(historyData.getValues().get(numOfPoints - i - 1).getClose());
            entries.add(new Entry(i + 1, closePrice));
        }
        return entries;
    }


}