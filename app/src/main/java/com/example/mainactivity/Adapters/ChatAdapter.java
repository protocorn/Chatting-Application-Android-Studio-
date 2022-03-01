package com.example.mainactivity.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.ChatDetailedActivity;
import com.example.mainactivity.Constants;
import com.example.mainactivity.GroupDetailActivity;
import com.example.mainactivity.PhotoView;
import com.example.mainactivity.R;
import com.example.mainactivity.Videos;
import com.example.mainactivity.models.MessageModel;
import com.example.mainactivity.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ChatAdapter extends RecyclerView.Adapter {
    private ArrayList<MessageModel> messageModels;
    private Context context;
    int Reciever_view_type = 1;
    int Sender_view_type = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())) {

            return Sender_view_type;
        } else {
            return Reciever_view_type;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Sender_view_type) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int i = position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Unsend Message");
                    builder.setMessage("Note:This Message Will Not Be Seen By Anyone After Unsending");
                    builder.setPositiveButton("Unsend", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletemessage(i);
                        }
                    });
                    builder.setNeutralButton("Copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
            if (messageModel.getMsg().equals("*Photo*")) {
                Glide.with(context).load(messageModel.getImageUrl()).into(((SenderViewHolder) holder).photo);
                ((SenderViewHolder) holder).photo.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).audio.setVisibility(View.GONE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PhotoView.class);
                        intent.putExtra("image", messageModel.getImageUrl());
                        context.startActivity(intent);
                    }
                });
            } else if (messageModel.getMsg().equals("*Video*")) {
                ((SenderViewHolder) holder).play.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).audio.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(messageModel.getVideoUrl());
                Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(500000); //unit in microsecond
                ((SenderViewHolder) holder).thumbanail.setImageBitmap(bmFrame);

                ((SenderViewHolder) holder).play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent vid = new Intent(context, Videos.class);
                        vid.putExtra("video", messageModel.getVideoUrl());
                        context.startActivity(vid);
                    }
                });
            } else if (messageModel.getMsg().equals("*Audio*")) {
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.GONE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);

            } else if (messageModel.getMsg().equals("*Location*")) {
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.GONE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).audio.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).location.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);

                ((SenderViewHolder) holder).stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SenderViewHolder) holder).stop.setVisibility(View.GONE);
                    }
                });

            } else if (messageModel.getMsg().equals("*Sticker*")) {
                ((SenderViewHolder) holder).sticker.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).audio.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.GONE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.GONE);
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);
                if (messageModel.getStickers().equals("sticker_1")) {
                    Picasso.get().load(R.drawable.new_19).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_2")) {
                    Picasso.get().load(R.drawable.new_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_3")) {
                    Picasso.get().load(R.drawable.new_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_4")) {
                    Picasso.get().load(R.drawable.new_18).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_5")) {
                    Picasso.get().load(R.drawable.new_15).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_6")) {
                    Picasso.get().load(R.drawable.new_11).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_7")) {
                    Picasso.get().load(R.drawable.new_13).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_8")) {
                    Picasso.get().load(R.drawable.new_14).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_9")) {
                    Picasso.get().load(R.drawable.new_16).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_10")) {
                    Picasso.get().load(R.drawable.new_10).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_11")) {
                    Picasso.get().load(R.drawable.new_28).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_12")) {
                    Picasso.get().load(R.drawable.new_12).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_13")) {
                    Picasso.get().load(R.drawable.new_21).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_14")) {
                    Picasso.get().load(R.drawable.new_22).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_15")) {
                    Picasso.get().load(R.drawable.new_20).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_16")) {
                    Picasso.get().load(R.drawable.new_27).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_17")) {
                    Picasso.get().load(R.drawable.stick_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_18")) {
                    Picasso.get().load(R.drawable.new_8).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_19")) {
                    Picasso.get().load(R.drawable.new_9).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_20")) {
                    Picasso.get().load(R.drawable.emoji_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_21")) {
                    Picasso.get().load(R.drawable.emoji_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_22")) {
                    Picasso.get().load(R.drawable.emoji_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_23")) {
                    Picasso.get().load(R.drawable.emoji_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_24")) {
                    Picasso.get().load(R.drawable.emoji_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("sticker_25")) {
                    Picasso.get().load(R.drawable.emoji_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_1")) {
                    Picasso.get().load(R.drawable.hand_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_2")) {
                    Picasso.get().load(R.drawable.hand_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_3")) {
                    Picasso.get().load(R.drawable.hand_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_4")) {
                    Picasso.get().load(R.drawable.hand_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_5")) {
                    Picasso.get().load(R.drawable.hand_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_6")) {
                    Picasso.get().load(R.drawable.hand_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_7")) {
                    Picasso.get().load(R.drawable.hand_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_8")) {
                    Picasso.get().load(R.drawable.hand_8).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_9")) {
                    Picasso.get().load(R.drawable.hand_9).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_10")) {
                    Picasso.get().load(R.drawable.hand_10).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_11")) {
                    Picasso.get().load(R.drawable.hand_11).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_12")) {
                    Picasso.get().load(R.drawable.hand_12).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_14")) {
                    Picasso.get().load(R.drawable.hand_14).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_15")) {
                    Picasso.get().load(R.drawable.hand_15).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_16")) {
                    Picasso.get().load(R.drawable.hand_16).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_17")) {
                    Picasso.get().load(R.drawable.hand_17).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("hand_18")) {
                    Picasso.get().load(R.drawable.hand_18).into(((SenderViewHolder) holder).sticker);
                }

                if (messageModel.getStickers().equals("greet_1")) {
                    Picasso.get().load(R.drawable.nee_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_2")) {
                    Picasso.get().load(R.drawable.new_30).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_3")) {
                    Picasso.get().load(R.drawable.new_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_4")) {
                    Picasso.get().load(R.drawable.new_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_5")) {
                    Picasso.get().load(R.drawable.new_25).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_6")) {
                    Picasso.get().load(R.drawable.new_26).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_7")) {
                    Picasso.get().load(R.drawable.new_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_8")) {
                    Picasso.get().load(R.drawable.new_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_9")) {
                    Picasso.get().load(R.drawable.greet_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_10")) {
                    Picasso.get().load(R.drawable.greet_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_11")) {
                    Picasso.get().load(R.drawable.greet_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_12")) {
                    Picasso.get().load(R.drawable.greet_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_13")) {
                    Picasso.get().load(R.drawable.greet_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_14")) {
                    Picasso.get().load(R.drawable.greet_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_15")) {
                    Picasso.get().load(R.drawable.greet_8).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_16")) {
                    Picasso.get().load(R.drawable.greet_11).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_17")) {
                    Picasso.get().load(R.drawable.greet_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_18")) {
                    Picasso.get().load(R.drawable.greet_10).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("greet_19")) {
                    Picasso.get().load(R.drawable.greet_9).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_1")) {
                    Picasso.get().load(R.drawable.sticker_24).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_2")) {
                    Picasso.get().load(R.drawable.sticker_22).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_3")) {
                    Picasso.get().load(R.drawable.sticker_21).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_4")) {
                    Picasso.get().load(R.drawable.sticker_25).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_5")) {
                    Picasso.get().load(R.drawable.sticker_18).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_6")) {
                    Picasso.get().load(R.drawable.sticker_20).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_7")) {
                    Picasso.get().load(R.drawable.sticker_19).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_8")) {
                    Picasso.get().load(R.drawable.cheems_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_9")) {
                    Picasso.get().load(R.drawable.cheems).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_10")) {
                    Picasso.get().load(R.drawable.cheems_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_11")) {
                    Picasso.get().load(R.drawable.cheems_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_12")) {
                    Picasso.get().load(R.drawable.cheems_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_13")) {
                    Picasso.get().load(R.drawable.cheems_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_14")) {
                    Picasso.get().load(R.drawable.sticker_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_15")) {
                    Picasso.get().load(R.drawable.sticker_10).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_16")) {
                    Picasso.get().load(R.drawable.memes_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_17")) {
                    Picasso.get().load(R.drawable.memes_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_18")) {
                    Picasso.get().load(R.drawable.sticker_15).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_19")) {
                    Picasso.get().load(R.drawable.sticker_16).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_20")) {
                    Picasso.get().load(R.drawable.sticker_13).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_21")) {
                    Picasso.get().load(R.drawable.sticker_17).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_22")) {
                    Picasso.get().load(R.drawable.sticker_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_23")) {
                    Picasso.get().load(R.drawable.sticker_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_24")) {
                    Picasso.get().load(R.drawable.sticker_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_25")) {
                    Picasso.get().load(R.drawable.sticker_8).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_26")) {
                    Picasso.get().load(R.drawable.sticker_12).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_27")) {
                    Picasso.get().load(R.drawable.sticker_30).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_28")) {
                    Picasso.get().load(R.drawable.sticker_28).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("meme_29")) {
                    Picasso.get().load(R.drawable.sticker_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_1")) {
                    Picasso.get().load(R.drawable.cat_1).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_2")) {
                    Picasso.get().load(R.drawable.cat_2).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_3")) {
                    Picasso.get().load(R.drawable.cat_3).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_4")) {
                    Picasso.get().load(R.drawable.cat_4).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_5")) {
                    Picasso.get().load(R.drawable.cat_5).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_6")) {
                    Picasso.get().load(R.drawable.cat_6).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_7")) {
                    Picasso.get().load(R.drawable.cat_7).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_8")) {
                    Picasso.get().load(R.drawable.cat_8).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_9")) {
                    Picasso.get().load(R.drawable.cat_9).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_10")) {
                    Picasso.get().load(R.drawable.cat_10).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_11")) {
                    Picasso.get().load(R.drawable.cat_11).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_12")) {
                    Picasso.get().load(R.drawable.cat_12).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_13")) {
                    Picasso.get().load(R.drawable.cat_13).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_14")) {
                    Picasso.get().load(R.drawable.cat_14).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_15")) {
                    Picasso.get().load(R.drawable.cat_15).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_16")) {
                    Picasso.get().load(R.drawable.cat_16).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_17")) {
                    Picasso.get().load(R.drawable.cat_17).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_18")) {
                    Picasso.get().load(R.drawable.cat_18).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_19")) {
                    Picasso.get().load(R.drawable.cat_19).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_20")) {
                    Picasso.get().load(R.drawable.cat_20).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_21")) {
                    Picasso.get().load(R.drawable.cat_21).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_22")) {
                    Picasso.get().load(R.drawable.cat_22).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_23")) {
                    Picasso.get().load(R.drawable.cat_23).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_24")) {
                    Picasso.get().load(R.drawable.cat_24).into(((SenderViewHolder) holder).sticker);
                }
                if (messageModel.getStickers().equals("cat_25")) {
                    Picasso.get().load(R.drawable.cat_25).into(((SenderViewHolder) holder).sticker);
                }

            } else if (messageModel.getMsg().equals("*Contact*")) {
                Picasso.get().load(messageModel.getContstatus()).into(((SenderViewHolder) holder).icon);
                ((SenderViewHolder) holder).displayname.setText(messageModel.getDisplayname());
                ((SenderViewHolder) holder).phonenumber.setText(messageModel.getPhonenumber());
                ((SenderViewHolder) holder).constraint.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
            } else if (messageModel.getMsg().equals("*GIF*")) {
                ((SenderViewHolder) holder).gif.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).gif.loadUrl(messageModel.getWebUrl());
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
            } else {
                ((SenderViewHolder) holder).photo.setVisibility(View.GONE);
                ((SenderViewHolder) holder).constraint.setVisibility(View.GONE);
                ((SenderViewHolder) holder).sticker.setVisibility(View.GONE);
                ((SenderViewHolder) holder).audio.setVisibility(View.GONE);
                ((SenderViewHolder) holder).thumbanail.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).play.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderlay.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).card_thumb.setVisibility(View.GONE);
                ((SenderViewHolder) holder).card_pic.setVisibility(View.GONE);

            }
            ((SenderViewHolder) holder).sendertime.setText(messageModel.getTime());
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMsg());

        } else {
            if (messageModel.getMsg().equals("*Photo*")) {
                Glide.with(context).load(messageModel.getImageUrl()).into(((RecieverViewHolder) holder).photos);
                ((RecieverViewHolder) holder).photos.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).play2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).thumbnail2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_picR.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).card_thumbR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recsticker.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).photos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PhotoView.class);
                        intent.putExtra("image", messageModel.getImageUrl());
                        context.startActivity(intent);
                    }
                });
            } else if (messageModel.getMsg().equals("*Sticker*")) {
                ((RecieverViewHolder) holder).recsticker.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).photos.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).play2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_picR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_thumbR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).thumbnail2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).reclayout.setVisibility(View.GONE);
                if (messageModel.getStickers().equals("sticker_1")) {
                    Picasso.get().load(R.drawable.new_19).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_2")) {
                    Picasso.get().load(R.drawable.new_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_3")) {
                    Picasso.get().load(R.drawable.new_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_4")) {
                    Picasso.get().load(R.drawable.new_18).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_5")) {
                    Picasso.get().load(R.drawable.new_15).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_6")) {
                    Picasso.get().load(R.drawable.new_11).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_7")) {
                    Picasso.get().load(R.drawable.new_13).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_8")) {
                    Picasso.get().load(R.drawable.new_14).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_9")) {
                    Picasso.get().load(R.drawable.new_16).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_10")) {
                    Picasso.get().load(R.drawable.new_10).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_11")) {
                    Picasso.get().load(R.drawable.new_28).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_12")) {
                    Picasso.get().load(R.drawable.new_12).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_13")) {
                    Picasso.get().load(R.drawable.new_21).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_14")) {
                    Picasso.get().load(R.drawable.new_22).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_15")) {
                    Picasso.get().load(R.drawable.new_20).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_16")) {
                    Picasso.get().load(R.drawable.new_27).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_17")) {
                    Picasso.get().load(R.drawable.stick_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_18")) {
                    Picasso.get().load(R.drawable.new_8).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_19")) {
                    Picasso.get().load(R.drawable.new_9).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_20")) {
                    Picasso.get().load(R.drawable.emoji_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_21")) {
                    Picasso.get().load(R.drawable.emoji_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_22")) {
                    Picasso.get().load(R.drawable.emoji_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_23")) {
                    Picasso.get().load(R.drawable.emoji_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_24")) {
                    Picasso.get().load(R.drawable.emoji_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("sticker_25")) {
                    Picasso.get().load(R.drawable.emoji_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_1")) {
                    Picasso.get().load(R.drawable.hand_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_2")) {
                    Picasso.get().load(R.drawable.hand_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_3")) {
                    Picasso.get().load(R.drawable.hand_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_4")) {
                    Picasso.get().load(R.drawable.hand_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_5")) {
                    Picasso.get().load(R.drawable.hand_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_6")) {
                    Picasso.get().load(R.drawable.hand_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_7")) {
                    Picasso.get().load(R.drawable.hand_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_8")) {
                    Picasso.get().load(R.drawable.hand_8).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_9")) {
                    Picasso.get().load(R.drawable.hand_9).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_10")) {
                    Picasso.get().load(R.drawable.hand_10).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_11")) {
                    Picasso.get().load(R.drawable.hand_11).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_12")) {
                    Picasso.get().load(R.drawable.hand_12).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_14")) {
                    Picasso.get().load(R.drawable.hand_14).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_15")) {
                    Picasso.get().load(R.drawable.hand_15).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_16")) {
                    Picasso.get().load(R.drawable.hand_16).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_17")) {
                    Picasso.get().load(R.drawable.hand_17).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("hand_18")) {
                    Picasso.get().load(R.drawable.hand_18).into(((RecieverViewHolder) holder).recsticker);
                }

                if (messageModel.getStickers().equals("greet_1")) {
                    Picasso.get().load(R.drawable.nee_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_2")) {
                    Picasso.get().load(R.drawable.new_30).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_3")) {
                    Picasso.get().load(R.drawable.new_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_4")) {
                    Picasso.get().load(R.drawable.new_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_5")) {
                    Picasso.get().load(R.drawable.new_25).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_6")) {
                    Picasso.get().load(R.drawable.new_26).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_7")) {
                    Picasso.get().load(R.drawable.new_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_8")) {
                    Picasso.get().load(R.drawable.new_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_9")) {
                    Picasso.get().load(R.drawable.greet_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_10")) {
                    Picasso.get().load(R.drawable.greet_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_11")) {
                    Picasso.get().load(R.drawable.greet_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_12")) {
                    Picasso.get().load(R.drawable.greet_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_13")) {
                    Picasso.get().load(R.drawable.greet_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_14")) {
                    Picasso.get().load(R.drawable.greet_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_15")) {
                    Picasso.get().load(R.drawable.greet_8).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_16")) {
                    Picasso.get().load(R.drawable.greet_11).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_17")) {
                    Picasso.get().load(R.drawable.greet_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_18")) {
                    Picasso.get().load(R.drawable.greet_10).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("greet_19")) {
                    Picasso.get().load(R.drawable.greet_9).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_1")) {
                    Picasso.get().load(R.drawable.sticker_24).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_2")) {
                    Picasso.get().load(R.drawable.sticker_22).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_3")) {
                    Picasso.get().load(R.drawable.sticker_21).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_4")) {
                    Picasso.get().load(R.drawable.sticker_25).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_5")) {
                    Picasso.get().load(R.drawable.sticker_18).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_6")) {
                    Picasso.get().load(R.drawable.sticker_20).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_7")) {
                    Picasso.get().load(R.drawable.sticker_19).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_8")) {
                    Picasso.get().load(R.drawable.cheems_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_9")) {
                    Picasso.get().load(R.drawable.cheems).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_10")) {
                    Picasso.get().load(R.drawable.cheems_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_11")) {
                    Picasso.get().load(R.drawable.cheems_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_12")) {
                    Picasso.get().load(R.drawable.cheems_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_13")) {
                    Picasso.get().load(R.drawable.cheems_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_14")) {
                    Picasso.get().load(R.drawable.sticker_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_15")) {
                    Picasso.get().load(R.drawable.sticker_10).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_16")) {
                    Picasso.get().load(R.drawable.memes_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_17")) {
                    Picasso.get().load(R.drawable.memes_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_18")) {
                    Picasso.get().load(R.drawable.sticker_15).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_19")) {
                    Picasso.get().load(R.drawable.sticker_16).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_20")) {
                    Picasso.get().load(R.drawable.sticker_13).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_21")) {
                    Picasso.get().load(R.drawable.sticker_17).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_22")) {
                    Picasso.get().load(R.drawable.sticker_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_23")) {
                    Picasso.get().load(R.drawable.sticker_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_24")) {
                    Picasso.get().load(R.drawable.sticker_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_25")) {
                    Picasso.get().load(R.drawable.sticker_8).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_26")) {
                    Picasso.get().load(R.drawable.sticker_12).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_27")) {
                    Picasso.get().load(R.drawable.sticker_30).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_28")) {
                    Picasso.get().load(R.drawable.sticker_28).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("meme_29")) {
                    Picasso.get().load(R.drawable.sticker_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_1")) {
                    Picasso.get().load(R.drawable.cat_1).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_2")) {
                    Picasso.get().load(R.drawable.cat_2).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_3")) {
                    Picasso.get().load(R.drawable.cat_3).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_4")) {
                    Picasso.get().load(R.drawable.cat_4).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_5")) {
                    Picasso.get().load(R.drawable.cat_5).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_6")) {
                    Picasso.get().load(R.drawable.cat_6).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_7")) {
                    Picasso.get().load(R.drawable.cat_7).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_8")) {
                    Picasso.get().load(R.drawable.cat_8).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_9")) {
                    Picasso.get().load(R.drawable.cat_9).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_10")) {
                    Picasso.get().load(R.drawable.cat_10).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_11")) {
                    Picasso.get().load(R.drawable.cat_11).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_12")) {
                    Picasso.get().load(R.drawable.cat_12).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_13")) {
                    Picasso.get().load(R.drawable.cat_13).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_14")) {
                    Picasso.get().load(R.drawable.cat_14).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_15")) {
                    Picasso.get().load(R.drawable.cat_15).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_16")) {
                    Picasso.get().load(R.drawable.cat_16).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_17")) {
                    Picasso.get().load(R.drawable.cat_17).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_18")) {
                    Picasso.get().load(R.drawable.cat_18).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_19")) {
                    Picasso.get().load(R.drawable.cat_19).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_20")) {
                    Picasso.get().load(R.drawable.cat_20).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_21")) {
                    Picasso.get().load(R.drawable.cat_21).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_22")) {
                    Picasso.get().load(R.drawable.cat_22).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_23")) {
                    Picasso.get().load(R.drawable.cat_23).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_24")) {
                    Picasso.get().load(R.drawable.cat_24).into(((RecieverViewHolder) holder).recsticker);
                }
                if (messageModel.getStickers().equals("cat_25")) {
                    Picasso.get().load(R.drawable.cat_25).into(((RecieverViewHolder) holder).recsticker);
                }

            } else if (messageModel.getMsg().equals("*Video*")) {
                ((RecieverViewHolder) holder).play2.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).photos.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recsticker.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_picR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_thumbR.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).thumbnail2.setVisibility(View.VISIBLE);
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(messageModel.getVideoUrl());
                Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(500000); //unit in microsecond
                ((RecieverViewHolder) holder).thumbnail2.setImageBitmap(bmFrame);

                ((RecieverViewHolder) holder).play2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent vid = new Intent(context, Videos.class);
                        vid.putExtra("video", messageModel.getVideoUrl());
                        context.startActivity(vid);
                    }
                });
            } else if (messageModel.getMsg().equals("*Location*")) {
                ((RecieverViewHolder) holder).play2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).photos.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recsticker.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).thumbnail2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_picR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_thumbR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).loc.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).loc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "geo:0,0?q=india";
                        Uri gmIntentUri = Uri.parse(uri);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (Manifest.permission.INTERNET.equals(null)) {
                            Toast.makeText(context, "Permissions Denied", Toast.LENGTH_SHORT).show();
                        } else {
                            context.startActivity(mapIntent);
                        }

                    }
                });

            } else {
                ((RecieverViewHolder) holder).photos.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recsticker.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).recieverMsg.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).reclayout.setVisibility(View.VISIBLE);
                ((RecieverViewHolder) holder).play2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).thumbnail2.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_picR.setVisibility(View.GONE);
                ((RecieverViewHolder) holder).card_thumbR.setVisibility(View.GONE);
            }
            ((RecieverViewHolder) holder).recievertime.setText(messageModel.getTime());
            ((RecieverViewHolder) holder).recieverMsg.setText(messageModel.getMsg());
        }
    }

    private void deletemessage(int position) {
        String time = messageModels.get(position).getTime();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");
        Query query = ref.child(messageModels.get(position).getUid()).orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    snapshot1.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg, recievertime;
        ImageView photos, recsticker, play2, thumbnail2;
        View reclayout;
        Button loc;
        CardView card_picR, card_thumbR;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.RecieverText);
            recievertime = itemView.findViewById(R.id.Reciever_Time);
            photos = itemView.findViewById(R.id.photos);
            reclayout = itemView.findViewById(R.id.reclayout);
            recsticker = itemView.findViewById(R.id.stkrec);
            play2 = itemView.findViewById(R.id.play2);
            thumbnail2 = itemView.findViewById(R.id.thumbnail2);
            card_picR = itemView.findViewById(R.id.card_picR);
            card_thumbR = itemView.findViewById(R.id.card_thumbR);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, sendertime, displayname, phonenumber;
        ImageView photo, icon, sticker, play, thumbanail, sel;
        Button stop;
        View constraint, senderlay, audio, location;
        WebView gif;
        CardView card_pic, card_thumb;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.Sender_Text);
            sendertime = itemView.findViewById(R.id.Sender_Time);
            photo = itemView.findViewById(R.id.photo);
            displayname = itemView.findViewById(R.id.displayname);
            phonenumber = itemView.findViewById(R.id.phonenum);
            constraint = itemView.findViewById(R.id.contact);
            icon = itemView.findViewById(R.id.contpic);
            gif = itemView.findViewById(R.id.gif);
            sticker = itemView.findViewById(R.id.stckr);
            play = itemView.findViewById(R.id.play);
            senderlay = itemView.findViewById(R.id.senderlayout);
            thumbanail = itemView.findViewById(R.id.thumbnail);
            audio = itemView.findViewById(R.id.audio);
            stop = itemView.findViewById(R.id.stop);
            sel = itemView.findViewById(R.id.sel);
            card_pic = itemView.findViewById(R.id.card_pic);
            card_thumb = itemView.findViewById(R.id.card_thumb);
        }
    }
}
