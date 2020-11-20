package com.example.instagram.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagram.DateUtil;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class StoryModel {

    private static final String DB_PATH = "story";
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void loadStoryFeed(final ArrayList<User> users, final String user_id, final onStoryFeedLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Story> story_list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = new User(dataSnapshot.getKey());
                    //User user = dataSnapshot.getValue(User.class);
                    for (User u : users) {
                        if (u.getUser_id().equals(user.getUser_id())) {
                            Story story = null;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Story s = data.getValue(Story.class);
                                if (DateUtil.isStoryActive(s.getTime())) {

                                    if (story != null && s.getTime() > story.getTime() || story == null) {
                                        story = s;
                                        story.setViewed(data.child("views").hasChild(user_id));
                                    }
                                }
                            }
                            if (story != null) {
                                story_list.add(story);
                            }

                        }
                    }
                }
                callback.onSuccess(story_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onStoryFeedLoaded {
        void onSuccess(ArrayList<Story> story);
        void onFailure();
    }

    public void loadUserStory(String user_id, final String watcher_id, final onStoryFeedLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Story> story = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story s = dataSnapshot.getValue(Story.class);
                    if (DateUtil.isStoryActive(s.getTime())) {
                        s.setViewed(dataSnapshot.child("views").hasChild(watcher_id));
                        s.setViews_counter((int) dataSnapshot.child("views").getChildrenCount());
                        story.add(s);
                    }
                }

                callback.onSuccess(story);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public void uploadStory(String url, String user_id, final onStoryUploaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id);
        String key = reference.push().getKey();
        reference = reference.child(key);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("story_id", key);
        hashMap.put("user_id", user_id);
        hashMap.put("time", ServerValue.TIMESTAMP);
        hashMap.put("url", url);
        reference.setValue(hashMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
            }
        });
    }

    public interface onStoryUploaded {
        void onFailure();
    }

    public void setStoryViewed(String user_id, String story_id, String viewer_id) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child(story_id).child("views").child(viewer_id);
        reference.setValue(true);
    }


}
