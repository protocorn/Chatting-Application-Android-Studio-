package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.mainactivity.Adapters.ParticipantsAdapter;
import com.example.mainactivity.Adapters.UserAdapter;
import com.example.mainactivity.databinding.ActivityParticipantsBinding;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {
    ActivityParticipantsBinding binding;
    String groupId, grprole = "";
    FirebaseAuth auth;
    ArrayList<Users> list = new ArrayList<>();
    private List<String> allList;
    ParticipantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityParticipantsBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        allList = new ArrayList<>();
        groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo();
        getParticipants();

        if(grprole.equals("participant")){
            binding.floatingActionButton3.setVisibility(View.GONE);
        }
        else {
            binding.floatingActionButton3.setVisibility(View.VISIBLE);
        }

        adapter = new ParticipantsAdapter(list, ParticipantsActivity.this, groupId, grprole);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantsActivity.this, AddParticipantActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
    }

    private void getParticipants() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId);
        ref.child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    allList.add(snapshot1.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users users = snapshot1.getValue(Users.class);
                    for (String id : allList) {
                        if (users.getUserid().equals(id)) {
                            list.add(users);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String groupId = "" + snapshot1.child("groupId").getValue();
                    String groupTitle = "" + snapshot1.child("groupTitle").getValue();
                    String groupDesc = "" + snapshot1.child("groupDesc").getValue();
                    String groupIcon = "" + snapshot1.child("groupIcon").getValue();
                    String timestamp = "" + snapshot1.child("timestamp").getValue();
                    String createdBy = "" + snapshot1.child("createdBy").getValue();

                    binding.collapsebar.setTitle(groupTitle);
                    binding.collapsebar.setSubtitle(groupDesc);
                    binding.collapsebar.setExpandedTitleTextColor(getColor(R.color.white));
                    binding.collapsebar.setExpandedSubtitleTextColor(getColor(R.color.white));
                    binding.collapsebar.setCollapsedSubtitleTextColor(getColor(R.color.white));
                    binding.collapsebar.setCollapsedTitleTextColor(getColor(R.color.white));
                    Glide.with(ParticipantsActivity.this).load(groupIcon).placeholder(R.drawable.finalgroup).into(binding.icongrp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("groups");
        ref2.child(groupId).child("Participants").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    grprole = "" + snapshot.child("role").getValue();
                    if(grprole.equals("participant")){
                        binding.floatingActionButton3.setVisibility(View.GONE);
                    }
                    else {
                        binding.floatingActionButton3.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
