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

public class FollowersModel {

    private static final String DB_PATH = "followers";
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void loadFollowers(String user_id, final onFollowersLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child("follow");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onFollowersLoaded {
        void onSuccess(long followers);
        void onFailure();
    }

    public void loadFollowings(String user_id, final onFollowingsLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onFollowingsLoaded {
        void onSuccess(long followings);
        void onFailure();
    }

    public void isFollow(String user_id, final String follower_id, final isUserFollow callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child("follow").child(follower_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onSuccess(true);
                } else {
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface isUserFollow {
        void onSuccess(boolean follow);
        void onFailure();
    }

    public void follow(final String user_id, final String follower_id, final onUserFollow callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child("follow").child(follower_id);
        reference.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference1 = db.getReference(DB_PATH).child(follower_id).child("following").child(user_id);
                reference1.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
            }
        });
    }

    public interface onUserFollow {
        void onSuccess();
        void onFailure();
    }

    public void unFollow(final String user_id, final String follower_id, final onUserFollow callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(user_id).child("follow").child(follower_id);
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference1 = db.getReference(DB_PATH).child(follower_id).child("following").child(user_id);
                reference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
            }
        });
    }

    public interface onUserUnFollow {
        void onSuccess();
        void onFailure();
    }

    public void loadFollowList(String user_id, int type, final onFollowListLoaded callback) {
       DatabaseReference reference = db.getReference(DB_PATH).child(user_id);
       switch(type) {
           case 0:
               reference = reference.child("follow");
           break;
           case 1:
               reference = reference.child("following");
           break;
       }
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               ArrayList<User> usersId = new ArrayList<>();
               for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                   //usersId.add(dataSnapshot.getValue(User.class));
                   usersId.add(new User(dataSnapshot.getKey()));
               }
               callback.onSuccess(usersId);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               callback.onFailure();
           }
       });
    }

    public interface onFollowListLoaded {
        void onSuccess(ArrayList<User> users);
        void onFailure();
    }
}
