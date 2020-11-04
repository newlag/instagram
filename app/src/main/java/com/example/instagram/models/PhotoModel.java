package com.example.instagram.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PhotoModel {

    private String userId;
    private FirebaseStorage db;

    public PhotoModel(String userId) {
        this.userId = userId;
        db = FirebaseStorage.getInstance();
    }

    public void uploadPhoto(Uri image, final onPhotoUploaded callback) {
        final StorageReference reference = db.getReference(userId).child("avatar").child("photo.jpg");
        UploadTask upload = reference.putFile(image);
        upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.onSuccess(uri.toString());
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

    public interface onPhotoUploaded {
        void onSuccess(String path);
        void onFailure();
    }
}
