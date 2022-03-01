package com.example.mainactivity.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.R;
import com.example.mainactivity.models.Users;

import java.util.ArrayList;
import java.util.List;

public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;

    public NamesAdapter(List<Users> list, Context context) {
        this.list = (ArrayList<Users>) list;
        this.context = context;
    }

    @NonNull
    @Override
    public NamesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.partiname, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NamesAdapter.ViewHolder holder, int position) {
        Users users = list.get(position);
        String name = users.getUsername();
        holder.roomuser.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomuser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomuser = itemView.findViewById(R.id.partinames);
        }
    }
}
