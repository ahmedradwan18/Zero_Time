package com.zerotime.zerotime2020.Secretary.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.anton46.stepsview.StepsView;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.FollowingOrderSettings;
import com.zerotime.zerotime2020.Secretary.Pojos.OrderState;
import com.zerotime.zerotime2020.Secretary.SecretaryUserData;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FollowingOrderAdapter extends RecyclerView.Adapter<FollowingOrderAdapter.FollowingOrderViewHolder> {
    private List<OrderState> ordersList;
    private Context context;
    String[] steps = {"   لم يتم \n الإستلام", "     تم \n الإستلام", "   جاري \n التوصيل", "     تم \n التوصيل"};

    public FollowingOrderAdapter(List<OrderState> ordersList, Context context) {
        this.ordersList = ordersList;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new FollowingOrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_order, parent, false));
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onBindViewHolder(@NonNull FollowingOrderViewHolder holder, int position) {
        SharedPreferences prefs = context.getSharedPreferences("UserState", MODE_PRIVATE);
        final OrderState orderState = ordersList.get(position);
        String userType = prefs.getString("UserType", "");
        if (userType != null) {
            switch (userType) {
                case "Secretary":
                    holder.settings.setVisibility(View.VISIBLE);
                    holder.userData.setVisibility(View.VISIBLE);
                    break;
                case "Moderator":

                    holder.settings.setVisibility(View.INVISIBLE);
                    holder.userData.setVisibility(View.VISIBLE);
                    break;
                case "User":
                    holder.settings.setVisibility(View.INVISIBLE);
                    holder.userData.setVisibility(View.INVISIBLE);
                    break;
            }
        }


        holder.name.setText(orderState.getName());
        holder.address.setText(orderState.getAddress());
        holder.price.setText(orderState.getPrice());
        holder.date.setText(orderState.getDate());
        holder.description.setText(orderState.getDescription());
        holder.phone.setText(orderState.getPhone());

        holder.userData.setOnClickListener(view -> {
            Intent intent = new Intent(context, SecretaryUserData.class);
            intent.putExtra("UserPhone", orderState.getUser_Phone());
            context.startActivity(intent);

        });

        holder.settings.setOnClickListener(view -> {

            Intent intent = new Intent(context, FollowingOrderSettings.class);
            intent.putExtra("OrderDate", orderState.getDate());
            context.startActivity(intent);
            ((Activity) context).finish();

        });
        holder.arrow.setOnClickListener(view -> {

            if (holder.expandableConstraint.getVisibility() == View.GONE) {
                holder.expandableConstraint.setVisibility(View.VISIBLE);
                holder.arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            } else {
                holder.expandableConstraint.setVisibility(View.GONE);
                holder.arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }

        });


        holder.stepsView.setLabels(steps)

                .setBarColorIndicator(context.getResources().getColor(R.color.material_blue_grey_800))
                .setProgressColorIndicator(context.getResources().getColor(R.color.colorPrimaryDark))
                .setLabelColorIndicator(context.getResources().getColor(R.color.colorPrimaryDark))
                .setCompletedPosition(orderState.getCurrentState())
                .drawView();


    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class FollowingOrderViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, address, description, price, date;
        StepsView stepsView;
        ImageView userData, settings;
        CardView cardView;
        Button arrow;
        ConstraintLayout cardsConstraint, expandableConstraint;

        public FollowingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.followingOrder_name_value);
            phone = itemView.findViewById(R.id.followingOrder_phone_value);
            address = itemView.findViewById(R.id.followingOrder_address_value);
            description = itemView.findViewById(R.id.followingOrder_description_value);
            price = itemView.findViewById(R.id.followingOrder_price_value);
            date = itemView.findViewById(R.id.followingOrder_date);
            stepsView = itemView.findViewById(R.id.followingOrder_stepsView);
            userData = itemView.findViewById(R.id.FollowingOrder_UserData);
            cardView = itemView.findViewById(R.id.FollowingOrderCard);
            settings = itemView.findViewById(R.id.FollowingOrder_Settings);
            arrow = itemView.findViewById(R.id.followingOrder_arrowBtn);
            cardsConstraint = itemView.findViewById(R.id.followingOrder_cards_constraint);
            expandableConstraint = itemView.findViewById(R.id.followingOrder_expandableView);

        }
    }
}