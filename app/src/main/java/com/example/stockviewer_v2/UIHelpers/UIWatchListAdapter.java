package com.example.stockviewer_v2.UIHelpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockviewer_v2.R;
import com.example.stockviewer_v2.watchList.StockPriceRealTime;

import java.util.List;

public class UIWatchListAdapter extends RecyclerView.Adapter<UIWatchListViewHolder> {
    private Context context;
    private List<StockPriceRealTime> items;

    public UIWatchListAdapter(Context context, List<StockPriceRealTime> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public UIWatchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UIWatchListViewHolder(LayoutInflater.from(context).inflate(R.layout.stock_data_mainpage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UIWatchListViewHolder holder, int position) {
        String dailyChangeStr;
        StockPriceRealTime item = items.get(holder.getAdapterPosition());

        if (item.getDailyChange() >= 0) {
            dailyChangeStr = "+" + item.getDailyChange() + "%";
            holder.getDailyChange().setTextColor(Color.rgb(0, 130, 0));
        } else {
            dailyChangeStr = "-" + item.getDailyChange() + "%";
            holder.getDailyChange().setTextColor(Color.rgb(200, 0, 0));
        }
        holder.getDailyChange().setText(dailyChangeStr);

        holder.getPrice().setText(Double.toString(item.getPrice()));
        holder.getSymbol().setText(item.getSymbol());

        holder.getRemoveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition(); // Get the current position
                if (currentPosition != RecyclerView.NO_POSITION) {
                    items.remove(currentPosition); // Remove the item from the list
                    notifyItemRemoved(currentPosition); // Notify the adapter of the item removal
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
