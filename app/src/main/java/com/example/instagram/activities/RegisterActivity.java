package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements RegisterPresenter.View {

    private EditText mEmail, mFullname, mUsername, mPassword;
    private Button mRegisterButton, mBack;
    private FirebaseAuth mFirebaseAuth;
    private RegisterPresenter registerPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerPresenter = new RegisterPresenter(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.reg_email);
        mFullname = findViewById(R.id.reg_fullname);
        mUsername = findViewById(R.id.reg_username);
        mPassword = findViewById(R.id.reg_password);

        mRegisterButton = findViewById(R.id.reg_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String fullname = mFullname.getText().toString();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                registerPresenter.register(email, fullname, username, password);
            }
        });

        mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void login() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }

    @Override
    public void wrong(int message) {
        Toast.makeText(RegisterActivity.this, getString(message), Toast.LENGTH_SHORT).show();
    }
}
