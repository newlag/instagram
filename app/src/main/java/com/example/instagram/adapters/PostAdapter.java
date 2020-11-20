package com.example.instagram.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.DateUtil;
import com.example.instagram.R;
import com.example.instagram.data.Post;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.presenters.PostListPresenter;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> implements StoryAdapter.Callback {

    private static final String TAG = "[PostAdapter.java]: ";

    private ArrayList<Post> posts;
    private ArrayList<User> users;
    private ArrayList<Story> story;
    private Context context;
    private PostListPresenter presenter;
    private Callback loading;
    private boolean isStoryiesCreated = false;



    /*public PostAdapter(Context context, ArrayList<Post> posts, ArrayList<User> users, PostListPresenter presenter, Callback loading) {
        Log.i(TAG, "получен список длиной: " + posts.size());
        this.context = context;
        this.posts = posts;
        this.presenter = presenter;
        this.loading = loading;
        this.users = users;
    }*/

    public PostAdapter(Context context, ArrayList<Post> posts, ArrayList<Story> story, PostListPresenter presenter, Callback loading) {
        this.context = context;
        this.posts = posts;
        this.story = story;
        this.presenter = presenter;
        this.loading = loading;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return 0;
            default: return 1;
        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.i("[PostAdapter.java]: ", " onCreateViewHolder called.");

        View v;

        switch (viewType) {
            case 0: // Строка с историями
                v = LayoutInflater.from(context).inflate(R.layout.story_container, parent, false);
                final RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new StoryAdapter(story, context, presenter, this));

            break;
            default: // пост
                v = LayoutInflater.from(context).inflate(R.layout.post_info, parent, false);
            break;
        }

        return new PostHolder(v);

        /*if (!isStoryiesCreated) {
            isStoryiesCreated = true;
            return new PostHolder(LayoutInflater.from(context), parent, true);
        } else {
            Log.i(TAG, "onCreateViewHolder called");
            return new PostHolder(LayoutInflater.from(context), parent);
        }*/

    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Log.i(TAG, "Отрисовка позиции: " + position);
        if (position == 0) { // Для историй
            holder.bindStoryies();
        } else { // Для постов
            holder.Bind(posts.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return (posts.size() + 1);
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        private ImageButton likeButton, commentButton;
        private ImageView photo, avatar;
        private TextView publisher, likes, description, date;
        private ArrayList<Post.Likes> likesList = new ArrayList<>();
        private boolean isLiked;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            photo = itemView.findViewById(R.id.post_image);
            avatar = itemView.findViewById(R.id.post_avatar);
            publisher = itemView.findViewById(R.id.name_label);
            likes = itemView.findViewById(R.id.like_counter);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.post_date);
        }

        public PostHolder() {
            super(null);
        }

        /*public PostHolder(LayoutInflater inflater, ViewGroup parent, boolean isStoryies) {
            super(inflater.inflate(R.layout.story_item, parent, false));
        }

        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.post_info, parent, false));
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            photo = itemView.findViewById(R.id.post_image);
            avatar = itemView.findViewById(R.id.post_avatar);
            publisher = itemView.findViewById(R.id.name_label);
            likes = itemView.findViewById(R.id.like_counter);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.post_date);
        }*/

        public void Bind(final Post post) {
            Log.i(TAG, "received" + post.getPost_id());
            itemView.setVisibility(View.GONE);
            date.setText(DateUtil.getDate(post.getTime()) + " d");

            presenter.getUserName(post.getUser_id(), new PostListPresenter.onUserNameLoaded() {
                @Override
                public void onSuccess(User user) {
                    String nick = user.getUsername();
                    publisher.setText(nick);
                    if (post.getDescription().length() > 0) {
                        description.setText(Html.fromHtml("<b>" + nick + "</b> " + post.getDescription()));
                        description.setVisibility(View.VISIBLE);
                    }

                    Glide.with(context) // Загрузка юзерпика
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
                            loading.onPostsLoaded();
                            //if (refresh.isRefreshing()) refresh.setRefreshing(false);
                        }
                    });

                    Glide.with(context) // Загрузка поста
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
                            loading.onComment(post.getPost_id());
                        }
                    });
                }
            });


        }

        public void bindStoryies() {

        }

        private void updateLikeButton() {
            if (isLiked) {
                likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
            } else {
                likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
            }
        }

        private void updateLikesCounter() {
            likes.setText(likesList.size() + " likes");
        }
    }

    public interface Callback {
        void onPostsLoaded();
        void onComment(String post_id);
        void uploadStory();
    }

    @Override
    public void uploadStory() {
        loading.uploadStory();
    }
}
