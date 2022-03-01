package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.mainactivity.Adapters.GroupAdapter;
import com.example.mainactivity.Adapters.RoomDisplayAdapter;
import com.example.mainactivity.databinding.ActivityUserProfileBinding;
import com.example.mainactivity.models.GroupModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    private ArrayList<GroupModel> GroupList;
    private RoomDisplayAdapter roomDisplayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database= FirebaseDatabase.getInstance();
        binding= ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfile.this);
        binding.chat.setLayoutManager(layoutManager);
        RoomDisplayAdapter adapter = new RoomDisplayAdapter(UserProfile.this, GroupList);
        binding.chat.setAdapter(adapter);

        String username = getIntent().getStringExtra("username");
        String profile = getIntent().getStringExtra("profile");
        String tagline = getIntent().getStringExtra("tagline");
        final String recieveId = getIntent().getStringExtra("userId");

        getSupportActionBar().setTitle(username);

        GroupList =new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroupList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.child("Participants").child(recieveId).exists()){
                        GroupModel model= snapshot1.getValue(GroupModel.class);
                        GroupList.add(model);
                    }
                }
                roomDisplayAdapter=new RoomDisplayAdapter(UserProfile.this, GroupList);
                binding.chat.setAdapter(roomDisplayAdapter);
                roomDisplayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.Username2.setText(username);
        Glide.with(this).load(profile).placeholder(R.drawable.user).into(binding.profile2);
        binding.tagline.setText(tagline);





        getFollowandfollowings();
        checkFollowingstatus();
        binding.followcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents=new Intent(UserProfile.this, FollowList.class);
                intents.putExtra("ids",recieveId);
                intents.putExtra("titlef","F O L L O W E R S");
                startActivity(intents);
            }
        });

        binding.followingcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents=new Intent(UserProfile.this, FollowList.class);
                intents.putExtra("ids",recieveId);
                intents.putExtra("titlef","F O L L O W I N G");
                startActivity(intents);
            }
        });



        binding.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(recieveId)
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(recieveId).child("followers").child(firebaseUser.getUid())
                            .setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(recieveId)
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(recieveId).child("followers").child(firebaseUser.getUid())
                            .removeValue();
                }

            }
        });

    }

    private void checkFollowingstatus() {
        final String recieveId = getIntent().getStringExtra("userId");
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(recieveId).exists()){
                            binding.follow.setText("Following");
                        }
                        else {
                            binding.follow.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFollowandfollowings() {
        final String recieveId = getIntent().getStringExtra("userId");
        ;        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(recieveId);

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followcount.setText(""+snapshot.getChildrenCount() + " followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followingcount.setText(""+snapshot.getChildrenCount() + " following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}