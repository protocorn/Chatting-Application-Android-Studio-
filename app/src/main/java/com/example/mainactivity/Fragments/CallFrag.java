package com.example.mainactivity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mainactivity.Adapters.GroupAdapter;
import com.example.mainactivity.Adapters.RoomDisplayAdapter;
import com.example.mainactivity.FollowList;
import com.example.mainactivity.GroupSearchActivity;
import com.example.mainactivity.R;
import com.example.mainactivity.databinding.FragmentCallBinding;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallFrag extends Fragment {
    public CallFrag() {

    }

    FragmentCallBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference mUserdatabase;
    Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCallBinding.inflate(inflater, container, false);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fuser = firebaseUser.getUid();
        binding.linear1.setVisibility(View.GONE);
        binding.linear2.setVisibility(View.GONE);
        binding.datalinear.setVisibility(View.VISIBLE);
        binding.tagline.setVisibility(View.VISIBLE);
        binding.editprofile.setVisibility(View.VISIBLE);
        binding.Username.setVisibility(View.VISIBLE);
        binding.save.setVisibility(View.GONE);
        getFollowandFollowing();

        binding.followingcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents = new Intent(getContext(), FollowList.class);
                intents.putExtra("ids", fuser);
                intents.putExtra("titlef", "F O L L O W I N G");
                startActivity(intents);
            }
        });

        binding.followcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intents = new Intent(getContext(), FollowList.class);
                intents.putExtra("ids", fuser);
                intents.putExtra("titlef", "F O L L O W E R S");
                startActivity(intents);
            }
        });


        binding.editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linear1.setVisibility(View.VISIBLE);
                binding.linear2.setVisibility(View.VISIBLE);
                binding.datalinear.setVisibility(View.GONE);
                binding.tagline.setVisibility(View.GONE);
                binding.editprofile.setVisibility(View.GONE);
                binding.save.setVisibility(View.VISIBLE);
                binding.Username.setVisibility(View.GONE);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query usernameQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(binding.editUsername.getText().toString());
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(binding.editUsername.getText().equals(binding.Username.getText()))
                        {
                            String tagline = binding.editTagline.getText().toString();
                            String username = binding.editUsername.getText().toString();

                            HashMap<String, Object> obj = new HashMap<>();
                            obj.put("tagline", tagline);
                            obj.put("username", username);

                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                            binding.linear1.setVisibility(View.GONE);
                            binding.linear2.setVisibility(View.GONE);
                            binding.editprofile.setVisibility(View.VISIBLE);
                            binding.save.setVisibility(view.GONE);
                            binding.datalinear.setVisibility(View.VISIBLE);
                            binding.tagline.setVisibility(View.VISIBLE);
                            binding.Username.setVisibility(View.VISIBLE);

                        }
                        else {
                            //if (snapshot.getChildrenCount() > 0) {
                            if(firebaseUser.getUid()=="1")
                            {
                                binding.editUsername.setError("Username Already Taken");
                            } else {
                                String tagline = binding.editTagline.getText().toString();
                                String username = binding.editUsername.getText().toString();

                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("username", username);
                                obj.put("tagline", tagline);
                                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);


                                binding.linear1.setVisibility(View.GONE);
                                binding.linear2.setVisibility(View.GONE);
                                binding.editprofile.setVisibility(View.VISIBLE);
                                binding.save.setVisibility(view.GONE);
                                binding.datalinear.setVisibility(View.VISIBLE);
                                binding.tagline.setVisibility(View.VISIBLE);
                                binding.Username.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Glide.with(getActivity()).load(users.getProfilepic()).placeholder(R.drawable.user).into(binding.profile);
                        binding.editTagline.setText(users.getTagline());
                        binding.editUsername.setText(users.getUsername());
                        binding.Username.setText(users.getUsername());
                        binding.tagline.setText(users.getTagline());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });
        return binding.getRoot();
    }

    private void getFollowandFollowing() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid());

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followcounter.setText("" + snapshot.getChildrenCount() + " followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.followingcounter.setText("" + snapshot.getChildrenCount() + " following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {

            Uri sfile = data.getData();
            binding.profile.setImageURI(sfile);

            final StorageReference reference = storage.getReference().child("profilepic")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(sfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilepic").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }
}
