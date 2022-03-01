package com.example.mainactivity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.Adapters.GroupAdapter;
import com.example.mainactivity.Adapters.UserAdapter;
import com.example.mainactivity.R;
import com.example.mainactivity.creategrp;
import com.example.mainactivity.databinding.FragmentStatusBinding;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class statusFrag extends Fragment {
    FragmentStatusBinding binding;
    FirebaseAuth auth;
    private ArrayList<GroupModel> GroupList;
    private GroupAdapter groupAdapter;
    public statusFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentStatusBinding.inflate(inflater,container,false);
        auth=FirebaseAuth.getInstance();

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),creategrp.class);
                startActivity(intent);
            }
        });
        loadGroups();

        return binding.getRoot();
    }


    private void loadGroups() {
        GroupList =new ArrayList<>();
         DatabaseReference ref= FirebaseDatabase.getInstance().getReference("groups");
         ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 GroupList.clear();
                 for(DataSnapshot snapshot1:snapshot.getChildren()){
                     if(snapshot1.child("Participants").child(auth.getCurrentUser().getUid()).exists()){
                         GroupModel model= snapshot1.getValue(GroupModel.class);
                         GroupList.add(model);
                     }
                 }
                 groupAdapter=new GroupAdapter(getContext(), GroupList);
                 binding.grplist.setAdapter(groupAdapter);
                 groupAdapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

    }
}