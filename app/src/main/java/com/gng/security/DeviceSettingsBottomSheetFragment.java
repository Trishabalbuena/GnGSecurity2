package com.gng.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeviceSettingsBottomSheetFragment extends BottomSheetDialogFragment {

    private String deviceName;
    private DeviceInteractionListener listener;

    public static DeviceSettingsBottomSheetFragment newInstance(String deviceName) {
        DeviceSettingsBottomSheetFragment fragment = new DeviceSettingsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("deviceName", deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(DeviceInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceName = getArguments().getString("deviceName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_settings_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button disconnectButton = view.findViewById(R.id.disconnect_button);
        disconnectButton.setOnClickListener(v -> {
            removeDevice(deviceName);
            Toast.makeText(getContext(), deviceName + " disconnected", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onDeviceRemoved();
            }
            dismiss();
        });
    }

    private void removeDevice(String deviceName) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
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
