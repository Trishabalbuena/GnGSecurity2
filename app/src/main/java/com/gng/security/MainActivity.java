package com.gng.security;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("GnGSecurityPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isNewUser", false)) {
            showNotificationsDialog();
        }
    }

    private void showNotificationsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button allowButton = dialogView.findViewById(R.id.allowButton);
        Button dontAllowButton = dialogView.findViewById(R.id.dontAllowButton);

        View.OnClickListener listener = v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("GnGSecurityPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isNewUser", false);
            editor.apply();
            dialog.dismiss();
        };

        allowButton.setOnClickListener(listener);
        dontAllowButton.setOnClickListener(listener);

        dialog.show();
    }
}
