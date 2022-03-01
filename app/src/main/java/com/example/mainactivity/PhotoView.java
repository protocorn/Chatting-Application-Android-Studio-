package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.mainactivity.databinding.ActivityPhotoViewBinding;

public class PhotoView extends AppCompatActivity {
ActivityPhotoViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhotoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String image= getIntent().getStringExtra("image");
        Glide.with(this).load(image).into(binding.mainimg);
    }
}