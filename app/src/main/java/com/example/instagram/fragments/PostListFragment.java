package com.example.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.DateUtil;
import com.example.instagram.R;
import com.example.instagram.activities.CommentsActivity;
import com.example.instagram.activities.UploadActivity;
import com.example.instagram.adapters.PostAdapter;
import com.example.instagram.adapters.StoryAdapter;
import com.example.instagram.data.Post;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.presenters.PostListPresenter;

import java.util.ArrayList;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

public class PostListFragment extends Fragment implements PostListPresenter.connectionError, PostAdapter.Callback {

    private static final String USER_ID_EXTRA = "USER_ID";
    private static final int STORY_REQUEST = 1;

    private ArrayList<Post> posts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>(); // Подписки
    private ArrayList<Story> story;


    private PostListPresenter presenter;

    private RecyclerView postViewer;
    private Toolbar toolbar;
    private SwipeRefreshLayout refresh;

    private String userId;
    private boolean isProfile;

    public static PostListFragment newInstance(String userId) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID_EXTRA, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        init(v);

        Bundle args = getArguments();

        presenter = new PostListPresenter(this);

        if (args != null) { // Профиль
            userId = args.getString(USER_ID_EXTRA);
            isProfile = true;
            toolbar.setTitle(R.string.posts_label);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        } else { // Лента
            isProfile = false;
            toolbar.setTitle(R.string.app_name);
        }

        update();

        /*updateList();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadPosts(new PostListPresenter.onPostsLoaded() {
                    @Override
                    public void onSuccess(ArrayList<Post> p) {
                        updateList();
                    }
                });
            }
        });*/

        return v;
    }

    private void update() {
        if (!isProfile) {
            presenter.loadFollowings(new PostListPresenter.onFollowingsLoaded() {
                @Override
                public void onSuccess(ArrayList<User> u) {
                    users = u;
                    loadPosts();
                    //loadStory();
                }
            });
        }
    }

    private void loadPosts() {
        presenter.loadPosts(users, new PostListPresenter.onPostsLoaded() {
            @Override
            public void onSuccess(ArrayList<Post> p) {
                posts = p;
                loadStory();
            }
        });
    }

    private void loadStory() {
        presenter.loadStory(users, new PostListPresenter.onStoryLoaded() {
            @Override
            public void onSuccess(ArrayList<Story> s) {
                story = s;
                postViewer.setLayoutManager(new LinearLayoutManager(getActivity()));
                postViewer.setAdapter(new PostAdapter(getContext(), posts, story, presenter, PostListFragment.this));
            }
        });
    }

    private void updateList() {
        /*if (isProfile) {
            presenter.loadPosts(userId, new PostListPresenter.onPostsLoaded() {
                @Override
                public void onSuccess(ArrayList<Post> p) {
                posts = p;
                //postViewer.getAdapter().notifyDataSetChanged();
                postViewer.setLayoutManager(new LinearLayoutManager(getActivity()));
                postViewer.setAdapter(new PostAdapter(getContext(), posts, presenter, PostListFragment.this));
                }
            });
        } else {
            presenter.loadPosts(new PostListPresenter.onPostsLoaded() {
                @Override
                public void onSuccess(ArrayList<Post> p) {
                posts = p;
                //postViewer.getAdapter().notifyDataSetChanged();
                postViewer.setLayoutManager(new LinearLayoutManager(getActivity()));
                postViewer.setAdapter(new PostAdapter(getContext(), posts, presenter, PostListFragment.this));
                }
            });
        }*/
    }

    private void init(View v) {
        toolbar = v.findViewById(R.id.toolbar);
        postViewer = v.findViewById(R.id.items_list);
        //postViewer.setLayoutManager(new LinearLayoutManager(getActivity()));
        //postViewer.setAdapter(new PostAdapter(getContext(), posts, presenter, this));
        refresh = v.findViewById(R.id.refresh);
    }

    /*private class PostsHolder extends RecyclerView.ViewHolder {
        TextView name, likes, description, date;
        ImageView photo;
        ImageButton likeButton, commentButton;
        ImageView avatar;
        boolean isLiked;
        ArrayList<Post.Likes> likesList = new ArrayList<>();

        public PostsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.post_info, parent, false));
            name = itemView.findViewById(R.id.name_label);
            likes = itemView.findViewById(R.id.like_counter);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.post_date);
            photo = itemView.findViewById(R.id.post_image);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            avatar = itemView.findViewById(R.id.post_avatar);
            description.setVisibility(View.GONE);
        }

        public void Bind(final Post post) {
            itemView.setVisibility(View.GONE);
            this.date.setText(DateUtil.getDate(post.getTime()) + getString(R.string.days_ago));

            presenter.getUserName(post.getUser_id(), new PostListPresenter.onUserNameLoaded() {
                @Override
                public void onSuccess(User user) {
                    String nick = user.getUsername();
                    name.setText(nick);
                    if (post.getDescription().length() > 0) {
                        description.setText(Html.fromHtml("<b>" + nick + "</b> " + post.getDescription()));
                        description.setVisibility(View.VISIBLE);
                    }

                Glide.with(getActivity())
                    .load(user.getImage())
                    .apply(new RequestOptions()
                    .placeholder(R.color.colorGrey)
                    .error(R.color.colorGrey))
                    .into(avatar);

                    presenter.loadLikes(post.getPost_id(), new PostListPresenter.onLikesLoaded() {
                        @Override
                        public void onSuccess(ArrayList<Post.Likes> likes) {
                            likesList = likes;
                            updateLikesCounter();
                            isLiked = presenter.isPostLiked(likesList);
                            updateLikeButton();
                            itemView.setVisibility(View.VISIBLE);
                            if (refresh.isRefreshing()) refresh.setRefreshing(false);
                        }
                    });
                }
            });

            Glide.with(getActivity())
                .load(post.getUrl())
                .apply(new RequestOptions()
                .placeholder(R.color.colorGrey)
                .error(R.color.colorGrey))
                .into(photo);


            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.likePost(post, isLiked);
                    isLiked = !isLiked;
                    updateLikeButton();
                    presenter.loadLikes(post.getPost_id(), new PostListPresenter.onLikesLoaded() {
                        @Override
                        public void onSuccess(ArrayList<Post.Likes> likes) {
                            likesList = likes;
                            updateLikesCounter();
                        }
                    });
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(CommentsActivity.newInstance(getActivity(), post.getPost_id()));
                }
            });
        }

        private void updateLikesCounter() {
            likes.setText(likesList.size() + " likes");
        }

        private void updateLikeButton() {
            if (isLiked) {
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
            } else {
                likeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
            }
        }
    }

    private class PostsAdapter extends RecyclerView.Adapter<PostsHolder> {

        @NonNull
        @Override
        public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PostsHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsHolder holder, int position) {
            holder.Bind(posts.get(position));
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }*/

    @Override
    public void showConnectionError() {
        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostsLoaded() {
        if (refresh.isRefreshing()) refresh.setRefreshing(false);
    }

    @Override
    public void onComment(String post_id) {
        startActivity(CommentsActivity.newInstance(getActivity(), post_id));
    }

    @Override
    public void uploadStory() {
        //getActivity().startActivityFromFragment(this, UploadActivity.newInstance(getActivity(), 2), STORY_REQUEST);
        Log.i("[PostListFragment.java]", "локекчебурек | вызов uploadStory");
        startActivityForResult(UploadActivity.newInstance(getContext(), 2), STORY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == STORY_REQUEST) {
                String path = UploadActivity.readFileExtra(data);
                presenter.uploadPhoto(path);
                Log.i("[PostListFragment.java]", "лолкекчебурек");
            }
        }
    }


}
