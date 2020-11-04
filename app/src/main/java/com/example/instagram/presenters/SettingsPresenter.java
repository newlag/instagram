package com.example.instagram.presenters;

import android.net.Uri;
import com.example.instagram.R;
import com.example.instagram.data.User;
import com.example.instagram.models.PhotoModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsPresenter {

    private String userId;
    private UsersModel user_db = new UsersModel();
    private PhotoModel photo_db;
    private connectionError connection;

    public SettingsPresenter(connectionError connection, String userId) {
        this.connection = connection;
        this.userId = userId;
        photo_db = new PhotoModel(userId);
    }

    public void loadProfile(final onProfileLoaded callback) {
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

    public interface onProfileLoaded {
        void onSuccess(User user);
    }

    public void updateProfile(final User user, final onProfileUpdated callback) {
        if (user.getFullname().length() < 1) {
            callback.onFailure(R.string.incor_fullname);
        } else if (user.getUsername().length() < 1 || !user.getUsername().matches("[a-z0-9]+")) {
            callback.onFailure(R.string.incor_username);
        } else if (user.getBio().length() > 64) {
            callback.onFailure(R.string.incor_bio);
        } else {
            user_db.isTheNameUsed(user, new UsersModel.onNameUsed() {
                @Override
                public void onSuccess(boolean used) {
                    if (used) {
                        callback.onFailure(R.string.username_use);
                    } else {
                        user_db.updateProfile(user, new UsersModel.onProfileUpdated() {
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
                }

                @Override
                public void onFailure() {
                    connection.showConnectionError();
                }
            });
        }
    }

    public interface onProfileUpdated {
        void onSuccess();
        void onFailure(int message);
    }

    public void uploadPhoto(Uri image, final onPhotoUploaded callback) {
        photo_db.uploadPhoto(image, new PhotoModel.onPhotoUploaded() {
            @Override
            public void onSuccess(String path) {
                callback.onSuccess(path);
            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onPhotoUploaded {
        void onSuccess(String path);
    }

    public interface connectionError {
        void showConnectionError();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
