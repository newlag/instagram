package com.example.instagram.presenters;
import com.example.instagram.data.User;
import com.example.instagram.models.UsersModel;
import java.util.ArrayList;

public class SearchPresenter {

    private connectionError connection;
    private UsersModel users_db = new UsersModel();

    public SearchPresenter(connectionError connection) {
        this.connection = connection;
    }

    public void findUsers(String name, final onUsersFound callback) {
        users_db.findUsers(name, new UsersModel.onUsersFound() {
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

    public interface onUsersFound {
        void onSuccess(ArrayList<User> users);
    }

    public interface connectionError {
        void showConnectionError();
    }

}
