package com.zerotime.zerotime2020.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotime.zerotime2020.Pojos.HistoryPojo;
import com.zerotime.zerotime2020.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryPojo> historyList = new ArrayList<>();
    Context context;

    public HistoryAdapter(List<HistoryPojo> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryPojo history = historyList.get(position);
        holder.description.setText(history.getDescription());
        holder.date.setText(history.getDate());
        holder.price.setText(history.getPrice());
        holder.Raddress.setText(history.getRaddress());
        holder.Rname.setText(history.getRname());
        holder.Rphone1.setText(history.getRphone1());
        holder.Rphone2.setText(history.getRphone2());

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setList(List<HistoryPojo> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView description, date, price, Raddress, Rname, Rphone1, Rphone2;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.History_description_value);
            date = itemView.findViewById(R.id.History_order_date);
            price = itemView.findViewById(R.id.History_price_value);
            Raddress = itemView.findViewById(R.id.History_address_value);
            Rname = itemView.findViewById(R.id.History_name_value);
            Rphone1 = itemView.findViewById(R.id.History_primary_phone_value);
            Rphone2 = itemView.findViewById(R.id.History_secondary_phone_value);
        }
    }
}