package com.example.parstagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.parstagram.R;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public Toolbar mainToolbar;
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_compose:
                    fragment = new ComposeFragment();
                    break;
                case R.id.action_timeline:
                default:
                    fragment = new TimelineFragment();
            }
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_timeline);
    }

    // Starts an intent to go to the loginOrSignup activity
    private void goLoginOrSignupActivity() {
        Intent intent = new Intent(this, LoginOrSignupActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logoutMenuItem:
                logout();
                return true;
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

    private void logout() {
        ParseUser.logOut();
        goLoginOrSignupActivity();
        finish();
    }
}