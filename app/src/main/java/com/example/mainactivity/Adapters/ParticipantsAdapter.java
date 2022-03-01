package com.example.mainactivity.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.R;
import com.example.mainactivity.UserProfile;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.HolderParticipants>{
    ArrayList<Users> list;
    Context context;
    String groupId,grprole,hisRole,previousRole;
    public ParticipantsAdapter(List<Users> list, Context context,String groupId,String grprole) {
        this.list = (ArrayList<Users>) list;
        this.context = context;
        this.groupId=groupId;
        this.grprole=grprole;
    }

    @NonNull
    @Override
    public HolderParticipants onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_participants,parent,false);
        return new HolderParticipants(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipants holder, int position) {
        Users users=list.get(position);
        GroupModel groupModel=new GroupModel();
        String name= users.getUsername();
        String profile= users.getProfilepic();
        String uid= users.getUserid();
        holder.participant.setVisibility(View.GONE);
        holder.creator.setVisibility(View.GONE);
        holder.admin.setVisibility(View.GONE);
        holder.partiuser.setText(name);
       if(profile==null){
           Glide.with(context).load(R.drawable.user).into(holder.partipic);
       }
       else {
           Glide.with(context).load(profile).placeholder(R.drawable.user).into(holder.partipic);
       }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grprole= ""+snapshot.child("role").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       holder.partipic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(context, UserProfile.class);
               intent.putExtra("username",users.getUsername());
               intent.putExtra("profile",users.getProfilepic());
               intent.putExtra("userId",users.getUserid());
               intent.putExtra("tagline",users.getTagline());
               intent.putExtra("follow", users.getFollow());
               context.startActivity(intent);
           }
       });
        CheckIfAlreadyExists(users,holder);
        holder.PartiLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
                reference.child(groupId).child("Participants").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            previousRole=""+snapshot.child("role").getValue();

                            String[] options;
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle(users.getUsername());
                            if(grprole.equals("creator")){
                                if(previousRole.equals("admin")){
                                    options=new String[]{"View Profile","Remove Admin","Remove Participant","Chat Privately"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(which==1){
                                                removeAdmin(users);
                                                dialog.dismiss();
                                            }
                                            else if(which==2){
                                                removeParticpant(users);
                                                dialog.dismiss();
                                            }

                                        }
                                    }).show();
                                }
                                else if(previousRole.equals("participant")){
                                    options=new String[]{"View Profile","Make Admin","Remove Participant","Chat Privately"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(which==1){
                                                makeAdmin(users);
                                                dialog.dismiss();
                                            }
                                            else if(which==2){
                                                removeParticpant(users);
                                                dialog.dismiss();
                                            }

                                        }
                                    }).show();
                                }
                            }
                            else if(grprole.equals("admin")){
                                if(previousRole.equals("creator")){
                                    options=new String[]{"View Profile","Chat Privately"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(which==0){

                                            }
                                            else if(which==1){

                                            }

                                        }
                                    }).show();
                                }
                                else if(previousRole.equals("participant")){
                                    options=new String[]{"View Profile","Make Admin","Remove Participant","Chat Privately"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==1){
                                                makeAdmin(users);
                                                dialog.dismiss();
                                            }
                                            else if(which==2){
                                                removeParticpant(users);
                                                dialog.dismiss();
                                            }

                                        }
                                    }).show();
                                }
                            }
                            else if(grprole.equals("participant")){
                                options=new String[]{"View Profile","Chat Privately"};
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        }
                        else {
                            Toast.makeText(context, "removing...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void makeAdmin(Users users) {
        HashMap<String,Object>hashMap2=new HashMap<>();
        hashMap2.put("role","admin");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(users.getUserid()).updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, users.getUsername() +" Is Now Admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeParticpant(Users users) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(users.getUserid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, users.getUsername() +" Is Removed From the Room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAdmin(Users users) {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("role","participant");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(users.getUserid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, users.getUsername() +" is No More An Admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CheckIfAlreadyExists(Users users, HolderParticipants holder) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
        reference.child(groupId).child("Participants").child(users.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                 hisRole=""+ snapshot.child("role").getValue();
                 if(hisRole.equals("creator")){
                     holder.creator.setVisibility(View.VISIBLE);
                 }
                 else if(hisRole.equals("participant")){
                     holder.participant.setVisibility(View.VISIBLE);
                 }
                 else if(hisRole.equals("admin")){
                     holder.admin.setVisibility(View.VISIBLE);
                 }
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

    class HolderParticipants extends RecyclerView.ViewHolder{
        TextView partiuser,admin,participant,creator;
        ImageView partipic,icongrp;
        SubtitleCollapsingToolbarLayout collapse;
        LinearLayout PartiLinear;

        public HolderParticipants(@NonNull View itemView) {
            super(itemView);
            partipic=itemView.findViewById(R.id.partipic);
            admin=itemView.findViewById(R.id.admin);
            participant=itemView.findViewById(R.id.participants);
            creator=itemView.findViewById(R.id.creator);
            partiuser=itemView.findViewById(R.id.partiuser);
            icongrp = itemView.findViewById(R.id.icongrp);
            collapse = itemView.findViewById(R.id.collapsebar);
            PartiLinear = itemView.findViewById(R.id.PartiLinear);
        }
    }
}