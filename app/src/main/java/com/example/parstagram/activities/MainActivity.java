package com.example.parstagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.FeedFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    public static final String TAG = "MainActivity";
    public ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        pbLoading = findViewById(R.id.pbLoading);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment composeFragment = new ComposeFragment(MainActivity.this);
        final Fragment feedFragment = new FeedFragment();
        final Fragment profileFragment = new ProfileFragment();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = feedFragment;
                        break;
                    case R.id.action_compose:
                        fragment = composeFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;
                    default:
                        fragment = new ComposeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // set default selection
        bottomNavigation.setSelectedItemId(R.id.action_home);

    }

    public void goToFeedFrag() {
        hideProgressBar();
        bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    public void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }



}