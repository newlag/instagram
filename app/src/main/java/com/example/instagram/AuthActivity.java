package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagram.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements AuthPresenter.View {

    EditText mLoginText, mPasswordText;
    Button mLoginButton, mRegisterButton;
    AuthPresenter authPresenter;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPresenter = new AuthPresenter(this);

        if (authPresenter.isLogged()) {
            login();
        } else {
            setContentView(R.layout.activity_auth);
            mLoginText = findViewById(R.id.reg_email);
            mPasswordText = findViewById(R.id.reg_password);
            mLoginButton = findViewById(R.id.reg_button);
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String login = mLoginText.getText().toString();
                    String password = mPasswordText.getText().toString();
                    authPresenter.login(login, password);
                }
            });
            mRegisterButton = findViewById(R.id.register_button);
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AuthActivity.this, RegisterActivity.class));
                }
            });
        }
    }

    @Override
    public void login() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
    }

    @Override
    public void wrong() {
        Toast.makeText(AuthActivity.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
    }
}
