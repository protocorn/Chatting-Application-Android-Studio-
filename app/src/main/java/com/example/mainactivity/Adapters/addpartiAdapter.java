package com.example.mainactivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.R;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class addpartiAdapter extends  RecyclerView.Adapter<addpartiAdapter.ViewHolder>{
    ArrayList<Users> list;
    Context context;
    FirebaseUser firebaseUser;
    String groupId;

    public addpartiAdapter(List<Users> list, Context context,String groupId) {
        this.list = (ArrayList<Users>) list;
        this.context = context;
        this.groupId = groupId;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_add_participants, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Glide.with(context).load(users.getProfilepic()).placeholder(R.drawable.user).into(holder.image);
        holder.UserName.setText(users.getUsername());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
                reference.child(groupId).child("Participants").child(users.getUserid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.add.setVisibility(View.GONE);
                        }
                        else {
                            addParticipant(users);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void addParticipant(Users users) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,String>hashMap=new HashMap<>();
        hashMap.put("uid",users.getUserid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",timestamp);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(users.getUserid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Added " + users.getUsername() + " to the room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView UserName;
        Button add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.contpic);
            UserName = itemView.findViewById(R.id.UserName);
            add = itemView.findViewById(R.id.add);
        }
    }
}
