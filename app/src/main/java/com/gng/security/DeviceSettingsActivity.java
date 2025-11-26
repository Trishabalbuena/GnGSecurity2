package com.gng.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeviceSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        Button disconnectButton = findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(v -> {
            String deviceNameToRemove = getIntent().getStringExtra("deviceName");
            if (deviceNameToRemove != null) {
                removeDevice(deviceNameToRemove);
                Toast.makeText(this, deviceNameToRemove + " disconnected", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void removeDevice(String deviceName) {
        SharedPreferences sharedPreferences = getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("deviceList", null);
        if (json == null) {
            return;
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> deviceList = new Gson().fromJson(json, type);

        deviceList.remove(deviceName);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newJson = new Gson().toJson(deviceList);
        editor.putString("deviceList", newJson);
        editor.apply();
    }
}
