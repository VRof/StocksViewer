package com.example.stockviewer_v2.UIHelpers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockviewer_v2.R;

public class UIWatchListViewHolder extends RecyclerView.ViewHolder {
    private TextView dailyChange, price, symbol;
    private Button remove_btn;

    public UIWatchListViewHolder(@NonNull View itemView) {
        super(itemView);
        dailyChange = itemView.findViewById(R.id.dailyChange);
        price = itemView.findViewById(R.id.price);
        symbol = itemView.findViewById(R.id.symbol);
        remove_btn = itemView.findViewById(R.id.remove_btn);
    }

    public TextView getDailyChange() {
        return dailyChange;
    }

    public TextView getPrice() {
        return price;
    }

    public TextView getSymbol() {
        return symbol;
    }

    public Button getRemoveButton() {
        return remove_btn;
    }
}
