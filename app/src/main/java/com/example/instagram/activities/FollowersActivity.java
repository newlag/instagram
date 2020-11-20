package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.R;
import com.example.instagram.data.User;
import com.example.instagram.presenters.FollowersPresenter;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity implements FollowersPresenter.onConnectionError {
    // wtf
    private static final String USER_EXTRA = "USER_ID";
    private static final String TYPE_EXTRA = "TYPE";
    private static final String USER_ID_EXTRA = "USER_ID_SHOW";

    private ProgressBar loading;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private String userId;
    private int type;

    private FollowersPresenter presenter;

    private ArrayList<User> usersList = new ArrayList<>();

    public static Intent newInstance(Context context, String userId, int type) { // type - 0 (followers), type - 1 (following)
        Intent intent = new Intent(context, FollowersActivity.class);
        intent.putExtra(USER_EXTRA, userId);
        intent.putExtra(TYPE_EXTRA, type);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Bundle args = getIntent().getExtras();
        if (!args.isEmpty()) {
            userId = args.getString(USER_EXTRA);
            type = args.getInt(TYPE_EXTRA);
        }
        init();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        presenter = new FollowersPresenter(userId, type, this);
        updateList();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        loading = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.items_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FollowersAdapter());
        switch (type) {
            case 0:
                toolbar.setTitle(R.string.followers_label);
            break;
            case 1:
                toolbar.setTitle(R.string.following_label);
            break;
        }
    }

    private void updateList() {
        loading.setVisibility(View.VISIBLE);
        presenter.loadFollowers(new FollowersPresenter.onFollowersLoaded() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                Log.i("[Follwoers]", "лолкекчебурек : " + users.size());
                loading.setVisibility(View.GONE);
                usersList = users;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }


    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    private class FollowersHolder extends RecyclerView.ViewHolder {

        TextView username, fullname;
        ImageView avatar;
        Button delete;

        public FollowersHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.user_list_item, parent, false));
            username = itemView.findViewById(R.id.content);
            fullname = itemView.findViewById(R.id.fullname_label);
            avatar = itemView.findViewById(R.id.user_photo);
            delete = itemView.findViewById(R.id.delete_button);
        }

        public void Bind(final User user) {
            if (type == 0 && presenter.isMyProfile()) delete.setVisibility(View.VISIBLE);
            username.setText(user.getUsername());
            if (user.getFullname().length() > 0) {
                fullname.setVisibility(View.VISIBLE);
                fullname.setText(user.getFullname());
            } else {
                fullname.setVisibility(View.GONE);
            }
            Glide.with(FollowersActivity.this)
                .load(user.getImage())
                .apply(new RequestOptions()
                .placeholder(R.color.colorGrey)
                .error(R.drawable.default_avatar))
                .into(avatar);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.deleteFollower(user.getUser_id());
                    usersList.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(USER_ID_EXTRA, user.getUser_id());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private class FollowersAdapter extends RecyclerView.Adapter<FollowersHolder> {

        @NonNull
        @Override
        public FollowersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FollowersHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FollowersHolder holder, int position) {
            holder.Bind(usersList.get(position));
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }
    }

    public static String readUserId(Intent intent) {
        return intent.getExtras().getString(USER_ID_EXTRA);
    }

}
