package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.data.Stories;
import com.example.instagram.data.User;

import java.util.ArrayList;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesHolder> {


    private LayoutInflater inflater;
    private ArrayList<User> users;
    private ArrayList<Stories> stories;

    public StoriesAdapter(Context context, ArrayList<Stories> stories, ArrayList<User> users) {
        inflater = LayoutInflater.from(context);
        this.stories = stories;
        this.users = users;
    }

    @Override
    public StoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.story_item, parent, false);
        return new StoriesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesHolder holder, int position) {
        if (position == 0) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class StoriesHolder extends RecyclerView.ViewHolder {

        public StoriesHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void Bind() {
            
        }
    }
}
