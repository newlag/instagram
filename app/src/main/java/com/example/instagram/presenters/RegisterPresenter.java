package com.example.instagram;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterPresenter {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    View view;

    public RegisterPresenter(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.view = view;
    }

    public void register(final String email, final String fullname, final String username, final String password) { // Напиши проверку требований к логину, паролю и т.д.
        if (!email.contains("@") || !email.contains(".")) {
            view.wrong(R.string.incor_email);
            return;
        }
        if (fullname.length() < 1) {
            view.wrong(R.string.incor_fullname);
            return;
        }
        if (username.length() < 1 || !username.matches("[a-z0-9]+")) {
            view.wrong(R.string.incor_username);
            return;
        }
        if (password.length() < 6) {
            view.wrong(R.string.incor_incorpassword);
            return;
        }
        // Блок регистрации
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userId = user.getUid();
                            databaseReference = databaseReference.child("users").child(userId);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user_id", userId);
                            map.put("email", email);
                            map.put("fullname", fullname);
                            map.put("username", username);
                            map.put("password", password);
                            map.put("bio", null);
                            databaseReference.setValue(map).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                view.login();
                                            }
                                        }
                                    });

                        } else {
                            view.wrong(R.string.error_register);
                        }
                    }
                });
    }



    public interface View {
        void login();
        void wrong(int message);
    }
}
