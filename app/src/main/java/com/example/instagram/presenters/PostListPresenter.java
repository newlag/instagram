package com.example.instagram.presenters;

import android.net.Uri;

import com.example.instagram.data.Post;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.models.FollowersModel;
import com.example.instagram.models.NotificationModel;
import com.example.instagram.models.PhotoModel;
import com.example.instagram.models.PostsModel;
import com.example.instagram.models.StoryModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;

public class PostListPresenter {

    private UsersModel user_db = new UsersModel();
    private PostsModel post_db = new PostsModel();
    private StoryModel story_db = new StoryModel();
    private PhotoModel photo_db = new PhotoModel();
    private FollowersModel follower_db = new FollowersModel();
    private NotificationModel notif_db = new NotificationModel();

    private connectionError connection;
    private String userId;

    private static final int MAX_POSTS = 30; // Максимальнок к-во загружаемых последних постов


    public PostListPresenter(connectionError connection) {
        this.connection = connection;
        userId = getUserId();
    }

    public String getUserId() {
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

    /*public void loadPosts(final onPostsLoaded callback) {
        follower_db.loadFollowList(userId, 1, new FollowersModel.onFollowListLoaded() {
            @Override
            public void onSuccess(final ArrayList<User> users) {
                post_db.loadFollowingsPosts(users, MAX_POSTS, new PostsModel.onFollowingPostsLoaded() {
                    @Override
                    public void onSuccess(ArrayList<Post> posts) {
                        callback.onSuccess(posts, users);
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
    }*/

    public void loadFollowings(final onFollowingsLoaded callback) {
        follower_db.loadFollowList(getUserId(), 1, new FollowersModel.onFollowListLoaded() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                callback.onSuccess(users);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onFollowingsLoaded {
        void onSuccess(ArrayList<User> users);
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

    public void loadPosts(ArrayList<User> users, final onPostsLoaded callback) {
        //post_db.l
        post_db.loadPosts(users, new PostsModel.onPostsLoaded() {
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

    public void loadUser(String user_id, final onUserLoaded callback) {
        user_db.loadUser(user_id, new UsersModel.onUserLoaded() {
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

    public interface onUserLoaded {
        void onSuccess(User user);
    }

    public void uploadPhoto(String path) {
        photo_db.uploadStory(Uri.parse(path), getUserId(), new PhotoModel.onStoryUploaded() {
            @Override
            public void onSuccess(String url) {
                uploadStory(url, getUserId());
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    private void uploadStory(String url, String user_id) {
        story_db.uploadStory(url, user_id, new StoryModel.onStoryUploaded() {
            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public void loadStory(ArrayList<User> users, final onStoryLoaded callback) {
        story_db.loadStoryFeed(users, getUserId(), new StoryModel.onStoryFeedLoaded() {
            @Override
            public void onSuccess(ArrayList<Story> story) {
                callback.onSuccess(story);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onStoryLoaded {
        void onSuccess(ArrayList<Story> story);
    }

}
