package com.example.mainactivity.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mainactivity.Fragments.CallFrag;
import com.example.mainactivity.Fragments.CameraFrag;
import com.example.mainactivity.Fragments.ChatsFrag;
import com.example.mainactivity.Fragments.statusFrag;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
            switch(position){
                case 0:return new ChatsFrag();
                case 1:return new statusFrag();
                case 2:return new CallFrag();
                case 3:return new CameraFrag();
            }
            return new ChatsFrag();
        }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title= null;
        if(position==0){

            title="INBOX";
        }
        if(position==1){

            title="CHAT ROOM";
        }
        if(position==2){

            title="PROFILE";
        }
        return title;
    }
}
