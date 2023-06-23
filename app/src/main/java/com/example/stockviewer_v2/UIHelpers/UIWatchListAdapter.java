package com.example.stockviewer_v2.UIHelpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockviewer_v2.MainActivity;
import com.example.stockviewer_v2.R;
import com.example.stockviewer_v2.graph.GraphActivity;
import com.example.stockviewer_v2.watchList.FileWriterReaderSingleton;
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
        return new UIWatchListViewHolder(LayoutInflater.from(context).inflate(R.layout.stock_data_item_in_watchlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UIWatchListViewHolder holder, int position) {
        String dailyChangeStr;
        StockPriceRealTime item = items.get(holder.getAdapterPosition());

        if (item.getDailyChange() >= 0) {
            dailyChangeStr = String.format("+%.2f%%", item.getDailyChange());
            holder.getDailyChange().setTextColor(Color.rgb(0, 130, 0));
        } else {
            dailyChangeStr = String.format("%.2f%%", item.getDailyChange());
            holder.getDailyChange().setTextColor(Color.rgb(130, 0, 0));
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
                    //rewrite file
                    FileWriterReaderSingleton writer =  FileWriterReaderSingleton.getInstance();
                    writer.saveWatchListToFile(context,items);
                    //
                }
            }
        });
        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(holder.itemView.getContext(), GraphActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
