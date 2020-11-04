package com.example.instagram.presenters;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.instagram.models.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPresenter {

    private connectionError connection;

    private String userId;

    private PostsModel posts_db = new PostsModel();

    public UploadPresenter(connectionError connection) {
        this.connection = connection;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void uploadPost(Uri image, final String description, final PostPresenterDetails.onPostUploaded callback) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child(userId + "/" + System.currentTimeMillis() + ".jpg");
        final UploadTask uploadTask = reference.putFile(image);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        posts_db.uploadPost(userId, description, uri.toString(), new PostsModel.onPostUploaded() {
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
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                connection.showConnectionError();
            }
        });
    }

    public interface onPostUploaded {
        void onSuccess();
    }

    public interface connectionError {
        void showConnectionError();
    }

}
