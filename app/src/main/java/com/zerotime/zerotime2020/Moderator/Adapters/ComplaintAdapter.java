package com.zerotime.zerotime2020.Moderator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotime.zerotime2020.Moderator.Pojos.Complaint_Pojo;
import com.zerotime.zerotime2020.R;

import java.util.ArrayList;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ClerkViewHolder> {
    private List<Complaint_Pojo> complaints = new ArrayList<>();
    private Context context;

    public ComplaintAdapter(ArrayList<Complaint_Pojo> complaintList, Context context) {
        this.complaints = complaintList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClerkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClerkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClerkViewHolder holder, int position) {

        final Complaint_Pojo complaint = complaints.get(position);
        holder.name.setText(complaint.getUserName());
        holder.phone.setText(complaint.getUserPhone());
        holder.complaint.setText(complaint.getComplaint());
        holder.date.setText(complaint.getComplaintDate());


    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    public void setList(List<Complaint_Pojo> complaintsList) {
        this.complaints = complaintsList;
        notifyDataSetChanged();
    }

    public static class ClerkViewHolder extends RecyclerView.ViewHolder {
        private TextView name, phone, complaint, date;

        public ClerkViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.complaint_name_value);
            phone = itemView.findViewById(R.id.complaint_phone_value);
            complaint = itemView.findViewById(R.id.complaint_complaint_value);
            date = itemView.findViewById(R.id.complaint_date);

        }
    }
}