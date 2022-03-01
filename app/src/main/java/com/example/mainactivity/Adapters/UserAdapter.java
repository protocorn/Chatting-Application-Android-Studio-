package com.example.mainactivity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.ChatDetailedActivity;
import com.example.mainactivity.R;
import com.example.mainactivity.UserProfile;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class    UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<Users> list;
    ArrayList<Users> alllist;
    ArrayList<Users> lists;
    ArrayList<Users> lists2;
    Context context;

    public UserAdapter(List<Users> list, Context context) {
        this.list = (ArrayList<Users>) list;
        this.context = context;
        this.alllist = new ArrayList<>(list);
        this.lists = new ArrayList<>(list);
        this.lists2 = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_users, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Glide.with(context).load(users.getProfilepic()).override(96, 96).placeholder(R.drawable.user).into(holder.image);
        holder.UserName.setText(users.getUsername());

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserid())
                .orderByChild("time")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.LastMsg.setText(snapshot1.child("msg").getValue().toString());
                                holder.time.setText(snapshot1.child("time").getValue().toString());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailedActivity.class);
                intent.putExtra("userId", users.getUserid());
                intent.putExtra("profile", users.getProfilepic());
                intent.putExtra("username", users.getUsername());
                intent.putExtra("tagline", users.getTagline());
                context.startActivity(intent);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("userId", users.getUserid());
                intent.putExtra("profile", users.getProfilepic());
                intent.putExtra("username", users.getUsername());
                intent.putExtra("tagline", users.getTagline());
                intent.putExtra("follow", users.getFollow());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView UserName, LastMsg, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.grp_pic);
            UserName = itemView.findViewById(R.id.UserName);
            LastMsg = itemView.findViewById(R.id.LastMsg);
            time = itemView.findViewById(R.id.timee);

        }
    }
}