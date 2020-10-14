package com.zerotime.zerotime2020.Moderator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotime.zerotime2020.CircularTextView;
import com.zerotime.zerotime2020.Moderator.Pojos.OrdersNumber;
import com.zerotime.zerotime2020.R;

import java.util.ArrayList;
import java.util.List;

public class NumberOrdersAdapter extends RecyclerView.Adapter<NumberOrdersAdapter.ClerkViewHolder> {
    private List<OrdersNumber> ordersNumbers = new ArrayList<>();
    private Context context;

    public NumberOrdersAdapter(List<OrdersNumber> ordersNumbers, Context context) {
        this.ordersNumbers = ordersNumbers;
        this.context = context;
    }

    @NonNull
    @Override
    public ClerkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClerkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orders_number, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClerkViewHolder holder, int position) {

        final OrdersNumber ordersNumber = ordersNumbers.get(position);
        holder.name.setText(ordersNumber.getName());
        holder.phone.setText(ordersNumber.getPhone());
        holder.ordersNumber.setText(String.valueOf(ordersNumber.getOrdersNumber()));
        holder.ordersNumber.setSolidColor("#428bca");
        holder.ordersNumber.setStrokeColor("#FFFFFF");
        holder.ordersNumber.setStrokeWidth(1);

    }

    @Override
    public int getItemCount() {
        return ordersNumbers.size();
    }

    public void setList(List<OrdersNumber> ordersNumbers) {
        this.ordersNumbers = ordersNumbers;
        notifyDataSetChanged();
    }

    public class ClerkViewHolder extends RecyclerView.ViewHolder {
        private TextView name, phone;
        private CircularTextView ordersNumber;

        public ClerkViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.numberOrders_name);
            phone = itemView.findViewById(R.id.numberOrders_phone);
            ordersNumber = itemView.findViewById(R.id.numberOrders_number);

        }
    }
}