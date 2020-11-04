package com.example.instagram.models;

import androidx.annotation.NonNull;

import com.example.instagram.data.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NotificationModel {

    private static final String DB_PATH = "notifications";
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void addFollowNotification(final String recipient_id, final String user_id) {
        DatabaseReference reference = db.getReference("users").child(recipient_id).child(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification.getType() == 1 && notification.getUser_id().equals(user_id)) {
                        DatabaseReference reference1 = db.getReference("users").child(recipient_id).child(DB_PATH).child(notification.getNotif_id());
                        reference1.removeValue();
                    }
                }
                addNotification(recipient_id, 1, user_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNotification(String recipient_id, int type, String user_id, String... p) { // p[0] - post_id, p[1] - comment_id
        HashMap<String, Object> hashMap = new HashMap<>();
        DatabaseReference reference = db.getReference("users").child(recipient_id).child(DB_PATH);
        String key = reference.push().getKey();

        hashMap.put("notif_id", key);
        hashMap.put("time", ServerValue.TIMESTAMP);
        hashMap.put("type", type);
        hashMap.put("user_id", user_id);

        switch (type) {
            case 0: // like
                hashMap.put("post_id", p[0]);
            break;
            case 2: // comment
                hashMap.put("post_id", p[0]);
                hashMap.put("comment_id", p[1]);
            break;
        }

        reference.child(key).setValue(hashMap);
    }

    public void loadNotifications(String user_id, final onNotificationLoaded callback) {
        DatabaseReference reference = db.getReference("users").child(user_id).child(DB_PATH);
        reference.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Notification> notifications = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    notifications.add(dataSnapshot.getValue(Notification.class));
                }
                Collections.reverse(notifications);
                callback.onSuccess(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onNotificationLoaded {
        void onSuccess(ArrayList<Notification> notifications);
        void onFailure();
    }

    public void deleteNotification(String user_id, String notif_id) {
        DatabaseReference reference = db.getReference("users").child(user_id).child(DB_PATH).child(notif_id);
        reference.removeValue();
    }
}
