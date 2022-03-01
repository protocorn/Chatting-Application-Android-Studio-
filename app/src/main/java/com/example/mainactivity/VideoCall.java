package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.mainactivity.databinding.ActivityVideoCallBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoCall extends AppCompatActivity {
    ActivityVideoCallBinding binding;
    String mic,video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityVideoCallBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        mic = getIntent().getStringExtra("mic");
        video = getIntent().getStringExtra("video");

        URL serverUrl;

        try {
            serverUrl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultoptions = new JitsiMeetConferenceOptions.Builder().setServerURL(serverUrl)
                    .setWelcomePageEnabled(false).build();
            JitsiMeet.setDefaultConferenceOptions(defaultoptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.mycode.getText().toString())) {
                    Toast.makeText(VideoCall.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                } else {
                    if(mic.equals("on")&&video.equals("on")) {
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(binding.mycode.getText().toString())
                                .setWelcomePageEnabled(false)
                                .setAudioMuted(false)
                                .setVideoMuted(false)
                                .build();
                        JitsiMeetActivity.launch(VideoCall.this, options);
                    }
                    else if(mic.equals("off")&&video.equals("on")){
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(binding.mycode.getText().toString())
                                .setWelcomePageEnabled(false)
                                .setAudioMuted(true)
                                .setVideoMuted(false)
                                .build();
                        JitsiMeetActivity.launch(VideoCall.this, options);
                    }
                    else if(mic.equals("on")&&video.equals("off")){
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(binding.mycode.getText().toString())
                                .setWelcomePageEnabled(false)
                                .setAudioMuted(false)
                                .setVideoMuted(true)
                                .build();
                        JitsiMeetActivity.launch(VideoCall.this, options);
                    }
                    else if(mic.equals("off")&&video.equals("off")){
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(binding.mycode.getText().toString())
                                .setWelcomePageEnabled(false)
                                .setAudioMuted(true)
                                .setVideoMuted(true)
                                .build();
                        JitsiMeetActivity.launch(VideoCall.this, options);
                    }
                }
            }
        });
    }
}