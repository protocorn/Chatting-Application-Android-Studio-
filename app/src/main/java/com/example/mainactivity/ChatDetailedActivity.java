package com.example.mainactivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mainactivity.Adapters.ChatAdapter;
import com.example.mainactivity.databinding.ActivityChatDetailedBinding;
import com.example.mainactivity.models.MessageModel;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gsconrad.richcontentedittext.RichContentEditText;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import render.animations.Attention;
import render.animations.Fade;
import render.animations.Render;

public class ChatDetailedActivity extends AppCompatActivity {
    ActivityChatDetailedBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    String SenderRoom, RecieverRoom, senderId, recieveId, profile, fwd;
    ChatAdapter adapter;
    int a, stickindex, dec;
    int menu = 1;
    String cat = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityChatDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        a = 1;
        stickindex = 1;
        binding.sticks.setImageResource(R.drawable.sticker);

        senderId = auth.getUid();
        recieveId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        //fwd = getIntent().getStringExtra("fwd");
        profile = getIntent().getStringExtra("profile");
        String tagline = getIntent().getStringExtra("tagline");

        binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu == 1) {
                    binding.menu.setVisibility(View.VISIBLE);
                    binding.more.setScaleX((float) 0.6);
                    binding.more.setScaleY((float) 0.6);
                    binding.more.setBackgroundResource(R.drawable.close);
                    menu = 0;
                } else if (menu == 0) {
                    binding.menu.setVisibility(View.GONE);
                    binding.more.setBackgroundResource(R.drawable.more);
                    binding.more.setScaleX((float) 1.0);
                    binding.more.setScaleY((float) 1.0);
                    menu = 1;
                }
            }
        });
        binding.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ChatDetailedActivity.this);
                dialog.setContentView(R.layout.reportaccount);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();
            }
        });

        binding.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ChatDetailedActivity.this);
                dialog.setContentView(R.layout.reportaccount);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button reportbtn;
                EditText othertext;
                RadioButton bully, scam, dislike, slactivity, hate, spam, violence, other;
                RadioGroup radioGroup;

                radioGroup = dialog.findViewById(R.id.radiogroup);
                reportbtn = dialog.findViewById(R.id.reportbtn);
                othertext = dialog.findViewById(R.id.other_text);
                bully = dialog.findViewById(R.id.bully);
                scam = dialog.findViewById(R.id.scam);
                dislike = dialog.findViewById(R.id.dislike);
                slactivity = dialog.findViewById(R.id.slacticity);
                hate = dialog.findViewById(R.id.hate);
                spam = dialog.findViewById(R.id.spam);
                violence = dialog.findViewById(R.id.violence);
                other = dialog.findViewById(R.id.other);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.other:
                                othertext.setVisibility(View.VISIBLE);
                                cat = "other";
                                break;
                            case R.id.bully:
                                cat = "bully";
                                break;
                            case R.id.scam:
                                cat = "scam";
                                break;
                            case R.id.dislike:
                                cat = "dislike";
                                break;
                            case R.id.slacticity:
                                cat = "nudity";
                                break;
                            case R.id.hate:
                                cat = "hate";
                                break;
                            case R.id.spam:
                                cat = "spam";
                                break;
                            case R.id.violence:
                                cat = "violence";
                                break;
                        }
                    }
                });
                reportbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cat==null) {
                            Toast.makeText(ChatDetailedActivity.this, "Select Category First", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ChatDetailedActivity.this, cat, Toast.LENGTH_SHORT).show();
                            cat=null;
                        }
                    }
                });

                dialog.show();
            }
        });
        binding.themes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatDetailedActivity.this, ThemesActivity.class));
            }
        });


        binding.sendmsg1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (binding.sendmsg1.getText().toString().isEmpty()) {
                    binding.sender.setImageResource(R.drawable.user);
                    dec = 1;
                } else {
                    binding.sender.setImageResource(R.drawable.send_button);
                    dec = 2;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.sendmsg1.getText().toString().isEmpty()) {
                    binding.sender.setImageResource(R.drawable.user);
                    dec = 1;
                } else {
                    binding.sender.setImageResource(R.drawable.send_button);
                    dec = 2;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.userName.setText(username);
        Glide.with(this).load(profile).override(70, 70).placeholder(R.drawable.user).into(binding.profileImage);

        binding.boom.setButtonEnum(ButtonEnum.SimpleCircle);
        binding.boom.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_1);
        binding.boom.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_1);

        int number = binding.boom.getButtonPlaceEnum().buttonNumber();
        int[] drawableResources = new int[]{
                R.drawable.picture,
                R.drawable.video,
                R.drawable.headphone,
                R.drawable.location,
                R.drawable.phones,
                R.drawable.document

        };
        for (int i = 0; i < number; i++) {
            SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                    .normalImageRes(drawableResources[i]).listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            buttonClicked(index);
                        }
                    });
            binding.boom.addBuilder(builder);
        }
        /*if(fwd.equals("forwd")){
            binding.forwd.setVisibility(View.VISIBLE);
        }*/
        /*binding.forwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.forwd.setVisibility(View.GONE);
            }
        });*/
        binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        binding.sticks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a == 1) {
                    Render render = new Render(ChatDetailedActivity.this);
                    render.setAnimation(Attention.RuberBand(binding.sticks));
                    render.start();
                    if (stickindex == 1) {
                        binding.stcikerscroll1.setVisibility(View.GONE);
                        binding.greetingscroll.setVisibility(View.GONE);
                        binding.catscroll.setVisibility(View.GONE);
                        binding.memescroll.setVisibility(View.GONE);
                        binding.stcikerscroll.setVisibility(View.VISIBLE);
                    }
                    if (stickindex == 2) {
                        binding.stcikerscroll.setVisibility(View.GONE);
                        binding.greetingscroll.setVisibility(View.GONE);
                        binding.catscroll.setVisibility(View.GONE);
                        binding.memescroll.setVisibility(View.GONE);
                        binding.stcikerscroll1.setVisibility(View.VISIBLE);
                    }
                    if (stickindex == 3) {
                        binding.stcikerscroll1.setVisibility(View.GONE);
                        binding.stcikerscroll.setVisibility(View.GONE);
                        binding.memescroll.setVisibility(View.GONE);
                        binding.catscroll.setVisibility(View.GONE);
                        binding.greetingscroll.setVisibility(View.VISIBLE);
                    }
                    if (stickindex == 4) {
                        binding.stcikerscroll.setVisibility(View.GONE);
                        binding.greetingscroll.setVisibility(View.GONE);
                        binding.stcikerscroll1.setVisibility(View.GONE);
                        binding.catscroll.setVisibility(View.GONE);
                        binding.memescroll.setVisibility(View.VISIBLE);
                    }
                    if (stickindex == 5) {
                        binding.stcikerscroll.setVisibility(View.GONE);
                        binding.greetingscroll.setVisibility(View.GONE);
                        binding.stcikerscroll1.setVisibility(View.GONE);
                        binding.memescroll.setVisibility(View.GONE);
                        binding.catscroll.setVisibility(View.VISIBLE);
                    }
                    binding.textstick.setVisibility(View.VISIBLE);
                    binding.sticks.setImageResource(R.drawable.happy);
                    float f = (float) 0.5;
                    binding.ChatRecycle.setAlpha(f);
                    a = 2;
                } else if (a == 2) {
                    Render render2 = new Render(ChatDetailedActivity.this);
                    render2.setAnimation(Attention.RuberBand(binding.sticks));
                    render2.start();
                    binding.stcikerscroll.setVisibility(View.GONE);
                    binding.stcikerscroll1.setVisibility(View.GONE);
                    binding.greetingscroll.setVisibility(View.GONE);
                    binding.memescroll.setVisibility(View.GONE);
                    binding.catscroll.setVisibility(View.GONE);
                    binding.textstick.setVisibility(View.GONE);
                    binding.sticks.setImageResource(R.drawable.sticker);
                    binding.ChatRecycle.setAlpha(1);
                    a = 1;
                }
            }
        });
        binding.emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.stcikerscroll1.setVisibility(View.GONE);
                binding.greetingscroll.setVisibility(View.GONE);
                binding.catscroll.setVisibility(View.GONE);
                binding.memescroll.setVisibility(View.GONE);
                binding.stcikerscroll.setVisibility(View.VISIBLE);
                binding.emoji.setTypeface(null, Typeface.BOLD);
                binding.handu.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setTypeface(null, Typeface.NORMAL);
                binding.greetings.setTypeface(null, Typeface.NORMAL);
                binding.memes.setTypeface(null, Typeface.NORMAL);
                binding.emoji.setAlpha(1);
                binding.handu.setAlpha((float) 0.5);
                binding.bugcat.setAlpha((float) 0.5);
                binding.greetings.setAlpha((float) 0.5);
                binding.memes.setAlpha((float) 0.5);
                stickindex = 1;
            }
        });
        binding.greetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.stcikerscroll1.setVisibility(View.GONE);
                binding.stcikerscroll.setVisibility(View.GONE);
                binding.memescroll.setVisibility(View.GONE);
                binding.catscroll.setVisibility(View.GONE);
                binding.greetingscroll.setVisibility(View.VISIBLE);
                binding.emoji.setTypeface(null, Typeface.NORMAL);
                binding.handu.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setTypeface(null, Typeface.NORMAL);
                binding.greetings.setTypeface(null, Typeface.BOLD);
                binding.memes.setTypeface(null, Typeface.NORMAL);
                binding.greetings.setAlpha(1);
                binding.handu.setAlpha((float) 0.5);
                binding.bugcat.setAlpha((float) 0.5);
                binding.emoji.setAlpha((float) 0.5);
                binding.memes.setAlpha((float) 0.5);

                stickindex = 3;
            }
        });
        binding.handu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.stcikerscroll.setVisibility(View.GONE);
                binding.greetingscroll.setVisibility(View.GONE);
                binding.memescroll.setVisibility(View.GONE);
                binding.catscroll.setVisibility(View.GONE);
                binding.stcikerscroll1.setVisibility(View.VISIBLE);
                binding.handu.setTypeface(null, Typeface.BOLD);
                binding.emoji.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setTypeface(null, Typeface.NORMAL);
                binding.greetings.setTypeface(null, Typeface.NORMAL);
                binding.memes.setTypeface(null, Typeface.NORMAL);
                binding.handu.setAlpha(1);
                binding.emoji.setAlpha((float) 0.5);
                binding.bugcat.setAlpha((float) 0.5);
                binding.greetings.setAlpha((float) 0.5);
                binding.memes.setAlpha((float) 0.5);

                stickindex = 2;
            }
        });
        binding.memes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.stcikerscroll.setVisibility(View.GONE);
                binding.greetingscroll.setVisibility(View.GONE);
                binding.stcikerscroll1.setVisibility(View.GONE);
                binding.catscroll.setVisibility(View.GONE);
                binding.memescroll.setVisibility(View.VISIBLE);
                binding.memes.setTypeface(null, Typeface.BOLD);
                binding.emoji.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setTypeface(null, Typeface.NORMAL);
                binding.greetings.setTypeface(null, Typeface.NORMAL);
                binding.handu.setTypeface(null, Typeface.NORMAL);
                binding.memes.setAlpha(1);
                binding.emoji.setAlpha((float) 0.5);
                binding.bugcat.setAlpha((float) 0.5);
                binding.greetings.setAlpha((float) 0.5);
                binding.handu.setAlpha((float) 0.5);
                stickindex = 4;
            }
        });

        binding.bugcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.stcikerscroll.setVisibility(View.GONE);
                binding.greetingscroll.setVisibility(View.GONE);
                binding.stcikerscroll1.setVisibility(View.GONE);
                binding.memescroll.setVisibility(View.GONE);
                binding.catscroll.setVisibility(View.VISIBLE);
                binding.memes.setTypeface(null, Typeface.NORMAL);
                binding.emoji.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setTypeface(null, Typeface.BOLD);
                binding.greetings.setTypeface(null, Typeface.NORMAL);
                binding.handu.setTypeface(null, Typeface.NORMAL);
                binding.bugcat.setAlpha(1);
                binding.emoji.setAlpha((float) 0.5);
                binding.memes.setAlpha((float) 0.5);
                binding.greetings.setAlpha((float) 0.5);
                binding.handu.setAlpha((float) 0.5);
                stickindex = 5;
            }
        });

        binding.sticker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_1");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_2");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_3");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_4");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_5");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_6");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_7");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_8");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_9");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_10");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_11");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_12");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_13");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_14");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_15");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_16");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_17");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_18");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_19");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_20");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_21");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_22");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_23");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_24");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.sticker25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("sticker_25");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });

        binding.hand1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_1");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_2");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_3");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_4");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_5");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_6");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_7");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_8");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_9");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_10");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_11");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_12");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_14");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_15");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_16");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_17");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.hand18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("hand_18");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });

        binding.greet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_1");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_2");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_3");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_4");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_5");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_6");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_7");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_8");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_9");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_10");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_11");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_12");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_13");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_14");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_15");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_16");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_17");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_18");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.greet19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("greet_19");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });

        binding.meme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_1");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_2");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_3");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_4");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_5");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_6");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_7");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_8");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_9");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_10");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_11");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_12");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_13");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_14");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_15");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_16");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_17");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_18");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_19");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_20");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_21");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_22");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_23");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_24");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_25");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_26");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_27");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_28");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.meme29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("meme_29");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });

        binding.cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_1");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_2");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_3");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_4");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_5");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_6");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_7");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_8");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_9");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_10");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_11");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_12");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_13");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_14");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_15");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_16");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_17");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_18");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_19");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_20");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_21");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_22");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_23");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_24");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });
        binding.cat25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg1.getText().toString();
                final MessageModel model = new MessageModel(senderId, msg);
                model.setMsg("*Sticker*");
                model.setStickers("cat_25");

                database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        });
            }
        });


        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        adapter = new ChatAdapter(messageModels, this);
        binding.ChatRecycle.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.ChatRecycle.setLayoutManager(layoutManager);

        SenderRoom = senderId + recieveId;
        RecieverRoom = recieveId + senderId;

        database.getReference().child("chats").child(SenderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = snapshot1.getValue(MessageModel.class);
                    messageModels.add(model);
                    binding.ChatRecycle.smoothScrollToPosition(binding.ChatRecycle.getAdapter().getItemCount());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendmsg1.setOnRichContentListener(new RichContentEditText.OnRichContentListener() {
            // Called when a keyboard sends rich content
            @Override
            public void onRichContent(Uri contentUri, ClipDescription description) {
                if (description.getMimeTypeCount() > 0) {
                    final String fileExtension = MimeTypeMap.getSingleton()
                            .getExtensionFromMimeType(description.getMimeType(0));
                    final String filename = "VibeX" + fileExtension;
                    final File richfile = new File(getFilesDir(), filename);
                    if (!writeTofileFRomContentUri(richfile, contentUri)) {
                        Toast.makeText(ChatDetailedActivity.this,
                                "Failed", Toast.LENGTH_LONG).show();
                    } else {
                        String msg = binding.sendmsg1.getText().toString();
                        final MessageModel model = new MessageModel(senderId, msg);
                        model.setMsg("*GIF*");
                        model.setWebUrl("file://" + richfile.getAbsolutePath());
                        database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        adapter.notifyDataSetChanged();
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
        });

        binding.sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dec == 2) {
                    String msg = binding.sendmsg1.getText().toString();
                    final MessageModel model = new MessageModel(senderId, msg);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Calendar ctime = Calendar.getInstance();
                    SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm");
                    final String savetime = currenttime.format(ctime.getTime());
                    model.setTime(savetime);
                    //getToken(msg,Constants.CHANNEL_ID,profile);
                    binding.sendmsg1.setText("");

                    database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                } else {
                    Toast.makeText(ChatDetailedActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean writeTofileFRomContentUri(File file, Uri uri) {
        if (file == null || uri == null) return false;
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            OutputStream output = new FileOutputStream(file);
            if (stream == null) return false;
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = stream.read(buffer)) != -1) output.write(buffer, 0, read);
            output.flush();
            output.close();
            stream.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("TAG", "Couldn't open stream: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TAG", "IOException on stream: " + e.getMessage());
        }
        return false;
    }

    private void buttonClicked(int index) {
        Toast.makeText(this, "Clicked" + index, Toast.LENGTH_SHORT).show();
        if (index == 0) {
            Intent image = new Intent();
            image.setAction(Intent.ACTION_GET_CONTENT);
            image.setType("image/*");
            startActivityForResult(image, 20);
        }
        if (index == 1) {
            Intent video = new Intent();
            video.setAction(Intent.ACTION_GET_CONTENT);
            video.setType("video/*");
            startActivityForResult(video, 30);
        }
        if (index == 4) {
            Intent cont = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(cont, 40);
        }
        if (index == 2) {
            Intent aud = new Intent();
            aud.setAction(Intent.ACTION_GET_CONTENT);
            aud.setType("audio/*");
            startActivityForResult(aud, 90);
        }
        if (index == 3) {
            String msg = binding.sendmsg1.getText().toString();
            final MessageModel model = new MessageModel(senderId, msg);
            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm");
            final String savetime = currenttime.format(ctime.getTime());

            model.setTime(savetime);
            //getToken(msg,Constants.CHANNEL_ID,profile);
            binding.sendmsg1.setText("");
            model.setMsg("*Location*");
            database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectimage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference ref = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    ref.putFile(selectimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();

                                        String msg = binding.sendmsg1.getText().toString();
                                        final MessageModel model = new MessageModel(senderId, msg);
                                        Calendar ctime = Calendar.getInstance();
                                        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm");
                                        final String savetime = currenttime.format(ctime.getTime());
                                        model.setMsg("*Photo*");
                                        model.setTime(savetime);
                                        model.setImageUrl(filepath);
                                        binding.sendmsg1.setText("");

                                        database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        adapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        } else if (requestCode == 30) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectvideo = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    reference.putFile(selectvideo).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String msg = binding.sendmsg1.getText().toString();
                                        final MessageModel model = new MessageModel(senderId, msg);
                                        Calendar ctime = Calendar.getInstance();
                                        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm");
                                        final String savetime = currenttime.format(ctime.getTime());
                                        model.setMsg("*Video*");
                                        model.setTime(savetime);
                                        model.setVideoUrl(filepath);

                                        binding.sendmsg1.setText("");

                                        database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        adapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        } else if (requestCode == 90) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectaudio = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    reference.putFile(selectaudio).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String msg = binding.sendmsg1.getText().toString();
                                        final MessageModel model = new MessageModel(senderId, msg);
                                        Calendar ctime = Calendar.getInstance();
                                        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm");
                                        final String savetime = currenttime.format(ctime.getTime());
                                        model.setMsg("*Audio*");
                                        model.setTime(savetime);
                                        model.setAudioUrl(filepath);

                                        binding.sendmsg1.setText("");

                                        database.getReference().child("chats").child(SenderRoom).push().setValue(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats").child(RecieverRoom).push().setValue(model)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        database.getReference().child("chats").child("time").push().setValue(model.getTime())
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        adapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (menu == 0) {
            binding.menu.setVisibility(View.GONE);
            binding.more.setBackgroundResource(R.drawable.more);
            binding.more.setScaleX((float) 1.0);
            binding.more.setScaleY((float) 1.0);
            menu = 1;
        } else {
            finish();
        }
        super.onBackPressed();
    }
}