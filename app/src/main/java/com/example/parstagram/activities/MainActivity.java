package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.parstagram.R;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public Toolbar mainToolbar;
    FrameLayout fragmentContainerView;
    public BottomNavigationView bottomNavigationView;
    public FragmentManager fragmentManager;
    public Fragment activeFragment;
    public Fragment composeFragment;
    public Fragment timelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();
        composeFragment = new ComposeFragment();
        timelineFragment = new TimelineFragment();
        activeFragment = timelineFragment;

        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, composeFragment).hide(composeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, timelineFragment).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_compose:
                    activeFragment = composeFragment;
                    break;
                case R.id.action_timeline:
                default:
                    activeFragment = timelineFragment;
            }
            // Hide everything except the active fragment
            fragmentManager.beginTransaction().hide(composeFragment).commit();
            fragmentManager.beginTransaction().hide(timelineFragment).commit();
            fragmentManager.beginTransaction().show(activeFragment).commit();
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