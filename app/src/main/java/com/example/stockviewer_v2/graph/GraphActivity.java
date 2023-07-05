package com.example.stockviewer_v2.graph;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stockviewer_v2.ApiControllerSingleton;
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

public class GraphActivity extends AppCompatActivity {
    private String symbol;
    private LineChart lineChart;
    private Button butt_1d, butt_5d, butt_1m, butt_6m, butt_ytd, butt_1y, butt_5y, butt_max;
    LineData lineData;
    LineDataSet dataSet;

    StockHistoryData historyData_1d_interval;

    StockHistoryData historyData_1month_interval;

    StockHistoryData historyData_1h_interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbol = getIntent().getStringExtra("symbol");
        setContentView(R.layout.activity_graph);
        lineChart = findViewById(R.id.chart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            init_button_listeners();
        }
        historyData_1h_interval = new StockHistoryData();
        historyData_1h_interval.setValues(new ArrayList<>());
        getDataFor1D();
        //max is 5y, ~8 days in month market is closed
        getHistoryData_1d_interval(5*(365 - 8*12));
        //max is 500 month, ~1984
        getHistoryData_1m_interval(500);
    }

    private void getHistoryData_1m_interval(int maxResults) {
        fetchDataAsync("1month", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    historyData_1month_interval = response.body();
//                    if (historyData_1month_interval != null) {
//
//                    }
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

    private void getHistoryData_1d_interval(int maxResults) {
        fetchDataAsync("1day", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    historyData_1d_interval = response.body();
//                    if (historyData_1d_interval != null) {
//
//                    }
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
    private void init_button_listeners(){
        butt_1d = findViewById(R.id.butt_1d);
        butt_5d = findViewById(R.id.butt_5d);
        butt_1m = findViewById(R.id.butt_1m);
        butt_6m = findViewById(R.id.butt_6m);
        butt_ytd = findViewById(R.id.butt_ytd);
        butt_1y = findViewById(R.id.butt_1y);
        butt_5y = findViewById(R.id.butt_5y);
        butt_max = findViewById(R.id.butt_max);

        //YTD calculations:
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Get the number of days from the beginning of the year to the current month - ~ 8 days when market is closed in month
        int daysFromBeginning = currentDate.getDayOfYear()-8*currentDate.getMonthValue();
        //assuming there is ~8 days in month when market is closed

        butt_1d.setOnClickListener(v -> getDataFor1D());
        butt_5d.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1h_interval,5,"1day")));
        butt_1m.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1d_interval,30-8,"1day")));
        butt_6m.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1d_interval,180-6*8,"1day")));
        butt_ytd.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1d_interval,daysFromBeginning,"1day")));
        butt_1y.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1d_interval,365-12*8,"1day")));
        butt_5y.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1d_interval,5*(365 -12*8),"1day")));
        butt_max.setOnClickListener(v -> showChart(convertDataToEntries(historyData_1month_interval,500,"1month")));
    }

    private void showChart(List<Entry> entries) {
        if(entries.isEmpty()){
            Toast.makeText(this, "max api requests per minute, try again later", Toast.LENGTH_SHORT).show();
            if(historyData_1h_interval.getValues()==null || historyData_1h_interval.getValues().isEmpty())
                getDataFor1D();
            else if(historyData_1d_interval.getValues()==null || historyData_1d_interval.getValues().isEmpty())
                getHistoryData_1d_interval(5*(365 - 8*12));
            else if(historyData_1month_interval.getValues()==null || historyData_1month_interval.getValues().isEmpty())
                getHistoryData_1m_interval(500);
            return;
        }
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
        HistoryStockDataApiService historyStockDataApiService = ApiControllerSingleton.getInstance().getRetrofit().create(HistoryStockDataApiService.class);
        Call<StockHistoryData> call = historyStockDataApiService.getStockHistoryData(symbol, interval, maxResults, BuildConfig.API_KEY);
        call.enqueue(callback);
    }

    private void getDataFor1D() {
        if(historyData_1h_interval.getValues() !=null && !historyData_1h_interval.getValues().isEmpty()) //restore if saved
            showChart(convertDataToEntries(historyData_1h_interval,7,"1h"));
        int maxResults = 7;
        fetchDataAsync("1h", maxResults, new Callback<StockHistoryData>() {
            @Override
            public void onResponse(Call<StockHistoryData> call, Response<StockHistoryData> response) {
                if (response.isSuccessful()) {
                    historyData_1h_interval = response.body();
                    if (historyData_1h_interval != null) {
                        showChart(convertDataToEntries(historyData_1h_interval,maxResults,"1h"));
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


    private List<Entry> convertDataToEntries(StockHistoryData historyData, int numOfPoints, String interval) {
        if(historyData.getValues()==null || historyData.getValues().isEmpty()){ //api failed, don't show
            return new ArrayList<Entry>();
        }
        if(interval.equalsIgnoreCase("1day") || interval.equalsIgnoreCase("1month")){
            lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int i = Math.round(value) ;
                    int listSize = historyData_1month_interval.getValues().size();
                    listSize = Math.min(listSize,numOfPoints);
                    if (i > 0 && i <= listSize) {
                        String date = historyData_1month_interval.getValues().get(listSize - i).getDatetime();
                        return date;
                    }
                    return "";
                }
            });
        }else if(interval.equalsIgnoreCase("1h")){
            lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int i= (int)(value);
                    return "" + (8+i) + ":30"; //9:30, 10:30 ...
                }
            });
        }
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < numOfPoints; i++) {
            float closePrice = Float.parseFloat(historyData.getValues().get(numOfPoints - i - 1).getClose());
            entries.add(new Entry(i + 1, closePrice));
        }
        return entries;
    }


}