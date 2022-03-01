package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mainactivity.Adapters.ChatAdapter;
import com.example.mainactivity.Adapters.GroupAdapter;
import com.example.mainactivity.databinding.ActivityCreategrpBinding;
import com.example.mainactivity.models.GroupModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

public class creategrp extends AppCompatActivity {
    ActivityCreategrpBinding binding;
    ActionBar actionBar;
    FirebaseAuth auth;
    private Uri imageuri=null;
    FirebaseDatabase database;
    private String[] storagepermissions;
    ProgressDialog pd;
    int temp=0;
    String type="private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCreategrpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
       actionBar.setTitle("Create New Room");
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        storagepermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkUser();
        
        binding.floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGroup();
            }
        });
        binding.switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.switch1.isChecked()){
                    binding.grpPublic.setTextColor(Color.parseColor("#00ACC1"));
                    binding.grpPrivate.setTextColor(Color.parseColor("#818181"));

                    binding.grpPublic.setTypeface(Typeface.DEFAULT_BOLD);
                    binding.grpPrivate.setTypeface(Typeface.DEFAULT);
                }
                else {
                    binding.grpPublic.setTextColor(Color.parseColor("#818181"));
                    binding.grpPrivate.setTextColor(Color.parseColor("#00ACC1"));
                    binding.grpPrivate.setTypeface(Typeface.DEFAULT_BOLD);
                    binding.grpPublic.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        binding.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int num = binding.description.getText().toString().length();
                binding.textView5.setText(""+(int)num);
                if(num>200){
                    binding.textView5.setTextColor(getColor(R.color.red));
                    binding.textView4.setTextColor(getColor(R.color.red));
                    temp=1;
                }
                else if(num<=200){
                    binding.textView5.setTextColor(getColor(R.color.black));
                    binding.textView4.setTextColor(getColor(R.color.black));
                    temp=0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 20);
                }
        });
    }

    private void CreateGroup() {
        pd=new ProgressDialog(this);
        pd.setMessage("Creating New Room...");
        String grpTitle= binding.roomtitle.getText().toString().trim();
        String grpDescrptn= binding.description.getText().toString().trim();
        String grpIcon= String.valueOf(R.drawable.finalgroup);
        if(binding.switch1.isChecked()){
           type="public";
        }
        else {
            type="private";
        }
        if(TextUtils.isEmpty(grpTitle)){
            binding.roomtitle.setError("Title Cannot Be Empty");
        }
        else if(grpTitle.length()<=4){
            binding.roomtitle.setError("Title is too Short");
        }
        else if(temp==1){
            Toast.makeText(this, "Character Limit Exceeded", Toast.LENGTH_SHORT).show();
        }
        else {
            String g_timestamp = "" + System.currentTimeMillis();
            if (imageuri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("profilepicgroup").child(g_timestamp);
                storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                newGroup("" + g_timestamp, "" + grpTitle, "" + grpDescrptn, "" + uri.toString(),""+type);
                            }
                        });
                    }
                });
            } else {
                newGroup("" + g_timestamp, "" + grpTitle, "" + grpDescrptn, "" + grpIcon,""+type);
            }
        }
    }

    private void newGroup(String g_timestamp, String grpTitle, String grpDesc,String grpIcon,String type) {
        GroupModel model = new GroupModel();
        if(imageuri==null) {
            model.setGroupTitle("" + grpTitle);
            model.setGroupIcon("" + grpIcon);
            model.setGroupDesc("" + grpDesc);
            model.setTimestamp("" + g_timestamp);
            model.setGroupId("" + g_timestamp);
            model.setCreatedBy("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            model.setRoom_type(""+ type);
            database.getReference().child("groups").child(g_timestamp).setValue(model);
        }
        else
        {
            model.setGroupTitle("" + grpTitle);
            model.setGroupIcon("" + imageuri);
            model.setGroupDesc("" + grpDesc);
            model.setTimestamp("" + g_timestamp);
            model.setGroupId("" + g_timestamp);
            model.setRoom_type(""+ type);
            model.setCreatedBy("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            database.getReference().child("groups").child(g_timestamp).setValue(model);
        }

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("groups");
        ref.child(g_timestamp).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String,String> hashMap1=new HashMap<>();
                hashMap1.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap1.put("role","creator");
                hashMap1.put("timestamp",g_timestamp);
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("groups");
                reference.child(g_timestamp).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(creategrp.this, grpTitle+" ChatRoom Created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(creategrp.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(creategrp.this, "Failed To Create ChatRoom", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void RequestPermission(){
        ActivityCompat.requestPermissions(this,storagepermissions,100);
    }

    private void checkUser() {
        FirebaseUser firebaseUser= auth.getCurrentUser();
        if(firebaseUser!=null){
            actionBar.setSubtitle(firebaseUser.getDisplayName());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0){
            boolean storageAccepted= grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(storageAccepted){
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 20);
            }
            else{
                RequestPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==20){
                imageuri= data.getData();
                binding.profile2.setImageURI(imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}