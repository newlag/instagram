package com.example.instagram.models;

import androidx.annotation.NonNull;

import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PostsModel {

    private static final String DB_PATH = "posts";
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void loadPosts(final String user_id, final onPostsLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> posts = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getUser_id().equals(user_id)) {
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                callback.onSuccess(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public void loadPosts(final ArrayList<User> users, final onPostsLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> posts = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (User user : users) {
                        if (user.getUser_id().equals(post.getUser_id())) {
                            posts.add(post);
                            break;
                        }
                    }
                }
                callback.onSuccess(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onPostsLoaded {
        void onSuccess(ArrayList<Post> posts);
        void onFailure();
    }

    public void likePost(String postId, String userId, boolean isPostLiked, final onPostLiked callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(postId).child("likes").child(userId);
        if (isPostLiked) {
            reference.removeValue().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure();
                }
            });
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user_id", userId);
            reference.setValue(hashMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure();
                }
            });
        }
    }

    public interface onPostLiked {
        void onFailure();
    }

    public void loadLikes(String postId, final onLikesLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(postId).child("likes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Post.Likes> likes = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    likes.add(dataSnapshot.getValue(Post.Likes.class));
                }
                callback.onSuccess(likes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onLikesLoaded {
        void onSuccess(ArrayList<Post.Likes> likes);
        void onFailure();
    }

    public void sendComment(String userId, String postId, String text, final onCommentSended callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(postId).child("comments");
        final String key = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment_id", key);
        hashMap.put("user_id", userId);
        hashMap.put("text", text);
        hashMap.put("time", ServerValue.TIMESTAMP);
        reference.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess(key);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
            }
        });
    }

    public interface onCommentSended {
        void onSuccess(String comment_id);
        void onFailure();
    }

    public void loadComments(String postId, final onCommentsLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(postId).child("comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post.Comments> comments = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    comments.add(dataSnapshot.getValue(Post.Comments.class));
                }
                callback.onSuccess(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onCommentsLoaded {
        void onSuccess(ArrayList<Post.Comments> comments);
        void onFailure();
    }

    public void deleteComment(String postId, String commentId, final onCommentDeleted callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(postId).child("comments").child(commentId);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.onSuccess();
                if (!task.isSuccessful()) callback.onFailure();
            }
        });
    }

    public interface onCommentDeleted {
        void onSuccess();
        void onFailure();
    }

    public void loadFollowingsPosts(final ArrayList<User> users, final int max_count, final onFollowingPostsLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> posts = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (User user : users) {

                        if (post.getUser_id().equals(user.getUser_id())) {
                            posts.add(post);
                            break;
                        }
                    }
                    if (posts.size() == max_count) {
                        break;
                    }
                }
                callback.onSuccess(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onFollowingPostsLoaded {
        void onSuccess(ArrayList<Post> posts);
        void onFailure();
    }

    public void uploadPost(String userId, String description, String url, final onPostUploaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH);
        String key = reference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("post_id", key);
        map.put("user_id", userId);
        map.put("description", description);
        map.put("time", ServerValue.TIMESTAMP);
        map.put("image", url);
        map.put("url", url);
        reference.child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public interface onPostUploaded {
        void onSuccess();
        void onFailure();
    }

    public void isLikeExist(final String post_id, String user_id, final onLikeExist callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(post_id).child("likes").child(user_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onLikeExist {
        void onSuccess(boolean status);
        void onFailure();
    }

    public void loadPost(String post_id, final onPostLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(post_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.getValue(Post.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onPostLoaded {
        void onSuccess(Post post);
        void onFailure();
    }

    public void isCommentExist(String post_id, final String comment_id, final onCommentExist callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(post_id).child("comments").child(comment_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onCommentExist {
        void onSuccess(boolean status);
        void onFailure();
    }

    public void loadComment(String post_id, String comment_id, final onCommentLoaded callback) {
        DatabaseReference reference = db.getReference(DB_PATH).child(post_id).child("comments").child(comment_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) callback.onSuccess(snapshot.getValue(Post.Comments.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    }

    public interface onCommentLoaded {
        void onSuccess(Post.Comments comment);
        void onFailure();
    }


}
