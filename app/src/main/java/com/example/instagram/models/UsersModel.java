package com.example.instagram.models;

import androidx.annotation.NonNull;

import com.example.instagram.data.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersModel {

    private static final String DB_PATH = "users";
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void loadUser(String user_id, final onUserLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onUserLoaded {
        void onSuccess(User user);
        void onFailure();
    }

    public void loadUsers(final ArrayList<User> users_list, final onUsersLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (User u : users_list) {
                        if (u.getUser_id().equals(user.getUser_id())) {
                            users.add(user);
                            break; 
                        }
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onUsersLoaded {
        void onSuccess(ArrayList<User> users);
        void onFailure();
    }

    public void isTheNameUsed(final User data, final onNameUsed callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean used = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUsername().equals(data.getUsername()) && !user.getUser_id().equals(data.getUser_id())) {
                        used = true;
                        break;
                    }
                }
                callback.onSuccess(used);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onNameUsed {
        void onSuccess(boolean used);
        void onFailure();
    }

    public void updateProfile(User user, final onProfileUpdated callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user.getUser_id());
        HashMap<String, Object> profile = new HashMap<>();
        profile.put("user_id", user.getUser_id());
        profile.put("email", user.getEmail());
        profile.put("fullname",  user.getFullname());
        profile.put("username", user.getUsername());
        profile.put("image", user.getImage());
        profile.put("bio", user.getBio());
        reference.setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
            }
        });

    }

    public interface onProfileUpdated {
        void onSuccess();
        void onFailure();
    }

    public void findUsers(final String name, final onUsersFound callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference
                .orderByChild("username")
                .startAt(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<User> users = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (!user.getUsername().contains(name)) {
                            break;
                        }
                        users.add(user);
                    }
                    callback.onSuccess(users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure();
                }
            });
    }

    public interface onUsersFound {
        void onSuccess(ArrayList<User> users);
        void onFailure();
    }
}
