package com.example.stockviewer_v2.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.stockviewer_v2.R;

import java.util.ArrayList;
import java.util.List;

public class StockSearchAdapter extends ArrayAdapter<StockMetadata> implements Filterable {
    private LayoutInflater inflater;
    private int resourceId;
    private List<StockMetadata> originalData;
    private List<StockMetadata> filteredData;

    private int MAX_RESULTS = 200;

    public StockSearchAdapter(Context context, int resourceId, List<StockMetadata> data) {
        super(context, resourceId);
        inflater = LayoutInflater.from(context);
        this.resourceId = resourceId;
        this.originalData = data;
        this.filteredData = new ArrayList<>(data);
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public StockMetadata getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.symbolTextView = view.findViewById(R.id.symbolTextView);
            holder.nameTextView = view.findViewById(R.id.nameTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        StockMetadata stock = filteredData.get(position);
        holder.symbolTextView.setText(stock.getSymbol());
        holder.nameTextView.setText(stock.getName());

        return view;
    }

    private static class ViewHolder {
        TextView symbolTextView;
        TextView nameTextView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = originalData;
                    results.count = originalData.size();
                } else {
                    List<StockMetadata> filteredList = new ArrayList<>();

                    if (originalData != null) {
                        for (StockMetadata stock : originalData) {
                            String symbol = stock.getSymbol();
                            String name = stock.getName();

                            // Check if the symbol or name matches the constraint (case-insensitive)
                            if (symbol.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    filteredList.add(stock);
                            }
                            // Adjust the maximum number of results here
                            if (filteredList.size() == MAX_RESULTS) {
                                break; // Reached the maximum number of results, stop filtering
                            }
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
        }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<StockMetadata>) results.values;
                clear();
                addAll(filteredData);
                notifyDataSetChanged();
            }
        };
    }


}
