package com.example.instagram.presenters;

import com.google.firebase.auth.FirebaseAuth;

public class MainPresenter {

    public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
