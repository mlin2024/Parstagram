package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.parstagram.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    public RelativeLayout loginRelativeLayout;
    public RelativeLayout editTextLayout;
    public TextInputLayout usernameInputLayout;
    public EditText usernameEditText;
    public TextInputLayout passwordInputLayout;
    public EditText passwordEditText;
    public Button loginButton;
    Animation shake;
    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginRelativeLayout = findViewById(R.id.loginRelativeLayout);
        editTextLayout = findViewById(R.id.editTextLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        usernameInputLayout.setHint(R.string.username);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordInputLayout.setHint(R.string.password);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
        loginProgressDialog = new ProgressDialog(LoginActivity.this);
        loginProgressDialog.setMessage(R.string.verifying_credentials + "");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick loginButton");
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                hideSoftKeyboard(loginRelativeLayout);
                loginUser(username, password);
            }
        });
    }

    // Uses parse method logInInBackground to attempt to log in with the credentials given
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        loginProgressDialog.show();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                loginProgressDialog.dismiss();
                if (e != null) { // The login failed
                    Log.e(TAG, "Login failed", e);
                    Snackbar.make(editTextLayout, R.string.login_failed, Snackbar.LENGTH_LONG).show();
                    editTextLayout.setBackgroundColor(Color.argb(100, 255, 0, 0));
                    editTextLayout.startAnimation(shake);
                    return;
                }
                else { // The login succeded
                    Toast.makeText(LoginActivity.this, R.string.welcome + username + "!", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                    finish();
                }
            }
        });
    }

    // Starts an intent to go to the main activity
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Minimizes the soft keyboard
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}