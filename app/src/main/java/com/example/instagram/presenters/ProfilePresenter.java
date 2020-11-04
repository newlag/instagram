package com.example.instagram.presenters;

import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.example.instagram.models.FollowersModel;
import com.example.instagram.models.NotificationModel;
import com.example.instagram.models.PostsModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class ProfilePresenter {

    private UsersModel user_db = new UsersModel();
    private PostsModel posts_db = new PostsModel();
    private FollowersModel followers_db = new FollowersModel();
    private NotificationModel notif_db = new NotificationModel();

    private String userId;

    public ProfilePresenter(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void getProfile(final onProfileLoaded callback) {
        user_db.loadUser(userId, new UsersModel.onUserLoaded() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onProfileLoaded {
        void onSuccess(User user);
        void onFailure();
    }

    public void getPosts(final onPostsLoaded callback) {
        posts_db.loadPosts(userId, new PostsModel.onPostsLoaded() {
            @Override
            public void onSuccess(ArrayList<Post> posts) {
                callback.onSuccess(posts);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onPostsLoaded {
        void onSuccess(ArrayList<Post> posts);
        void onFailure();
    }

    public void getFollowers(final onFollowersLoaded callback) {
        followers_db.loadFollowers(userId, new FollowersModel.onFollowersLoaded() {
            @Override
            public void onSuccess(long followers) {
                callback.onSuccess(followers);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onFollowersLoaded {
        void onSuccess(long followers);
        void onFailure();
    }

    public void getFollowings(final onFollowingsLoaded callback) {
        followers_db.loadFollowings(userId, new FollowersModel.onFollowingsLoaded() {
            @Override
            public void onSuccess(long followings) {
                callback.onSuccess(followings);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onFollowingsLoaded {
        void onSuccess(long followings);
        void onFailure();
    }

    public void isFollow(final isUserFollow callback) {
        followers_db.isFollow(userId, getUserId(), new FollowersModel.isUserFollow() {
            @Override
            public void onSuccess(boolean follow) {
                callback.onSuccess(follow);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface isUserFollow {
        void onSuccess(boolean follow);
        void onFailure();
    }

    public void follow(final String follower_id, final onUserFollow callback) {
        followers_db.follow(follower_id, getUserId(), new FollowersModel.onUserFollow() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                notif_db.addFollowNotification(follower_id, getUserId());
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onUserFollow {
        void onSuccess();
        void onFailure();
    }

    public void unFollow(String follower_id, final onUserUnFollow callback) {
        followers_db.unFollow(follower_id, getUserId(),  new FollowersModel.onUserFollow() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public interface onUserUnFollow {
        void onSuccess();
        void onFailure();
    }
}
