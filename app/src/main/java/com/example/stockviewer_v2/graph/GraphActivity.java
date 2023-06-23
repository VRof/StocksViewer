package com.example.stockviewer_v2.graph;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Button butt_1d, butt_5d, butt_1m, butt_6m, butt_ytd, butt_1y, butt_5y, butt_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        butt_1d = findViewById(R.id.butt_1d);
        butt_5d = findViewById(R.id.butt_5d);
        butt_1m = findViewById(R.id.butt_1m);
        butt_6m = findViewById(R.id.butt_6m);
        butt_ytd = findViewById(R.id.butt_ytd);
        butt_1y = findViewById(R.id.butt_1y);
        butt_5y = findViewById(R.id.butt_5y);
        butt_max = findViewById(R.id.butt_max);

        lineChart = findViewById(R.id.chart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // Set background color
        lineChart.setBackgroundColor(Color.LTGRAY);


        setupChart(getDataFor5D());
        LineDataSet dataSet = new LineDataSet(getDataFor5D(), "dddd");  // Set null label text
        dataSet.setColor(Color.BLUE);  // Set color for the line
        dataSet.setFillColor(Color.parseColor("#66B2FF"));  // Set color for the area under the graph
        dataSet.setDrawFilled(true);  // Enable filling the area under the graph
        dataSet.setValueTextSize(12f);

        // Set up button click listeners
        butt_1d.setOnClickListener(v -> setupChart(getDataFor1D()));

        butt_5d.setOnClickListener(v -> setupChart(getDataFor5D()));

    }

    private void setupChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        // Set color for the area under the graph
        dataSet.setFillColor(Color.parseColor("#9dfab6"));
        dataSet.setDrawFilled(true);
        lineChart.getLegend().setEnabled(false); // Disable the legend

        // disable description
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Customize the label formatting based on your needs
                return value + "$";
            }


        });



        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        lineChart.invalidate();
    }

    private List<Entry> getDataFor1D() {
        // TODO: Fetch and return data for 1D time period
        // Example data for demonstration
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 15));
        entries.add(new Entry(2, 20));
        entries.add(new Entry(3, 50));
        entries.add(new Entry(4, 25));
        return entries;
    }

    private List<Entry> getDataFor5D() {
        // TODO: Fetch and return data for 5D time period
        // Example data for demonstration
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(20, 203));
        entries.add(new Entry(30, 244));
        entries.add(new Entry(40, 550));
        return entries;
    }
}