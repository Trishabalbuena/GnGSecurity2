package com.gng.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchMaterial enableNotificationsSwitch = view.findViewById(R.id.enable_notifications_switch);
        TextView defaultAlarmSound = view.findViewById(R.id.default_alarm_sound);
        TextView pinCodeSettings = view.findViewById(R.id.pin_code_settings);

        enableNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch state change
        });

        defaultAlarmSound.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Default Alarm Sound clicked", Toast.LENGTH_SHORT).show();
        });

        pinCodeSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "PIN Code Settings clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
