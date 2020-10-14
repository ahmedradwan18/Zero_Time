package com.zerotime.zerotime2020.Moderator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotime.zerotime2020.Moderator.Pojos.Clerk_History;
import com.zerotime.zerotime2020.R;

import java.util.List;

public class ClerkHistoryAdapter extends RecyclerView.Adapter<ClerkHistoryAdapter.ClerkViewHolder> {
    Context context;
    private List<Clerk_History> clerk_histories;

    public ClerkHistoryAdapter(List<Clerk_History> clerk_histories, Context context) {
        this.clerk_histories = clerk_histories;
        this.context = context;
    }

    @NonNull
    @Override
    public ClerkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClerkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clerk_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClerkViewHolder holder, int position) {

        final Clerk_History history = clerk_histories.get(position);
        holder.description.setText(history.getDescription());
        holder.price.setText(history.getPrice());
        holder.date.setText(history.getDate());
        holder.size.setText(history.getSize());
        holder.ReceiverPhone.setText(history.getReceiverPhone());
        holder.ReceiverAddress.setText(history.getReceiverAddress());

    }

    @Override
    public int getItemCount() {
        return clerk_histories.size();
    }

    public void setList(List<Clerk_History> histories) {
        this.clerk_histories = histories;
        notifyDataSetChanged();
    }

    public static class ClerkViewHolder extends RecyclerView.ViewHolder {
        private TextView name, description, price, size, ReceiverAddress, ReceiverPhone, date;

        public ClerkViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.clerkHistory_description_value);
            date = itemView.findViewById(R.id.clerkHistory_date);
            price = itemView.findViewById(R.id.clerkHistory_price_value);
            size = itemView.findViewById(R.id.clerkHistory_size_value);
            ReceiverAddress = itemView.findViewById(R.id.clerkHistory_address_value);
            ReceiverPhone = itemView.findViewById(R.id.clerkHistory_phone_value);

        }
    }
}