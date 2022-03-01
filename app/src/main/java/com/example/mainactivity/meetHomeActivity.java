package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.InCallService;
import android.view.View;
import android.widget.Toast;

import com.example.mainactivity.databinding.ActivityMeetHomeBinding;

import java.net.Inet4Address;

public class meetHomeActivity extends AppCompatActivity {
ActivityMeetHomeBinding binding;
int mic=1,video=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMeetHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(meetHomeActivity.this,MainActivity.class));
            }
        });

        binding.joinmeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(meetHomeActivity.this, VideoCall.class);
                if(mic==1){
                    intent.putExtra("mic","on");
                }
                else
                {
                    intent.putExtra("mic","off");
                }
                if(video==1){
                    intent.putExtra("video","on");
                }
                else
                {
                    intent.putExtra("video","off");
                }
                startActivity(intent);
            }
        });
        binding.hostmeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(meetHomeActivity.this, VideoCallHost.class);
                if(video==1){
                    intent2.putExtra("video","on");
                }
                else
                {
                    intent2.putExtra("video","off");
                }
                if(mic==1){
                    intent2.putExtra("mic","on");
                }
                else
                {
                    intent2.putExtra("mic","off");
                }
                startActivity(intent2);
            }
        });
        binding.mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mic==1){
                    binding.mic.setBackgroundResource(R.drawable.micmute);
                    mic=0;
                }
                else if(mic==0){
                    binding.mic.setBackgroundResource(R.drawable.micunmute);
                    mic=1;
                }
            }
        });
        binding.vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video==1){
                    binding.vid.setBackgroundResource(R.drawable.videooff);
                    video=0;
                }
                else if(video==0){
                    binding.vid.setBackgroundResource(R.drawable.videoon);
                    video=1;
                }
            }
        });
    }
}