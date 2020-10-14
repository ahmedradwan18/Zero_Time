package com.zerotime.zerotime2020.Secretary.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerotime.zerotime2020.R;
import com.zerotime.zerotime2020.Secretary.Pojos.SecretaryChatPojo;
import com.zerotime.zerotime2020.Secretary.Pojos.Users;
import com.zerotime.zerotime2020.Secretary.SecretaryMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zerotime.zerotime2020.R.drawable.avatar1;

public class DisplayChatsAdapter extends RecyclerView.Adapter<DisplayChatsAdapter.ViewHolder> {
    private Context context;
    private List<Users> mUsers;
    private String theLastMessage;

    public DisplayChatsAdapter(Context context, List<Users> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_display_users, parent, false);

        return new DisplayChatsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = mUsers.get(position);
        holder.userName.setText(user.getUserName());

        int random = user.getRandom();
        switch (random) {
            case 0:
                holder.imageView.setImageResource(R.drawable.avatar1);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.avatar3);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.avatar4);
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.avatar5);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.avatar6);
                break;
            case 5:
                holder.imageView.setImageResource(R.drawable.avatar7);
                break;
            case 6:
                holder.imageView.setImageResource(R.drawable.avatar8);
                break;
            case 7:
                holder.imageView.setImageResource(R.drawable.avatar9);
                break;
            case 8:
                holder.imageView.setImageResource(R.drawable.avatar10);
                break;

            case 9:
                holder.imageView.setImageResource(R.drawable.avatar11);
                break;


        }


        lastMessage(user.getUserPrimaryPhone(), holder.lastMsg);

        holder.chatCard.setOnClickListener(v -> {

            Intent intent = new Intent(context, SecretaryMessage.class);
            intent.putExtra("UserID", user.getUserPrimaryPhone());
            intent.putExtra("UniqueID", "DisplayChatsAdapter");
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMsg;
        CardView chatCard;
        CircleImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.display_users_row_user_name);
            lastMsg = itemView.findViewById(R.id.display_users_row_last_message);
            chatCard = itemView.findViewById(R.id.display_users_row_card);
            imageView = itemView.findViewById(R.id.img_user);
        }
    }

    private void lastMessage(final String userPrimaryPhone, final TextView last_msg) {
        theLastMessage = "default";
        SecretaryChatPojo secretaryChatPojo = new SecretaryChatPojo();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    secretaryChatPojo.setSender(snapshot.child("Sender").getValue(String.class));
                    secretaryChatPojo.setReceiver(snapshot.child("Receiver").getValue(String.class));
                    secretaryChatPojo.setMessage(snapshot.child("Message").getValue(String.class));
                    secretaryChatPojo.setType(snapshot.child("Type").getValue(String.class));

                    if (secretaryChatPojo.getSender().equals("Zero Time") && secretaryChatPojo.getReceiver().equals(userPrimaryPhone) ||
                            secretaryChatPojo.getSender().equals(userPrimaryPhone) && secretaryChatPojo.getReceiver().equals("Zero Time")) {
                        theLastMessage = secretaryChatPojo.getMessage();

                    }
                }

                if ("default".equals(theLastMessage)) {
                    last_msg.setText("لا توجد رسائل");
                } else {
                    if (secretaryChatPojo.getType().equals("Text")) {
                        if (theLastMessage.startsWith("https://firebasestorage")){
                            last_msg.setText("صورة");
                            return;
                        }
                        last_msg.setText(theLastMessage);
                    } else last_msg.setText("صورة");
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
