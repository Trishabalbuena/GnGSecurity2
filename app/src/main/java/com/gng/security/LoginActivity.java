package com.gng.security; // Replace with your actual package name

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn, registerBtn;
    private ImageButton googleLogin, facebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        googleLogin = findViewById(R.id.googleLogin);
        facebookLogin = findViewById(R.id.facebookLogin);
    }

    private void setupClickListeners() {
        loginBtn.setOnClickListener(v -> showLoginDialog());
        registerBtn.setOnClickListener(v -> showRegisterDialog());

        googleLogin.setOnClickListener(v -> signInWithGoogle());
        facebookLogin.setOnClickListener(v -> signInWithFacebook());
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login to GnG Security");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);
        EditText passwordInput = dialogView.findViewById(R.id.passwordInput);

        builder.setView(dialogView);
        builder.setPositiveButton("Login", (dialog, which) -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            performLogin(email, password);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Account");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null);
        EditText fullNameInput = dialogView.findViewById(R.id.fullNameInput);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);
        EditText passwordInput = dialogView.findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = dialogView.findViewById(R.id.confirmPasswordInput);

        builder.setView(dialogView);
        builder.setPositiveButton("Register", (dialog, which) -> {
            String fullName = fullNameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            performRegistration(fullName, email, password, confirmPassword);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void performLogin(String email, String password) {
        // TODO: Implement actual login logic
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the login state
        SharedPreferences sharedPreferences = getSharedPreferences("GnGSecurityPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.commit(); // Use commit() for an immediate, synchronous save

        // Proceed to main activity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void performRegistration(String fullName, String email, String password, String confirmPassword) {
        // TODO: Implement actual registration logic
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the login state
        SharedPreferences sharedPreferences = getSharedPreferences("GnGSecurityPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.commit(); // Use commit() for an immediate, synchronous save

        // Proceed to main activity
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void signInWithGoogle() {
        // TODO: Implement Google Sign-In
        Toast.makeText(this, "Google Sign-In will be implemented", Toast.LENGTH_SHORT).show();
    }

    private void signInWithFacebook() {
        // TODO: Implement Facebook Sign-In
        Toast.makeText(this, "Facebook Sign-In will be implemented", Toast.LENGTH_SHORT).show();
    }
}
