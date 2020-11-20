package com.example.instagram.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.instagram.DateUtil;
import com.example.instagram.R;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.presenters.StoryPresenter;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoryPresenter.Connection, StoriesProgressView.StoriesListener {

    private static final String STORY_EXTRA = "STORY";
    private static final String POSITION_EXTRA = "POSITION";
    private static final String MY_STORY_EXTRA = "MY_STORY";
    private static final Long STORY_DURATION = 3000L;

    private ArrayList<Story> users;
    private ArrayList<Story> story;


    private StoriesProgressView progress;
    private ImageView story_photo, profile_photo;
    private int story_position = 0;
    private int user_position;
    private Button view_counter;

    private TextView username, date;

    private StoryPresenter presenter;

    public static Intent newInstance(Context context, ArrayList<Story> users, int position, boolean myStory) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putParcelableArrayListExtra(STORY_EXTRA, users);
        intent.putExtra(POSITION_EXTRA, position);
        intent.putExtra(MY_STORY_EXTRA, myStory);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        users = getIntent().getParcelableArrayListExtra(STORY_EXTRA);
        user_position = getIntent().getIntExtra(POSITION_EXTRA, 0);
        presenter = new StoryPresenter(this);
        init();
        if (getIntent().getBooleanExtra(MY_STORY_EXTRA, false)) {
            user_position = -1;
            loadUserStory(presenter.getUserId());
        } else {
            loadUserStory(users.get(user_position).getUser_id());
        }

    }

    private void init() {
        view_counter = findViewById(R.id.view_counter);
        story_photo = findViewById(R.id.story_photo);
        //story.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
        progress = findViewById(R.id.stories_progress);
        username = findViewById(R.id.username_label);
        profile_photo = findViewById(R.id.profile_pic);
        date = findViewById(R.id.date_label);
        /*progress.setStoriesCount(3);
        progress.setStoryDuration(1500L);*/


    }

    private void loadUserStory(String user_id) { ;
        presenter.loadUserStory(user_id, new StoryPresenter.onUserStoryLoaded() {
            @Override
            public void onSuccess(ArrayList<Story> s, User u) {
                if (presenter.getUserId().equals(u.getUser_id())) {
                    view_counter.setVisibility(View.VISIBLE);
                } else {
                    view_counter.setVisibility(View.GONE);
                }
                username.setText(u.getUsername());
                Glide.with(StoryActivity.this)
                    .load(u.getImage())
                    .apply(new RequestOptions()
                    .placeholder(R.color.colorGrey)
                    .error(R.drawable.default_avatar))
                    .into(profile_photo);
                story = s;
                story_position = 0;
                progress.setStoriesCount(s.size());
                progress.setStoryDuration(STORY_DURATION);
                progress.setStoriesListener(StoryActivity.this);
                showStory(story.get(story_position));
                progress.startStories();
            }
        });
    }

    private void showStory(final Story s) {
        view_counter.setText(getResources().getString(R.string.views_text) + s.getViews_coutnter());
        date.setText(DateUtil.hoursAgo(s.getTime()) + " h.");
        Glide.with(StoryActivity.this)
                .load(s.getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (!presenter.getUserId().equals(s.getUser_id())) {
                            if (!s.isViewed()) {
                                presenter.setStoryView(s.getUser_id(), s.getStory_id(), presenter.getUserId());
                            }
                        }
                        return false;
                    }
                })
                .into(story_photo);
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext() {
        if (story_position < (story.size() - 1)) {
            story_position++;
            showStory(story.get(story_position));
        }
    }

    @Override
    public void onPrev() {
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        if (user_position < (users.size() - 1)) {
            user_position++;
            loadUserStory(users.get(user_position).getUser_id());
        } else {
            finish();
        }

    }
}

