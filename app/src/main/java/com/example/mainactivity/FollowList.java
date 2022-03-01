package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mainactivity.Adapters.SearchAdapter;
import com.example.mainactivity.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowList extends AppCompatActivity {
    String id;
    String title;
    private List<String> allList;
    RecyclerView recyclerView;
    private List<Users> list;
    SearchAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        getSupportActionBar().hide();

        Intent intents = getIntent();
        id = intents.getStringExtra("ids");
        title = intents.getStringExtra("titlef");
        TextView textView= findViewById(R.id.folwtxt);
        textView.setText(title);
        recyclerView = findViewById(R.id.ChatView3);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FollowList.this));
        list =new ArrayList<>();
        adapter=new SearchAdapter(list, FollowList.this);
        recyclerView.setAdapter(adapter);

        allList=new ArrayList<>();

        switch (title){
            case "F O L L O W E R S":
                getFollowers();
                break;

            case "F O L L O W I N G":
                getFollowing();
                break;
        }
    }

    private void getFollowing() {
        String id=getIntent().getStringExtra("ids");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    allList.add(snapshot1.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        String id=getIntent().getStringExtra("ids");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    allList.add(snapshot1.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showUsers(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Users users= snapshot1.getValue(Users.class);
                    for(String id : allList){
                        if(users.getUserid().equals(id)){
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}