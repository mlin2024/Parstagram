package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.parstagram.R;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

public class LoginOrSignupActivity extends AppCompatActivity {
    public static final String TAG = "LoginOrSignupActivity";

    public Button loginStartButton;
    public Button signupStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
            finish();
        }

        loginStartButton = findViewById(R.id.loginStartButton);
        signupStartButton = findViewById(R.id.signupStartButton);

        loginStartButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick loginStartButton");
            goLoginActivity();
        });

        signupStartButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick signupStartButton");
            goSignupActivity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
            finish();
        }
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void goSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}