package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.mainactivity.databinding.ActivityVideoCallHostBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class VideoCallHost extends AppCompatActivity {
    ActivityVideoCallHostBinding binding;
    String mic,video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityVideoCallHostBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        mic = getIntent().getStringExtra("mic");
        video = getIntent().getStringExtra("video");


        // create a string of all characters
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 6;

        for(int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }

        String randomString = sb.toString();
        binding.textView.setText(randomString);

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
                if(mic.equals("on")&&video.equals("on")) {
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(randomString)
                            .setWelcomePageEnabled(false)
                            .setAudioMuted(false)
                            .setVideoMuted(false)
                            .build();
                    JitsiMeetActivity.launch(VideoCallHost.this, options);
                }
                else if(mic.equals("off")&&video.equals("on")){
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(randomString)
                            .setWelcomePageEnabled(false)
                            .setAudioMuted(true)
                            .setVideoMuted(false)
                            .build();
                    JitsiMeetActivity.launch(VideoCallHost.this, options);
                }
                else if(mic.equals("on")&&video.equals("off")){
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(randomString)
                            .setWelcomePageEnabled(false)
                            .setAudioMuted(false)
                            .setVideoMuted(true)
                            .build();
                    JitsiMeetActivity.launch(VideoCallHost.this, options);
                }
                else if(mic.equals("off")&&video.equals("off")){
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(randomString)
                            .setWelcomePageEnabled(false)
                            .setAudioMuted(true)
                            .setVideoMuted(true)
                            .build();
                    JitsiMeetActivity.launch(VideoCallHost.this, options);
                }
            }
        });

    }

}