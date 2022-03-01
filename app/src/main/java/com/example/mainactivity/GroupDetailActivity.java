package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mainactivity.Adapters.ChatRoomAdapter;
import com.example.mainactivity.Adapters.GroupChatAdapter;
import com.example.mainactivity.Adapters.HaveNotAdapter;
import com.example.mainactivity.Adapters.NamesAdapter;
import com.example.mainactivity.Adapters.haveAdapter;
import com.example.mainactivity.databinding.ActivityGroupDetailBinding;
import com.example.mainactivity.models.GroupChat;
import com.example.mainactivity.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import render.animations.Attention;
import render.animations.Fade;
import render.animations.Render;
import render.animations.Zoom;

public class GroupDetailActivity extends AppCompatActivity {
    ActivityGroupDetailBinding binding;
    String groupId,GroupDesc,GroupIcon;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    String senderId;
    GroupChatAdapter adapter;

    int a, stickindex;
    ArrayList<Users> list = new ArrayList<>();
    ArrayList<Users> lists = new ArrayList<>();
    ArrayList<Users> lists2 = new ArrayList<>();
    private List<String> allList;
    private List<String> allhavelist;
    private List<String> allhavenotlist;
    ChatRoomAdapter adapter2;
    NamesAdapter namesAdapter;
    HaveNotAdapter adapterhavenot;
    haveAdapter adapterhave;
    float count1, count2, totalcount, countskip, perc1, perc2;
    private ArrayList<GroupChat> groupChatList;
    String Role;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityGroupDetailBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        a = 1;
        stickindex = 1;
        binding.sender2.setImageResource(R.drawable.send_button);
        senderId = auth.getUid();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        allList = new ArrayList<>();
        allhavelist = new ArrayList<>();
        allhavenotlist = new ArrayList<>();
        getParticipants();
        adapter2 = new ChatRoomAdapter(list, GroupDetailActivity.this);
        binding.RoomRecycle.setAdapter(adapter2);

        namesAdapter = new NamesAdapter(list, GroupDetailActivity.this);
        binding.namecycle.setAdapter(namesAdapter);

        binding.ChatRecycle2.onScrolled(0,100);
        binding.sender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.sendmsg2.getText().toString();
                sendMessage(msg);
            }
        });
        binding.linearLayout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupDetailActivity.this, ParticipantsActivity.class);
                intent1.putExtra("groupId", groupId);
                startActivity(intent1);

            }
        });
        binding.card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.playPsych.getVisibility() == View.GONE) {
                    binding.playPsych.setVisibility(View.VISIBLE);
                } else if (binding.playPsych.getVisibility() == View.VISIBLE) {
                    binding.playPsych.setVisibility(View.GONE);
                }
            }
        });
        binding.car2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.playPsych2.getVisibility() == View.GONE) {
                    binding.playPsych2.setVisibility(View.VISIBLE);
                } else if (binding.playPsych2.getVisibility() == View.VISIBLE) {
                    binding.playPsych2.setVisibility(View.GONE);
                }
            }
        });
        binding.grpgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Render render = new Render(GroupDetailActivity.this);
                render.setAnimation(Attention.RuberBand(binding.grpgame));
                render.start();
                if (binding.games.getVisibility() == View.GONE) {
                    Render render2 = new Render(GroupDetailActivity.this);
                    render2.setAnimation(Fade.In(binding.games));
                    render2.start();
                    binding.games.setVisibility(View.VISIBLE);
                } else if (binding.games.getVisibility() == View.VISIBLE) {
                    Render render3 = new Render(GroupDetailActivity.this);
                    render3.setAnimation(Fade.Out(binding.games));
                    render3.start();
                    binding.games.setVisibility(View.GONE);
                }
            }
        });

        loadGroupInfo();
        loadgrpmsg();

        binding.boom2.setButtonEnum(ButtonEnum.SimpleCircle);
        binding.boom2.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_1);
        binding.boom2.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_1);

        int number = binding.boom2.getButtonPlaceEnum().buttonNumber();
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
            binding.boom2.addBuilder(builder);
        }

        binding.playPsych.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.games.setVisibility(View.GONE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                ref.child(groupId).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Role = "" + snapshot.child("role").getValue();
                        if (Role.equals("creator")) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("rather", "started");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups");
                            reference.child(groupId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("groups");
                                    ref1.child(groupId).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String newRole = "" + snapshot.child("role").getValue();
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                            ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        String Rather = "" + snapshot1.child("rather").getValue();
                                                        if (Rather.equals("started")) {
                                                            Toast.makeText(GroupDetailActivity.this, "passed1", Toast.LENGTH_SHORT).show();
                                                            if (newRole.equals("participant") || newRole.equals("admin") || newRole.equals("creator")) {
                                                                binding.sendmsg2.setText("");
                                                                Dialog dialog = new Dialog(GroupDetailActivity.this);

                                                                dialog.setContentView(R.layout.sample_system);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                TextView option1, option2, optionsel, percent1, percent2;
                                                                Button exit, next;
                                                                LottieAnimationView timer;
                                                                ImageView check1, check2;
                                                                option1 = dialog.findViewById(R.id.option1);
                                                                option2 = dialog.findViewById(R.id.option2);
                                                                timer = dialog.findViewById(R.id.wall);
                                                                optionsel = dialog.findViewById(R.id.optionselector);
                                                                exit = dialog.findViewById(R.id.end);
                                                                next = dialog.findViewById(R.id.next);
                                                                percent1 = dialog.findViewById(R.id.perct1);
                                                                percent2 = dialog.findViewById(R.id.perct2);
                                                                check1 = dialog.findViewById(R.id.checkop1);
                                                                check2 = dialog.findViewById(R.id.checkop2);
                                                                option1.setText("Option 1");
                                                                option2.setText("Option 2");
                                                                String opt_1[] = {"know you were about to die of cancer", "fight one bear-sized duck", " have a terrible boss and a good job",
                                                                        "kiss in Paris", "go on a video call date", " be the strongest man on earth ", "follow your dreams",
                                                                        "meet the girl of your dreams in ten years", "live alone", "use a dating app", "lose your sight", "never be able to go out during the day",
                                                                        "vomit on your hero", "communicate only in emoji", "be forced to live the same day over and over again for a full year",
                                                                        "have a third nipple", "be forced to listen to the same 10 songs on repeat for the rest of your life", "have your hair pulled",
                                                                        "be funny but really stupid", "give up sex", "have your name tattooed on your forehead", "know how you die", "have an intelligent partner",
                                                                        "lose all of your teeth", "pay twice as much for a plane ticket", "always stink and not know it", "get banned from Snapchat",
                                                                        "spend one night with your most hated ex", "eat your hair trimmings after every haircut", "wash all your dishes using your tongue",
                                                                        "be the opposite gender for one day", "fart loudly in an elevator", "laugh uncontrollably when you’re sad", "be gossiped about",
                                                                        "lose all your money", "change your appearance", "cuddle with a giant slug", "have one partner", "get a paper cut between your fingers every time you turn a page",
                                                                        "always be itchy", "spend a day with a loved one that has passed away", "only use Reddit for the rest of your life", "have no eyebrows",
                                                                        "Would you rather post an embarrassing, drunken picture of you to your Instagram story that your crush sees", "live with no electronics"

                                                                };
                                                                String opt_2[] = {"live the remainder of your life unknowing", "ten duck-sized bears", "good boss and a terrible job", "kiss in a tent in the woods",
                                                                        "socially-distanced date", "the smartest man on earth", "become wealthy", "meet ten girls now", "or with roommates",
                                                                        "go on a blind date", "or your memories", "never be able to go out at night", "have your hero vomit on you", " never be able to text at all ever again",
                                                                        "or take 3 years off the end of your life", "or an extra toe", " forced to watch the same 5 movies on repeat for the rest of your life",
                                                                        "or your back scratched", "boring but really smart", "give up food", "have no front teeth", "know when you die", "a good-looking partner",
                                                                        "or all of your hair", "or never be able to fly", "always smell something that stinks that no one else smells", " accidentally post an unflattering selfie to your story",
                                                                        "live on the streets for a week", "eat your nail clippings each time you cut your nails", "wash your pets using your tongue",
                                                                        "be any animal for one day", "fart loudly in a public restroom with an occupant the next stall over", "cry like a baby when you’re happy", "to be ignored forever",
                                                                        "all your pictures", "change your personality", "cuddle with a giant spider", "multiple partners", "bite your tongue every time you eat food",
                                                                        "always sweat", "get to explore a day in the year 3021", "only use Youtube for the rest of your life", "or no eyelashes",
                                                                        "send your crush an embarrassing direct message", "live with no friends"

                                                                };
                                                                if (newRole.equals("creator")) {
                                                                    next.setVisibility(View.VISIBLE);
                                                                    exit.setText("END");
                                                                    next.setText("NEXT");
                                                                } else if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                    exit.setText("LEAVE");
                                                                    next.setText("START");
                                                                }
                                                                option1.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        check1.setVisibility(View.VISIBLE);
                                                                        check2.setVisibility(View.GONE);
                                                                    }
                                                                });
                                                                option2.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        check2.setVisibility(View.VISIBLE);
                                                                        check1.setVisibility(View.GONE);
                                                                    }
                                                                });
                                                                option1.setClickable(false);
                                                                option2.setClickable(false);
                                                                next.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                            next.setVisibility(View.GONE);
                                                                        }
                                                                        if (next.getText().equals("NEXT")) {
                                                                            DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference("groups");
                                                                            reff3.child(groupId).child("Rather").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            reff3.child(groupId).child("Rather1").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            reff3.child(groupId).child("Rather2").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            timer.playAnimation();
                                                                            check1.setVisibility(View.GONE);
                                                                            check2.setVisibility(View.GONE);
                                                                            countDownTimer = new CountDownTimer(10000, 1000) {
                                                                                @Override
                                                                                public void onTick(long millisUntilFinished) {
                                                                                    next.setClickable(false);
                                                                                    next.setAlpha((float) 0.5);
                                                                                    option1.setClickable(true);
                                                                                    option2.setClickable(true);
                                                                                }

                                                                                @Override
                                                                                public void onFinish() {
                                                                                    next.setClickable(true);
                                                                                    next.setAlpha(1);
                                                                                    timer.pauseAnimation();
                                                                                    timer.setProgress(0);
                                                                                    if (check1.getVisibility() == View.VISIBLE) {
                                                                                        optionsel.setText("option1");
                                                                                        if (optionsel.getText().equals("option1")) {
                                                                                            HashMap<String, Object> vote = new HashMap<>();
                                                                                            vote.put("vote", optionsel.getText().toString());
                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                            ref0.child(groupId).child("Rather1").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                    updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Rather1").addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            count1 = snapshot.getChildrenCount();
                                                                                                            percent1.setText("" + count1);
                                                                                                            totalcount = count1 + count2;
                                                                                                            perc1 = (count1 / totalcount) * 100;
                                                                                                            perc2 = (count2 / totalcount) * 100;
                                                                                                            percent1.setText("" + perc1 + "%");
                                                                                                            percent2.setText("" + perc2 + "%");
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    } else if (check2.getVisibility() == View.VISIBLE) {
                                                                                        optionsel.setText("option2");
                                                                                        if (optionsel.getText().equals("option2")) {
                                                                                            HashMap<String, Object> vote = new HashMap<>();
                                                                                            vote.put("vote", optionsel.getText().toString());
                                                                                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                            ref1.child(groupId).child("Rather2").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                    updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Rather2").addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            count2 = snapshot.getChildrenCount();
                                                                                                            percent2.setText("" + count2);
                                                                                                            totalcount = count1 + count2;
                                                                                                            perc1 = (count1 / totalcount) * 100;
                                                                                                            perc2 = (count2 / totalcount) * 100;
                                                                                                            percent1.setText("" + perc1 + "%");
                                                                                                            percent2.setText("" + perc2 + "%");
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    } else {
                                                                                        optionsel.setText("");
                                                                                        if (optionsel.getText().equals("")) {
                                                                                            HashMap<String, Object> vote = new HashMap<>();
                                                                                            vote.put("vote", optionsel.getText().toString());
                                                                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                            ref2.child(groupId).child("Rather").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                    updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Rather").addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            countskip = snapshot.getChildrenCount();
                                                                                                            totalcount = count1 + count2;
                                                                                                            perc1 = (count1 / totalcount) * 100;
                                                                                                            perc2 = (count2 / totalcount) * 100;
                                                                                                            percent1.setText("" + perc1 + "%");
                                                                                                            percent2.setText("" + perc2 + "%");
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                    option1.setClickable(false);
                                                                                    option2.setClickable(false);
                                                                                    check1.setVisibility(View.GONE);
                                                                                    check2.setVisibility(View.GONE);
                                                                                }
                                                                            }.start();

                                                                            int min_val = 0;
                                                                            int max_val = 44;
                                                                            ThreadLocalRandom tlr = ThreadLocalRandom.current();
                                                                            int randomNum = tlr.nextInt(min_val, max_val + 1);
                                                                            String a = (opt_1[randomNum]);
                                                                            String b = (opt_2[randomNum]);
                                                                            HashMap<String, Object> hashMap3 = new HashMap<>();
                                                                            hashMap3.put("option1", a);
                                                                            hashMap3.put("option2", b);
                                                                            hashMap3.put("rather", "playing");
                                                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref2.child(groupId).updateChildren(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (newRole.equals("creator")) {
                                                                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                    String opt1_ = "" + snapshot1.child("option1").getValue();
                                                                                                    String opt2_ = "" + snapshot1.child("option2").getValue();
                                                                                                    option1.setText(opt1_);
                                                                                                    option2.setText(opt2_);
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        } else {
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref.child(groupId).child("option1").addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    timer.playAnimation();
                                                                                    check1.setVisibility(View.GONE);
                                                                                    check2.setVisibility(View.GONE);
                                                                                    CountDownTimer countDownTimer1 = new CountDownTimer(10000, 1000) {
                                                                                        @Override
                                                                                        public void onTick(long millisUntilFinished) {
                                                                                            option1.setClickable(true);
                                                                                            option2.setClickable(true);
                                                                                        }


                                                                                        @Override
                                                                                        public void onFinish() {

                                                                                            timer.pauseAnimation();
                                                                                            timer.setProgress(0);
                                                                                            if (check1.getVisibility() == View.VISIBLE) {
                                                                                                optionsel.setText("option1");
                                                                                                if (optionsel.getText().equals("option1")) {
                                                                                                    HashMap<String, Object> vote = new HashMap<>();
                                                                                                    vote.put("vote", optionsel.getText().toString());
                                                                                                    DatabaseReference reff = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    reff.child(groupId).child("Rather1").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                            updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                            ref0.child(groupId).child("Rather1").addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    count1 = snapshot.getChildrenCount();
                                                                                                                    totalcount = count1 + count2;
                                                                                                                    perc1 = (count1 / totalcount) * 100;
                                                                                                                    perc2 = (count2 / totalcount) * 100;
                                                                                                                    percent1.setText("" + perc1 + "%");
                                                                                                                    percent2.setText("" + perc2 + "%");
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            } else if (check2.getVisibility() == View.VISIBLE) {
                                                                                                optionsel.setText("option2");
                                                                                                if (optionsel.getText().equals("option2")) {
                                                                                                    HashMap<String, Object> vote = new HashMap<>();
                                                                                                    vote.put("vote", optionsel.getText().toString());
                                                                                                    DatabaseReference reff2 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    reff2.child(groupId).child("Rather2").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                            updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                            ref0.child(groupId).child("Rather2").addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    count2 = snapshot.getChildrenCount();
                                                                                                                    totalcount = count1 + count2;
                                                                                                                    perc1 = (count1 / totalcount) * 100;
                                                                                                                    perc2 = (count2 / totalcount) * 100;
                                                                                                                    percent1.setText("" + perc1 + "%");
                                                                                                                    percent2.setText("" + perc2 + "%");
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            } else {
                                                                                                optionsel.setText("");
                                                                                                if (optionsel.getText().equals("")) {
                                                                                                    HashMap<String, Object> vote = new HashMap<>();
                                                                                                    vote.put("vote", optionsel.getText().toString());
                                                                                                    DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    reff3.child(groupId).child("Rather").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                            updateChildren(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                            ref0.child(groupId).child("Rather").addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    countskip = snapshot.getChildrenCount();
                                                                                                                    totalcount = count1 + count2;
                                                                                                                    perc1 = (count1 / totalcount) * 100;
                                                                                                                    perc2 = (count2 / totalcount) * 100;
                                                                                                                    percent1.setText("" + perc1 + "%");
                                                                                                                    percent2.setText("" + perc2 + "%");
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                            option1.setClickable(false);
                                                                                            option2.setClickable(false);
                                                                                            check1.setVisibility(View.GONE);
                                                                                            check2.setVisibility(View.GONE);
                                                                                        }
                                                                                    }.start();
                                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                    String opt1_ = "" + snapshot1.child("option1").getValue();
                                                                                                    String opt2_ = "" + snapshot1.child("option2").getValue();
                                                                                                    option1.setText(opt1_);
                                                                                                    option2.setText(opt2_);
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                exit.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if (newRole.equals("creator")) {
                                                                            dialog.dismiss();
                                                                            HashMap<String, Object> hashMap2 = new HashMap<>();
                                                                            //hashMap2.put("rather", "ends");
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref.child(groupId).updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    reff3.child(groupId).child("Rather").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("Rather1").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("Rather2").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("option1").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("option2").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("rather").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    dialog.dismiss();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(GroupDetailActivity.this, "You Left The Game", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                                dialog.setCancelable(false);
                                                                dialog.show();
                                                                if (Rather.equals("ends") || Rather.equals(null)) {
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(GroupDetailActivity.this, "Only Host Can Start Games", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.playPsych2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.games.setVisibility(View.GONE);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                ref.child(groupId).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Role = "" + snapshot.child("role").getValue();
                        if (Role.equals("creator")) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("neverever", "started");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups");
                            reference.child(groupId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("groups");
                                    ref4.child(groupId).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String newRole = "" + snapshot.child("role").getValue();
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                            ref.orderByChild("groupId").equalTo(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        String Never = "" + snapshot1.child("neverever").getValue();
                                                        if (Never.equals("started")) {
                                                            if (newRole.equals("participant") || newRole.equals("admin") || newRole.equals("creator")) {
                                                                binding.sendmsg2.setText("");
                                                                Dialog dialog2 = new Dialog(GroupDetailActivity.this);
                                                                dialog2.setContentView(R.layout.nevergame);
                                                                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                TextView question,texthave,textnever;
                                                                Button exit, next;
                                                                LottieAnimationView timer;
                                                                RecyclerView havelist, havenotlist;
                                                                ImageView ihave, ihavenever;
                                                                question = dialog2.findViewById(R.id.question);
                                                                ihave = dialog2.findViewById(R.id.ihave);
                                                                timer = dialog2.findViewById(R.id.timer2);
                                                                ihavenever = dialog2.findViewById(R.id.ihavenot);
                                                                exit = dialog2.findViewById(R.id.end2);
                                                                next = dialog2.findViewById(R.id.next2);
                                                                texthave = dialog2.findViewById(R.id.texthave);
                                                                textnever = dialog2.findViewById(R.id.textnever);
                                                                havelist = dialog2.findViewById(R.id.haveList);
                                                                havenotlist = dialog2.findViewById(R.id.havenotlist);
                                                                adapterhave = new haveAdapter(lists, GroupDetailActivity.this);
                                                                havelist.setAdapter(adapterhave);
                                                                adapterhavenot = new HaveNotAdapter(lists2, GroupDetailActivity.this);
                                                                havenotlist.setAdapter(adapterhavenot);
                                                                String quest[] = {"Used a fake ID", "Broken up with someone", "Skipped out on a bill", "Fought in public", "Kissed someone in public", "Trolled someone on social media",
                                                                        "Snooped through someone’s stuff", "Went 24 hours without showering", "Went on a solo vacation", "Went viral online", "Slept outdoors for an entire night", "Left someone on read",
                                                                        "Participated in a protest", "Deleted a post on social media because it didn’t get enough likes", "Worn someone else’s underwear", "Binged an entire series in one day", "Cheated on a partner",
                                                                        "Peed in a pool", "Convinced a friend to dump a partner", "Been awake for 24 straight hours or more", "Thought a cartoon character was hot", "Tried to make an ex jealous", "Dated someone over ten years older",
                                                                        "Flashed someone", "Rubbed someone else's toothbrush in something other than toothpaste", "Shoplifted", "Been kicked out of a pub/club/bar", "Gotten a tattoo", "Had a crush on a teacher", "Had a crush on a friend's sibling",
                                                                        "Met a celebrity", "Used a dating app", "Broken a bone", "Cried at school", "Stolen anything", "Used someone else's Netflix password", "Been sent to the principal's office as a kid", "Re-gifted a gift",
                                                                        "DMed a celebrity", "Ignored someone I knew in public", "Edited my selfies", "Googled my own name", "Dropped my phone in a toilet", "Jumped in a pool with all my clothes on", "Stayed out past three in the morning", "Unfollowed a friend on social media",
                                                                        "Stolen money from a family member", "Gone to a bar or club completely alone", "Escaped from class", "Fall in love with anyone through social network", "Stuck gum under a desk", "Screwed up at school", "Faked A Sick Voice To Get A Leave From Office",
                                                                        "Pretended To Be On A Call To Ignore Someone", "Eaten Food That Fell On The Floor", "Used My Phone In The Bathroom", "Prank-Called Someone I Hated"
                                                                };
                                                                if (newRole.equals("creator")) {
                                                                    next.setVisibility(View.VISIBLE);
                                                                    exit.setText("END");
                                                                    next.setText("NEXT");
                                                                } else if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                    exit.setText("LEAVE");
                                                                    next.setText("START");
                                                                }
                                                                next.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                            next.setVisibility(View.GONE);
                                                                        }
                                                                        if (next.getText().equals("NEXT")) {
                                                                            DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference("groups");
                                                                            reff3.child(groupId).child("Ihave").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            reff3.child(groupId).child("Ihavenot").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                }
                                                                            });
                                                                            timer.playAnimation();
                                                                            ihave.setVisibility(View.VISIBLE);
                                                                            ihavenever.setVisibility(View.VISIBLE);
                                                                            texthave.setVisibility(View.VISIBLE);
                                                                            textnever.setVisibility(View.VISIBLE);
                                                                            countDownTimer = new CountDownTimer(10000, 1000) {
                                                                                @Override
                                                                                public void onTick(long millisUntilFinished) {
                                                                                    next.setClickable(false);
                                                                                    next.setAlpha((float) 0.5);
                                                                                    havelist.setVisibility(View.GONE);
                                                                                    havenotlist.setVisibility(View.GONE);

                                                                                    ihave.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            ihave.setClickable(false);
                                                                                            ihavenever.setVisibility(View.GONE);
                                                                                            textnever.setVisibility(View.GONE);
                                                                                            Render render = new Render(GroupDetailActivity.this);
                                                                                            render.setAnimation(Zoom.In(ihave));
                                                                                            render.start();
                                                                                            HashMap<String, Object> never = new HashMap<>();
                                                                                            never.put("vote", "Ihave");
                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                            ref0.child(groupId).child("Ihave").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                    updateChildren(never).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Ihave").addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            allhavelist.clear();
                                                                                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                                allhavelist.add(snapshot1.getKey());
                                                                                                            }
                                                                                                            HaveShow();
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });

                                                                                    ihavenever.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            ihave.setVisibility(View.GONE);
                                                                                            texthave.setVisibility(View.GONE);
                                                                                            ihavenever.setClickable(false);
                                                                                            Render render = new Render(GroupDetailActivity.this);
                                                                                            render.setAnimation(Zoom.In(ihavenever));
                                                                                            render.start();
                                                                                            HashMap<String, Object> never = new HashMap<>();
                                                                                            never.put("vote", "Ihavenot");
                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                            ref0.child(groupId).child("Ihavenot").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                    updateChildren(never).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Ihavenot").addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            allhavenotlist.clear();
                                                                                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                                allhavenotlist.add(snapshot1.getKey());
                                                                                                            }
                                                                                                            HaveNotShow();

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void onFinish() {
                                                                                    next.setClickable(true);
                                                                                    next.setAlpha(1);
                                                                                    ihave.setClickable(true);
                                                                                    ihavenever.setClickable(true);
                                                                                    ihavenever.setClickable(true);
                                                                                    timer.pauseAnimation();
                                                                                    timer.setProgress(0);
                                                                                    ihave.setVisibility(View.GONE);
                                                                                    ihavenever.setVisibility(View.GONE);
                                                                                    havelist.setVisibility(View.VISIBLE);
                                                                                    havenotlist.setVisibility(View.VISIBLE);
                                                                                    texthave.setVisibility(View.VISIBLE);
                                                                                    textnever.setVisibility(View.VISIBLE);

                                                                                }
                                                                            }.start();

                                                                            int min_val = 0;
                                                                            int max_val = 54;
                                                                            ThreadLocalRandom tlr = ThreadLocalRandom.current();
                                                                            int randomNum = tlr.nextInt(min_val, max_val + 1);
                                                                            String neverques = (quest[randomNum]);
                                                                            HashMap<String, Object> hashMap3 = new HashMap<>();
                                                                            hashMap3.put("question", neverques);
                                                                            hashMap3.put("neverever", "playing");
                                                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref2.child(groupId).updateChildren(hashMap3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (newRole.equals("creator")) {
                                                                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                    String nvq = "" + snapshot1.child("question").getValue();
                                                                                                    question.setText(nvq);
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        } else {
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref.child(groupId).child("question").addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    timer.playAnimation();
                                                                                    ihave.setVisibility(View.VISIBLE);
                                                                                    ihavenever.setVisibility(View.VISIBLE);
                                                                                    texthave.setVisibility(View.VISIBLE);
                                                                                    textnever.setVisibility(View.VISIBLE);
                                                                                    CountDownTimer countDownTimer1 = new CountDownTimer(10000, 1000) {
                                                                                        @Override
                                                                                        public void onTick(long millisUntilFinished) {
                                                                                            havelist.setVisibility(View.GONE);
                                                                                            havenotlist.setVisibility(View.GONE);
                                                                                            ihave.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    ihave.setClickable(false);
                                                                                                    ihavenever.setVisibility(View.GONE);
                                                                                                    textnever.setVisibility(View.GONE);
                                                                                                    Render render = new Render(GroupDetailActivity.this);
                                                                                                    render.setAnimation(Zoom.In(ihave));
                                                                                                    render.start();
                                                                                                    HashMap<String, Object> never = new HashMap<>();
                                                                                                    never.put("vote", "Ihave");
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Ihave").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                            updateChildren(never).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                            ref0.child(groupId).child("Ihave").addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    allhavelist.clear();
                                                                                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                                        allhavelist.add(snapshot1.getKey());
                                                                                                                    }
                                                                                                                    HaveShow();
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });

                                                                                            ihavenever.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    ihave.setVisibility(View.GONE);
                                                                                                    texthave.setVisibility(View.GONE);
                                                                                                    ihavenever.setClickable(false);
                                                                                                    Render render = new Render(GroupDetailActivity.this);
                                                                                                    render.setAnimation(Zoom.In(ihavenever));
                                                                                                    render.start();
                                                                                                    HashMap<String, Object> never = new HashMap<>();
                                                                                                    never.put("vote", "Ihavenot");
                                                                                                    DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                    ref0.child(groupId).child("Ihavenot").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                                                                            updateChildren(never).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            DatabaseReference ref0 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                                            ref0.child(groupId).child("Ihavenot").addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    allhavenotlist.clear();
                                                                                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                                        allhavenotlist.add(snapshot1.getKey());
                                                                                                                    }
                                                                                                                    HaveNotShow();
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });
                                                                                        }


                                                                                        @Override
                                                                                        public void onFinish() {

                                                                                            timer.pauseAnimation();
                                                                                            timer.setProgress(0);
                                                                                            ihave.setClickable(true);
                                                                                            ihavenever.setClickable(true);
                                                                                            texthave.setVisibility(View.VISIBLE);
                                                                                            textnever.setVisibility(View.VISIBLE);
                                                                                            ihave.setVisibility(View.GONE);
                                                                                            ihavenever.setVisibility(View.GONE);
                                                                                            havelist.setVisibility(View.VISIBLE);
                                                                                            havenotlist.setVisibility(View.VISIBLE);
                                                                                        }
                                                                                    }.start();
                                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (newRole.equals("participant") || newRole.equals("admin")) {
                                                                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                                    String nvq = "" + snapshot1.child("question").getValue();
                                                                                                    question.setText(nvq);
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });

                                                                exit.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if (newRole.equals("creator")) {
                                                                            dialog2.dismiss();
                                                                            HashMap<String, Object> hashMap2 = new HashMap<>();
                                                                            //hashMap2.put("neverever", "ends");
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                                                            ref.child(groupId).updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference("groups");
                                                                                    reff3.child(groupId).child("neverever").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("question").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    reff3.child(groupId).child("Ihave").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                   reff3.child(groupId).child("Ihavenot").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                        }
                                                                                    });
                                                                                    dialog2.dismiss();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(GroupDetailActivity.this, "You Left The Game", Toast.LENGTH_SHORT).show();
                                                                            dialog2.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                                dialog2.setCancelable(false);
                                                                dialog2.show();
                                                                if (Never.equals("ends") || Never.equals(null)) {
                                                                    dialog2.dismiss();
                                                                }
                                                            }
                                                        } else if (Never.equals("ends")) {

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(GroupDetailActivity.this, "Only Host Can Start Games", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getParticipants() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("groups").child(groupId);
        ref.child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    allList.add(snapshot1.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users users = snapshot1.getValue(Users.class);
                    for (String id : allList) {
                        if (users.getUserid().equals(id)) {
                            list.add(users);
                        }
                    }
                }
                adapter2.notifyDataSetChanged();
                namesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void HaveNotShow() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lists2.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users users = snapshot1.getValue(Users.class);
                    for (String id1 : allhavenotlist) {
                        if (users.getUserid().equals(id1)) {
                            lists2.add(users);
                        }
                    }
                }
                adapterhavenot.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void HaveShow() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lists.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users users = snapshot1.getValue(Users.class);
                    for (String id2 : allhavelist) {
                        if (users.getUserid().equals(id2)) {
                            lists.add(users);
                        }
                    }
                }
                adapterhavenot.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadgrpmsg() {
        groupChatList = new ArrayList<>();
        adapter = new GroupChatAdapter(groupChatList, GroupDetailActivity.this);
        binding.ChatRecycle2.setAdapter(adapter);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    GroupChat grpmodel = snapshot1.getValue(GroupChat.class);
                    groupChatList.add(grpmodel);
                    binding.ChatRecycle2.smoothScrollToPosition(binding.ChatRecycle2.getAdapter().getItemCount());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendMessage(String msg) {
        String time = "" + System.currentTimeMillis();
        GroupChat grpmodel = new GroupChat(auth.getCurrentUser().getUid(), msg, time);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.child(groupId).child("Messages").child(time).setValue(grpmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                binding.sendmsg2.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupDetailActivity.this, "Message Failed To Send", Toast.LENGTH_SHORT).show();
            }
        });
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

        }
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String groupTitle = "" + snapshot1.child("groupTitle").getValue();
                    String groupDesc = "" + snapshot1.child("groupDesc").getValue();
                    String groupIcon = "" + snapshot1.child("groupIcon").getValue();
                    String timestamp = "" + snapshot1.child("timestamp").getValue();
                    String createdBy = "" + snapshot1.child("createdBy").getValue();

                    binding.groupTitle.setText(groupTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                                        String msg = binding.sendmsg2.getText().toString();
                                        String time = "" + System.currentTimeMillis();
                                        GroupChat model = new GroupChat(auth.getCurrentUser().getUid(), msg, time);
                                        model.setMsg("*Photo*");
                                        model.setImageUrl(filepath);
                                        binding.sendmsg2.setText("");


                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                        ref.child(groupId).child("Messages").child(time).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                binding.sendmsg2.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GroupDetailActivity.this, "Message Failed To Send", Toast.LENGTH_SHORT).show();
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
                                        String msg = binding.sendmsg2.getText().toString();
                                        String time = "" + System.currentTimeMillis();
                                        GroupChat model = new GroupChat(auth.getCurrentUser().getUid(), msg, time);
                                        model.setMsg("*Video*");
                                        model.setVideoUrl(filepath);
                                        binding.sendmsg2.setText("");
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                        ref.child(groupId).child("Messages").child(time).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                binding.sendmsg2.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GroupDetailActivity.this, "Message Failed To Send", Toast.LENGTH_SHORT).show();
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
        else if (requestCode == 90) {
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
                                        String msg = binding.sendmsg2.getText().toString();
                                        String time = "" + System.currentTimeMillis();
                                        GroupChat model = new GroupChat(auth.getCurrentUser().getUid(), msg, time);
                                        model.setMsg("*Audio*");
                                        model.setAudioUrl(filepath);

                                        binding.sendmsg2.setText("");
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
                                        ref.child(groupId).child("Messages").child(time).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                binding.sendmsg2.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GroupDetailActivity.this, "Message Failed To Send", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String Rather = "" + snapshot1.child("rather").getValue();
                    if (Rather.equals("started")&& Role.equals("creator")) {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("rather","ends");
                        database.getReference("groups").child(groupId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                    }
                    else {
                        Toast.makeText(GroupDetailActivity.this, "Failed To Join", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String Rather = "" + snapshot1.child("rather").getValue();
                    if (Rather.equals("started")&& Role.equals("creator")) {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("rather","ends");
                        database.getReference("groups").child(groupId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                    }
                    else {
                        Toast.makeText(GroupDetailActivity.this, "Failed To Join", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}