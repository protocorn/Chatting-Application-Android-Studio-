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
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.R;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RoomDisplayAdapter extends RecyclerView.Adapter<RoomDisplayAdapter.HolderSearch> {
    private  Context context;
    private ArrayList<GroupModel>Grouplist;
    FirebaseUser firebaseUser;

    public RoomDisplayAdapter(Context context, ArrayList<GroupModel> Grouplist) {
        this.context = context;
        this.Grouplist = Grouplist;
    }
    @NonNull
    @Override
    public HolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_search, parent, false);
        return new HolderSearch(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearch holder, int position) {
        GroupModel groupModel=Grouplist.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.grpname.setText(groupModel.getGroupTitle());
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupModel.getRoom_type().equals("public")){
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
                    reference.child(groupModel.getGroupId()).child("Participants").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(context, "You Have Already Joined This Room", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String timestamp=""+System.currentTimeMillis();
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("uid",firebaseUser.getUid());
                                hashMap.put("role","participant");
                                hashMap.put("timestamp",timestamp);

                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
                                reference.child(groupModel.getGroupId()).child("Participants").child(firebaseUser.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "You Joined The Room", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    Toast.makeText(context, "private", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String Desc= groupModel.getGroupDesc();
        if(Desc.equals("")){
            holder.desc.setVisibility(View.GONE);
        }
        else {
            holder.desc.setText(Desc);
        }
        Glide.with(context).load(groupModel.getGroupIcon()).placeholder(R.drawable.finalgroup).into(holder.grppic);
    }

    @Override
    public int getItemCount() {
        return Grouplist.size();
    }

    public static class HolderSearch extends RecyclerView.ViewHolder {
        TextView grpname,desc;
        ImageView grppic;
        Button join;
        public HolderSearch(@NonNull View itemView) {
            super(itemView);
            grpname=itemView.findViewById(R.id.group_title);
            desc=itemView.findViewById(R.id.group_desc);
            grppic=itemView.findViewById(R.id.group_image);
            join=itemView.findViewById(R.id.group_join);
        }
    }
}
