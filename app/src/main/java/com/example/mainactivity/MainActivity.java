package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mainactivity.Adapters.FragmentsAdapter;
import com.example.mainactivity.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#43DC5C"));
        ColorDrawable colorDrawable2 = new ColorDrawable(Color.parseColor("#00BCD4"));
        binding.searchbtn2.setVisibility(View.GONE);
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setElevation(0);
        setTitle("VibeX");
        binding.searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
        binding.searchbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GroupSearchActivity.class));
            }
        });
        binding.viewpager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewpager);
        binding.tablayout.getTabAt(3).setIcon(R.drawable.search);
        binding.tablayout.setTabIconTint(ColorStateList.valueOf(Color.WHITE));
        binding.searchbtn.setVisibility(View.GONE);
        binding.searchbtn2.setVisibility(View.GONE);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.searchbtn.setVisibility(View.VISIBLE);
                    binding.searchbtn2.setVisibility(View.GONE);
                    actionBar.setBackgroundDrawable(colorDrawable);
                    binding.tablayout.setBackgroundColor(colorDrawable.getColor());
                    binding.tablayout.getTabAt(1).setIcon(null);
                    binding.tablayout.getTabAt(1).setText("CHAT ROOM");
                    binding.tablayout.getTabAt(2).setIcon(null);
                    binding.tablayout.getTabAt(2).setText("PROFILE");
                    binding.tablayout.setVisibility(View.VISIBLE);
                    actionBar.show();
                } else if (position == 1) {
                    binding.searchbtn2.setVisibility(View.VISIBLE);
                    binding.searchbtn.setVisibility(View.GONE);
                    actionBar.setBackgroundDrawable(colorDrawable2);
                    binding.searchbtn.setBackgroundColor(colorDrawable2.getColor());
                    binding.tablayout.setBackgroundColor(colorDrawable2.getColor());
                    binding.tablayout.getTabAt(2).setIcon(null);
                    binding.tablayout.getTabAt(2).setText("PROFILE");
                    binding.tablayout.getTabAt(1).setIcon(R.drawable.group);
                    binding.tablayout.getTabAt(1).setText("");
                    binding.tablayout.setVisibility(View.VISIBLE);
                    actionBar.show();
                } else if (position == 2) {
                    actionBar.setBackgroundDrawable(colorDrawable);
                    binding.tablayout.setBackgroundColor(colorDrawable.getColor());
                    binding.searchbtn.setVisibility(View.GONE);
                    binding.searchbtn2.setVisibility(View.GONE);
                    binding.tablayout.getTabAt(2).setIcon(R.drawable.profile);
                    binding.tablayout.getTabAt(2).setText("");
                    binding.tablayout.getTabAt(1).setIcon(null);
                    binding.tablayout.getTabAt(1).setText("CHAT ROOM");
                    binding.tablayout.setVisibility(View.VISIBLE);
                    actionBar.show();
                }
                else if(position==3){
                    binding.searchbtn.setVisibility(View.GONE);
                    binding.searchbtn2.setVisibility(View.GONE);
                    binding.tablayout.getTabAt(2).setIcon(null);
                    binding.tablayout.getTabAt(2).setText("PROFILE");
                    binding.tablayout.getTabAt(1).setIcon(null);
                    binding.tablayout.getTabAt(1).setText("CHAT ROOM");
                    binding.tablayout.setVisibility(View.GONE);
                    actionBar.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                auth.signOut();
                Intent i = new Intent(MainActivity.this, loginActivity.class);
                startActivity(i);
                break;

            case R.id.grpcht:
                startActivity(new Intent(MainActivity.this, meetHomeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}