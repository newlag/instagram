package com.example.instagram.presenters;

import android.util.Log;

import com.example.instagram.data.User;
import com.example.instagram.models.FollowersModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FollowersPresenter {

    private String userId;
    private int type;
    private onConnectionError connection;

    private FollowersModel followers_db = new FollowersModel();
    private UsersModel user_db = new UsersModel();

    public FollowersPresenter(String userId, int type, onConnectionError connection) {
        this.userId = userId;
        this.type = type;
        this.connection = connection;
    }

    public void loadFollowers(final onFollowersLoaded callback) {
        followers_db.loadFollowList(userId, type, new FollowersModel.onFollowListLoaded() {
            @Override
            public void onSuccess(ArrayList<User> usersId) {
                Log.i("[Presenter]", "лолкекчебурек : " + usersId.size());
                user_db.loadUsers(usersId, new UsersModel.onUsersLoaded() {
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

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public void deleteFollower(String followerId) {
        followers_db.unFollow(userId, followerId, new FollowersModel.onUserFollow() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {
                connection.showConnectionError();
            }
        });
    }

    public interface onFollowersLoaded {
        void onSuccess(ArrayList<User> users);
    }

    public interface onConnectionError {
        void showConnectionError();
    }

    public boolean isMyProfile() {
        return userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
