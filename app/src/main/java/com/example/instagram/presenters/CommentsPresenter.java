package com.example.instagram.presenters;

import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.example.instagram.models.NotificationModel;
import com.example.instagram.models.PostsModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CommentsPresenter {

    private connectionError connection;
    private UsersModel users_db = new UsersModel();
    private PostsModel posts_db = new PostsModel();
    private NotificationModel notif_db = new NotificationModel();

    public CommentsPresenter(connectionError connection) {
        this.connection = connection;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadPost(String post_id, final onPostLoaded callback) {
        posts_db.loadPost(post_id, new PostsModel.onPostLoaded() {
            @Override
            public void onSuccess(Post post) {
                callback.onSuccess(post);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onPostLoaded {
        void onSuccess(Post post);
    }

    public void loadUser(String userId, final onUserLoaded callback) {
        users_db.loadUser(userId, new UsersModel.onUserLoaded() {
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

    public void sendComment(String text, final String postId, final onCommentSended callback) {
        posts_db.sendComment(getUserId(), postId, text, new PostsModel.onCommentSended() {
            @Override
            public void onSuccess(final String comment_id) {
                callback.onSuccess();
                posts_db.loadPost(postId, new PostsModel.onPostLoaded() {
                    @Override
                    public void onSuccess(Post post) {
                        if (!post.getUser_id().equals(getUserId())) {
                            notif_db.addNotification(post.getUser_id(), 2, getUserId(), postId, comment_id);
                        }
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

    public interface onCommentSended {
        void onSuccess();
    }

    public void loadComments(String postId, final onCommentsLoaded callback) {
        posts_db.loadComments(postId, new PostsModel.onCommentsLoaded() {
            @Override
            public void onSuccess(ArrayList<Post.Comments> comments) {
                callback.onSuccess(comments);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public void deleteComment(String postId, String commentId, final onCommentDeleted callback) {
        posts_db.deleteComment(postId, commentId, new PostsModel.onCommentDeleted() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onCommentDeleted {
        void onSuccess();
    }

    public interface onCommentsLoaded {
        void onSuccess(ArrayList<Post.Comments> comments);
    }

    public interface connectionError {
        void showConnectionError();
    }
}
