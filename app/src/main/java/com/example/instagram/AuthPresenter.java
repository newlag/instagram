package com.example.instagram;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthPresenter {

    private FirebaseAuth firebaseAuth;
    View view;

    public AuthPresenter(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.view = view;
    }

    public boolean isLogged() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("userId");

        boolean logged = false;
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return true;
        }
        return logged;
    }

    public void login(String login, String pass) {
        firebaseAuth.signInWithEmailAndPassword(login, pass).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            view.login();
                        } else {
                            view.wrong();
                        }

                    }
                });
    }

    public interface View {
        void login();
        void wrong();
    }

}
