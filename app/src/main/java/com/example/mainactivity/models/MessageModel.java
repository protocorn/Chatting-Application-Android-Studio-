package com.example.mainactivity.models;

import android.graphics.Bitmap;

public class MessageModel {

    String Uid, msg, time, ImageUrl,VideoUrl,Displayname,phonenumber, contstatus, WebUrl, Stickers,audioUrl;

    public MessageModel(String uid, String msg, String time) {
        Uid = uid;
        this.msg = msg;
        this.time = time;
    }

    public MessageModel(String uid, String msg) {
        Uid = uid;
        this.msg = msg;
    }

    public MessageModel(){}


    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getStickers() {
        return Stickers;
    }

    public void setStickers(String stickers) {
        Stickers = stickers;
    }

    public String getWebUrl() {
        return WebUrl;
    }

    public void setWebUrl(String webUrl) {
        WebUrl = webUrl;
    }

    public String getContstatus() {
        return contstatus;
    }

    public void setContstatus(String contstatus) {
        this.contstatus = contstatus;
    }

    public String getDisplayname() {
        return Displayname;
    }

    public void setDisplayname(String displayname) {
        Displayname = displayname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

