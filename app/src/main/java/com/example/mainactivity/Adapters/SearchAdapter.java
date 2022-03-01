package com.example.mainactivity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.R;
import com.example.mainactivity.SearchActivity;
import com.example.mainactivity.UserProfile;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;
    FirebaseUser firebaseUser;

    public SearchAdapter(List<Users> list, Context context) {
        this.list = (ArrayList<Users>) list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_users, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Glide.with(context).load(users.getProfilepic()).placeholder(R.drawable.user).into(holder.image);
        holder.UserName.setText(users.getUsername());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        isFollowed(users.getUserid(), holder.follow);

        if(users.getUserid().equals(firebaseUser.getUid())){
            holder.follow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(users.getUserid())
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(users.getUserid()).child("followers").child(firebaseUser.getUid())
                            .setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(users.getUserid())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(users.getUserid()).child("followers").child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("userId", users.getUserid());
                intent.putExtra("profile", users.getProfilepic());
                intent.putExtra("username", users.getUsername());
                intent.putExtra("tagline", users.getTagline());
                context.startActivity(intent);
            }
        });
    }

    private void isFollowed(String userid, Button follow) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    follow.setText("Following");
                }
                else
                {
                    follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView UserName;
        Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.contpic);
            UserName = itemView.findViewById(R.id.UserName);
            follow = itemView.findViewById(R.id.follower);

        }
    }
}

