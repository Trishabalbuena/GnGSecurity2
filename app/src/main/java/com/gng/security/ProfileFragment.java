package com.gng.security;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView nameTextView, emailTextView;
    private RelativeLayout changePasswordButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (getActivity() != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        }

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.name);
        emailTextView = view.findViewById(R.id.email);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());

        Button deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountConfirmationDialog());

        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());

            String displayName = user.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                nameTextView.setText(displayName);
            } else if (user.getEmail() != null && user.getEmail().contains("@")) {
                nameTextView.setText(user.getEmail().split("@")[0]);
            } else {
                nameTextView.setText("User");
            }

            // Check provider and set button visibility
            boolean isPasswordProvider = false;
            for (UserInfo userInfo : user.getProviderData()) {
                if (EmailAuthProvider.PROVIDER_ID.equals(userInfo.getProviderId())) {
                    isPasswordProvider = true;
                    break;
                }
            }
            changePasswordButton.setVisibility(isPasswordProvider ? View.VISIBLE : View.GONE);
        }
    }

    private void showChangePasswordDialog(){
        if(getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText oldPasswordInput = dialogView.findViewById(R.id.old_password_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        EditText confirmNewPasswordInput = dialogView.findViewById(R.id.confirm_new_password_input);
        Button saveButton = dialogView.findViewById(R.id.save_new_password_button);

        saveButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmNewPasswordInput.getText().toString();

            if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!newPassword.equals(confirmPassword)){
                Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            reauthenticateAndUpdatePassword(oldPassword, newPassword, dialog);
        });

        dialog.show();
    }

    private void reauthenticateAndUpdatePassword(String oldPassword, String newPassword, AlertDialog dialog) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to update password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Authentication failed. Please check your old password.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void logoutUser() {
        if (getActivity() == null) return;

        mAuth.signOut();

        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                Log.d(TAG, "Google Sign-out successful.");
            });
        }

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void showDeleteAccountConfirmationDialog() {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No user found to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Here you would also delete user data from your database (e.g. device list and pincodes)

        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");
                        Toast.makeText(getContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        logoutUser(); // Logout after successful deletion
                    } else {
                        Log.w(TAG, "Error deleting account.", task.getException());
                        Toast.makeText(getContext(), "Failed to delete account. Please try logging in again before deleting.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
