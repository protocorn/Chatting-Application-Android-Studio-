package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;

import com.example.mainactivity.Adapters.ChatAdapter;
import com.example.mainactivity.databinding.ActivityVideosBinding;

public class Videos extends AppCompatActivity {
ActivityVideosBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVideosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String video = getIntent().getStringExtra("video");
        binding.videoView.setVideoPath(video);
        MediaController mediaController=new MediaController(Videos.this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.start();
    }
}