package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.mainactivity.Adapters.SearchAdapter;
import com.example.mainactivity.databinding.ActivitySearchBinding;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    FirebaseDatabase database;
    DatabaseReference mUserdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        ArrayList<Users> list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mUserdatabase=FirebaseDatabase.getInstance().getReference("Users");

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        binding.ChatView.setLayoutManager(layoutManager);
        SearchAdapter adapter = new SearchAdapter(list, SearchActivity.this);
        binding.ChatView.setAdapter(adapter);

        binding.seacrhppl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            private void searchUsers(String s) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Query query= FirebaseDatabase.getInstance().getReference("Users")
                        .orderByChild("username").startAt(s).endAt(s+"\uf8ff");

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        list.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Users users = snapshot1.getValue(Users.class);

                            assert users !=null;
                            assert firebaseUser != null;

                            if (!users.getUserid().equals(firebaseUser.getUid())) {
                                list.add(users);
                            }
                        }
                        SearchAdapter adapter = new SearchAdapter(list, SearchActivity.this);
                        binding.ChatView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserid(dataSnapshot.getKey());
                    if (!users.getUserid().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}