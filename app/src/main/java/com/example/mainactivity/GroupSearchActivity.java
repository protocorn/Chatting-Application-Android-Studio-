package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.mainactivity.Adapters.GroupAdapter;
import com.example.mainactivity.Adapters.RoomDisplayAdapter;
import com.example.mainactivity.Adapters.SearchAdapter;
import com.example.mainactivity.databinding.ActivityGroupSearchBinding;
import com.example.mainactivity.databinding.ActivitySearchBinding;
import com.example.mainactivity.models.GroupModel;
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

public class GroupSearchActivity extends AppCompatActivity {
    ActivityGroupSearchBinding binding;
    FirebaseDatabase database;
    DatabaseReference mUserdatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityGroupSearchBinding.inflate(getLayoutInflater());
        auth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        ArrayList<GroupModel> list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mUserdatabase = FirebaseDatabase.getInstance().getReference("Users");

        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupSearchActivity.this);
        binding.ChatView2.setLayoutManager(layoutManager);
        RoomDisplayAdapter adapter = new RoomDisplayAdapter(GroupSearchActivity.this, list);
        binding.ChatView2.setAdapter(adapter);

        binding.seacrhppl2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            private void searchUsers(String s) {
                Query query = FirebaseDatabase.getInstance().getReference("groups")
                        .orderByChild("groupTitle").startAt(s).endAt(s + "\uf8ff");

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            GroupModel groupModel = snapshot1.getValue(GroupModel.class);

                            assert groupModel != null;
                            list.add(groupModel);

                        }
                        RoomDisplayAdapter adapter = new RoomDisplayAdapter(GroupSearchActivity.this, list);
                        binding.ChatView2.setAdapter(adapter);
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GroupModel model = snapshot1.getValue(GroupModel.class);
                        list.add(model);
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