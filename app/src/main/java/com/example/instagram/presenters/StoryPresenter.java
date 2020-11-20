package com.example.instagram.presenters;

import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.models.StoryModel;
import com.example.instagram.models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class StoryPresenter {

    private StoryModel story_db = new StoryModel();
    private UsersModel user_db = new UsersModel();
    private Connection connection;

    public StoryPresenter(Connection connection) {
        this.connection = connection;
    }

    public String getUserId() { return FirebaseAuth.getInstance().getCurrentUser().getUid(); }

    public void loadUserStory(final String user_id, final onUserStoryLoaded callback) {
        story_db.loadUserStory(user_id, getUserId(), new StoryModel.onStoryFeedLoaded() {
            @Override
            public void onSuccess(final ArrayList<Story> story) {
                user_db.loadUser(user_id, new UsersModel.onUserLoaded() {
                    @Override
                    public void onSuccess(User user) {
                        callback.onSuccess(story, user);
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

    public interface onUserStoryLoaded {
        void onSuccess(ArrayList<Story> story, User user);
    }

    public void setStoryView(String user_id, String story_id, String viewer_id) {
        story_db.setStoryViewed(user_id, story_id, viewer_id);
    }

    public interface Connection {
        void showConnectionError();
    }

}
