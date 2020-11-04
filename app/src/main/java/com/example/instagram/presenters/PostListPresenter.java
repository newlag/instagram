package com.example.instagram.presenters;

import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.example.instagram.models.FollowersModel;
import com.example.instagram.models.NotificationModel;
import com.example.instagram.models.PostsModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostListPresenter {

    private UsersModel user_db = new UsersModel();
    private PostsModel post_db = new PostsModel();
    private FollowersModel follower_db = new FollowersModel();
    private NotificationModel notif_db = new NotificationModel();

    private connectionError connection;
    private String userId;

    private static final int MAX_POSTS = 30; // Максимальнок к-во загружаемых последних постов


    public PostListPresenter(connectionError connection) {
        this.connection = connection;
        userId = getUserId();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void getUserName(String userId, final onUserNameLoaded callback) {
        user_db.loadUser(userId, new UsersModel.onUserLoaded() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onUserNameLoaded {
        void onSuccess(User user);
    }

    public interface connectionError {
        void showConnectionError();
    }

    public void loadLikes(String postId, final onLikesLoaded callback) {
        post_db.loadLikes(postId, new PostsModel.onLikesLoaded() {
            @Override
            public void onSuccess(ArrayList<Post.Likes> likes) {
                callback.onSuccess(likes);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onLikesLoaded {
        void onSuccess(ArrayList<Post.Likes> likes);
    }

    public void likePost(Post post, boolean isLiked) {
        String postId = post.getPost_id();
        post_db.likePost(postId, userId, isLiked, new PostsModel.onPostLiked() {
            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
        notif_db.addNotification(post.getUser_id(), 0, userId, post.getPost_id());

    }

    public boolean isPostLiked(ArrayList<Post.Likes> likes) {
        for (Post.Likes like : likes) {
            if (like.getUser_id().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public void loadPosts(final onPostsLoaded callback) {
        follower_db.loadFollowList(userId, 1, new FollowersModel.onFollowListLoaded() {
            @Override
            public void onSuccess(ArrayList<String> users) {
                post_db.loadFollowingsPosts(users, MAX_POSTS, new PostsModel.onFollowingPostsLoaded() {
                    @Override
                    public void onSuccess(ArrayList<Post> posts) {
                        callback.onSuccess(posts);
                    }

                    @Override
                    public void onFailure() {
                        connection.showConnectionError();
                    }
                });
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public void loadPosts(String userId, final onPostsLoaded callback) {
        post_db.loadPosts(userId, new PostsModel.onPostsLoaded() {
            @Override
            public void onSuccess(ArrayList<Post> posts) {
                callback.onSuccess(posts);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onPostsLoaded {
        void onSuccess(ArrayList<Post> posts);
    }
}
