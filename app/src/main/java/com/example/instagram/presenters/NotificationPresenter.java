package com.example.instagram.presenters;

import com.example.instagram.data.Notification;
import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.example.instagram.models.FollowersModel;
import com.example.instagram.models.NotificationModel;
import com.example.instagram.models.PostsModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class NotificationPresenter {

    private String user_id;

    private connectionError connection;
    private PostsModel post_db = new PostsModel();
    private NotificationModel notif_db = new NotificationModel();
    private UsersModel user_db = new UsersModel();
    private FollowersModel follower_db = new FollowersModel();

    public NotificationPresenter(connectionError connection) {
        user_id = getUserId();
        this.connection = connection;
    }

    public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void checkNotification(final Notification notification, final onNotificationChecked callback) {
        switch(notification.getType()) {
            case 0: // like
                post_db.isLikeExist(notification.getPost_id(), notification.getUser_id(), new PostsModel.onLikeExist() {
                    @Override
                    public void onSuccess(boolean status) {
                        callback.isNotificationExist(status);
                        if (!status) {
                            remove(notification);
                        }
                    }

                    @Override
                    public void onFailure() {
                        connection.showConnectionError();
                    }
                });
            break;
            case 1: // follow
                follower_db.isFollow(user_id, notification.getUser_id(), new FollowersModel.isUserFollow() {
                    @Override
                    public void onSuccess(boolean follow) {
                        callback.isNotificationExist(follow);
                        if (!follow) {
                            remove(notification);
                        }
                    }

                    @Override
                    public void onFailure() {
                        connection.showConnectionError();
                    }
                });
            break;
            case 2: // comment
                post_db.isCommentExist(notification.getPost_id(), notification.getComment_id(), new PostsModel.onCommentExist() {
                    @Override
                    public void onSuccess(boolean status) {
                        callback.isNotificationExist(status);
                        if (!status) {
                            remove(notification);
                        }
                    }

                    @Override
                    public void onFailure() {
                        connection.showConnectionError();
                    }
                });
            break;
        }
    }

    private void remove(Notification notification) {
        notif_db.deleteNotification(notification.getUser_id(), notification.getNotif_id());
    }

    public interface onNotificationChecked {
        void isNotificationExist(boolean status);
    }

    public void loadNotificationDetails(final Notification notification, final onNotificationDetailsLoaded callback) {
        user_db.loadUser(notification.getUser_id(), new UsersModel.onUserLoaded() {
            @Override
            public void onSuccess(final User user) {
                switch (notification.getType()) {
                    case 0: // like
                        post_db.loadPost(notification.getPost_id(), new PostsModel.onPostLoaded() {
                            @Override
                            public void onSuccess(Post post) {
                                callback.onSuccess(user, post.getUrl());
                            }

                            @Override
                            public void onFailure() {
                                connection.showConnectionError();
                            }
                        });
                    break;
                    case 1: // follow
                        callback.onSuccess(user);
                    break;
                    case 2: // comment
                        post_db.loadPost(notification.getPost_id(), new PostsModel.onPostLoaded() {
                            @Override
                            public void onSuccess(final Post post) {
                                post_db.loadComment(notification.getPost_id(), notification.getComment_id(), new PostsModel.onCommentLoaded() {
                                    @Override
                                    public void onSuccess(Post.Comments comment) {
                                        callback.onSuccess(user, post.getUrl(), comment.getText());
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
                    break;
                }
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onNotificationDetailsLoaded {
        void onSuccess(User user, String... content);
    }

    public void loadNotificatons(final onNotificationsLoaded callback) {
        notif_db.loadNotifications(user_id, new NotificationModel.onNotificationLoaded() {
            @Override
            public void onSuccess(ArrayList<Notification> notifications) {
                callback.onSuccess(notifications);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onNotificationsLoaded {
        void onSuccess(ArrayList<Notification> notifications);
    }

    public interface connectionError {
        void showConnectionError();
    }
}
