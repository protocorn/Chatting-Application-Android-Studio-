package com.example.mainactivity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainactivity.GroupDetailActivity;
import com.example.mainactivity.R;
import com.example.mainactivity.models.GroupModel;
import com.example.mainactivity.models.Users;

import java.util.ArrayList;
import java.util.List;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.HolderGroupAdapter> {
    private  Context context;
    private ArrayList<GroupModel>GroupList;

    public GroupAdapter(Context context, ArrayList<GroupModel> GroupList) {
        this.context = context;
        this.GroupList = GroupList;
    }

    @NonNull
    @Override
    public HolderGroupAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.sample_group,parent,false);
        return new HolderGroupAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupAdapter holder, int position) {
    GroupModel model= GroupList.get(position);
    String groupId= model.getGroupId();
    String groupIcon= model.getGroupIcon();
    holder.grpTitle.setText(model.getGroupTitle());

    Glide.with(context).load(groupIcon).placeholder(R.drawable.finalgroup).into(holder.grpIcon);

    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(context, GroupDetailActivity.class);
            intent.putExtra("groupId",groupId);
            intent.putExtra("GroupIcon",groupIcon);
            context.startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return GroupList.size();
    }

   public static class HolderGroupAdapter extends RecyclerView.ViewHolder{
        ImageView grpIcon;
        TextView grpTitle, sender,lastmsg,time;
        public HolderGroupAdapter(@NonNull View itemView) {
            super(itemView);

            grpIcon= itemView.findViewById(R.id.grpIcon);
            grpTitle= itemView.findViewById(R.id.GroupTitle);
            sender= itemView.findViewById(R.id.sendername);
            lastmsg= itemView.findViewById(R.id.grplast);
            time= itemView.findViewById(R.id.grp_time);
        }
    }
}