package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "SignupActivity";

    public RelativeLayout signupRelativeLayout;
    public Toolbar signupToolbar;
    public RelativeLayout editTextLayout;
    public TextInputLayout usernameInputLayout;
    public EditText usernameEditText;
    public TextInputLayout passwordInputLayout;
    public EditText passwordEditText;
    public TextInputLayout emailInputLayout;
    public EditText emailEditText;
    public Button signupButton;
    public Animation shake;
    public ProgressDialog signupProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupRelativeLayout = findViewById(R.id.signupRelativeLayout);
        signupToolbar = findViewById(R.id.signupToolbar);
        setSupportActionBar(signupToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editTextLayout = findViewById(R.id.editTextLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        signupButton = findViewById(R.id.signupButton);
        shake = AnimationUtils.loadAnimation(SignupActivity.this, R.anim.shake);
        signupProgressDialog = new ProgressDialog(SignupActivity.this);
        signupProgressDialog.setMessage(getResources().getString(R.string.verifying_credentials));

        // Enable username hint only if username field is in focus
        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) usernameInputLayout.setHint(R.string.Username_asterisk);
            else usernameInputLayout.setHint("");
        });

        // Enable password hint only if password field is in focus
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) passwordInputLayout.setHint(R.string.Password_asterisk);
            else passwordInputLayout.setHint("");
        });

        // Enable email hint only if email field is in focus
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) emailInputLayout.setHint(R.string.Email);
            else emailInputLayout.setHint("");
        });

        // Set click logic for button
        signupButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick loginButton");
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            hideSoftKeyboard(signupRelativeLayout);
            signupUser(username, password, email);
        });
    }

    // Uses parse method logInInBackground to attempt to log in with the credentials given
    private void signupUser(String username, String password, String email) {
        Log.i(TAG, "Attempting to sign up user " + username);
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        if (!email.isEmpty()) user.setEmail(email);
        signupProgressDialog.show();
        user.signUpInBackground(e -> {
            signupProgressDialog.dismiss();
            if (e != null) { // The signup failed
                Log.e(TAG, "Signup failed", e);
                Snackbar.make(editTextLayout, R.string.signup_failed, Snackbar.LENGTH_LONG).show();
                editTextLayout.setBackgroundColor(Color.argb(100, 255, 0, 0));
                editTextLayout.startAnimation(shake);
                return;
            }
            else { // The signup succeded
                Toast.makeText(SignupActivity.this, getResources().getString(R.string.welcome) + " " + username + "!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Minimizes the soft keyboard
    public void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}