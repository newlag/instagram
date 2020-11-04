package com.example.instagram.fragments;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.activities.FollowersActivity;
import com.example.instagram.presenters.ProfilePresenter;
import com.example.instagram.R;
import com.example.instagram.activities.SettingsActivity;
import com.example.instagram.data.Post;
import com.example.instagram.data.User;

import java.util.ArrayList;


public class ProfileFragment extends BaseFragment {

    private static final int SETTINGS_REQUEST_CODE = 0;
    private static final int FOLLOWERS_REQUEST_CODE = 1;

    private static final String USER_ID_EXTRA = "USER_ID";
    private String user_id;
    private boolean isFollow;
    private  ArrayList<Post> posts_list = new ArrayList<>();

    private User user;
    private RecyclerView photosView;
    private ProfilePresenter presenter;
    private SwipeRefreshLayout refresh;
    private Toolbar toolBar;

    private ConstraintLayout profile;
    private TextView label, fullname, bio;
    private Button action_button, posts_button, followers_button, followings_button;
    private ImageView avatar;

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_EXTRA, user_id);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle args = getArguments();
        if (!args.isEmpty()) {
            user_id = args.getString(USER_ID_EXTRA);
        }

        init(v); // Инициализация элементов интерфейса

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action_button.getText().equals(getString(R.string.edit_profile_text))) {
                    startActivityForResult(SettingsActivity.newInstance(getActivity(), user_id), SETTINGS_REQUEST_CODE);
                } else {
                    if (isFollow) {
                        presenter.unFollow(user_id, new ProfilePresenter.onUserUnFollow() { // Отписка
                            @Override
                            public void onSuccess() {
                                isFollow = false;
                                updateFollow();
                            }

                            @Override
                            public void onFailure() {
                                showError();
                            }
                        });
                    } else {
                        presenter.follow(user_id, new ProfilePresenter.onUserFollow() { // Подписка
                            @Override
                            public void onSuccess() {
                                isFollow = true;
                                updateFollow();
                            }

                            @Override
                            public void onFailure() {
                                showError();
                            }
                        });
                    }
                }
            }
        });

        followers_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(FollowersActivity.newInstance(getActivity(), user_id, 0), FOLLOWERS_REQUEST_CODE);
            }
        });

        followings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(FollowersActivity.newInstance(getActivity(), user_id, 1), FOLLOWERS_REQUEST_CODE);
            }
        });


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profile.setVisibility(View.GONE);
                updateProfile();

            }
        });


        presenter = new ProfilePresenter(user_id);
        updateProfile();
        setHasOptionsMenu(true);
        return v;
    }

    private void updateProfile() {
        refresh.setRefreshing(true);
        presenter.getProfile(new ProfilePresenter.onProfileLoaded() {
            @Override
            public void onSuccess(User result) {
                user = result;
                Glide.with(getActivity())
                    .load(user.getImage())
                    .apply(new RequestOptions()
                    .placeholder(R.color.colorGrey)
                    .error(R.drawable.default_avatar))
                    .into(avatar);
                toolBar.setTitle(user.getUsername());

                String fn = user.getFullname();
                if (fn == null || fn.isEmpty()) {
                    fullname.setVisibility(View.GONE);
                } else {
                    fullname.setText(fn);
                }



                String b = user.getBio();
                if (b == null) {
                    bio.setVisibility(View.GONE);
                } else {
                    bio.setText(b);
                }
                loadPosts();
            }

            @Override
            public void onFailure() {
                showError();
            }
        });
    }



    private void loadPosts() {
        presenter.getPosts(new ProfilePresenter.onPostsLoaded() { // Загрузка постов
            @Override
            public void onSuccess(ArrayList<Post> posts) {
                posts_list = posts;
                posts_button.setText(Html.fromHtml("<b>" + posts_list.size() + "</b><br>" + getString(R.string.post_button)));
                loadFollowers();
            }

            @Override
            public void onFailure() {
                showError();
            }
        });
    }

    private void loadFollowers() {
        presenter.getFollowers(new ProfilePresenter.onFollowersLoaded() {
            @Override
            public void onSuccess(long followers) {
                followers_button.setText(Html.fromHtml("<b>" +String.valueOf(followers) + "</b><br>" + getString(R.string.follow_text)));
                loadFollowings();
            }

            @Override
            public void onFailure() {
                showError();
            }
        });
    }

    private void loadFollowings() {
        presenter.getFollowings(new ProfilePresenter.onFollowingsLoaded() {
            @Override
            public void onSuccess(long followings) {
                followings_button.setText(Html.fromHtml("<b>" +String.valueOf(followings) + "</b><br>" + getString(R.string.following_text)));

                if (user_id.equals(presenter.getUserId())) {
                    showProfile();
                    action_button.setText(R.string.edit_profile_text);
                } else {
                    toolBar.setNavigationIcon(R.drawable.ic_back);
                    toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().onBackPressed();
                        }
                    });
                    isFollow();
                }
            }

            @Override
            public void onFailure() {
                showError();
            }
        });
    }

    private void isFollow() {
        presenter.isFollow(new ProfilePresenter.isUserFollow() {
            @Override
            public void onSuccess(boolean follow) {
                isFollow = follow;
                updateFollow();
                showProfile();
            }

            @Override
            public void onFailure() {
                showError();
            }
        });
    }

    private void updateFollow() {
        if (isFollow) {
            action_button.setText(R.string.following_text);
            action_button.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            action_button.setTextColor(getResources().getColor(R.color.colorBlack));
        } else {
            action_button.setText(R.string.follow_text);
            action_button.setBackgroundColor(getResources().getColor(R.color.colorBlueButton));
            action_button.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void showProfile() {
        refresh.setRefreshing(false);
        profile.setVisibility(View.VISIBLE);
        if (refresh.isRefreshing()) {
            refresh.setRefreshing(false);
            photosView.getAdapter().notifyDataSetChanged();
        }

    }

    private void init(View v) {
        profile = v.findViewById(R.id.profile_view);
        profile.setVisibility(View.GONE);
        avatar = v.findViewById(R.id.profile_avatar);
        fullname = v.findViewById(R.id.fullname_textview);
        bio = v.findViewById(R.id.bio_textview);
        action_button = v.findViewById(R.id.action_button);
        posts_button = v.findViewById(R.id.post_counter);
        followers_button = v.findViewById(R.id.followers_counter);
        followings_button = v.findViewById(R.id.followings_counter);
        photosView = (RecyclerView) v.findViewById(R.id.recycler_view);
        photosView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        photosView.setAdapter(new PhotoAdapter());
        photosView.setHasFixedSize(true);
        photosView.setItemViewCacheSize(20);
        photosView.setDrawingCacheEnabled(true);
        photosView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        toolBar = v.findViewById(R.id.toolbar);
        refresh = v.findViewById(R.id.refresh);
    }

    private void showError() {
        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SETTINGS_REQUEST_CODE:
                    profile.setVisibility(View.GONE);
                    refresh.setRefreshing(true);
                    updateProfile();
                break;
                case FOLLOWERS_REQUEST_CODE:
                    addFragment(ProfileFragment.newInstance(FollowersActivity.readUserId(data)));
                break;
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.photo_item, parent, false));
        }

        public void Bind(String image_url) {
            int size = photosView.getWidth() / 3;
            Glide.with(getActivity())
                .load(image_url)
                .apply(new RequestOptions()
                .override(size, size))
                .into((ImageView) itemView.findViewById(R.id.post_photo));
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFragment(PostListFragment.newInstance(user_id));
                }
            });
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PhotoHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            holder.Bind(posts_list.get(position).getUrl());
        }

        @Override
        public int getItemCount() {
            return posts_list.size();
        }
    }

}
