package com.gng.security;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements DeviceInteractionListener {

    private RecyclerView devicesRecyclerView;
    private DeviceAdapter adapter;
    private List<String> deviceList;
    private CardView addDeviceCard;
    private FloatingActionButton addDeviceFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Always inflate the single, unified layout
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find all views
        addDeviceCard = view.findViewById(R.id.addDeviceCard);
        devicesRecyclerView = view.findViewById(R.id.devices_recycler_view);
        addDeviceFab = view.findViewById(R.id.add_device_fab);

        // Initialize list and adapter
        deviceList = getDeviceList();
        adapter = new DeviceAdapter(deviceList, this);

        Context context = getContext();
        if (context != null) {
            devicesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            devicesRecyclerView.setAdapter(adapter);
        }

        // Set click listeners
        if (addDeviceCard != null) {
            addDeviceCard.setOnClickListener(v -> showAddDeviceDialog());
        }
        if (addDeviceFab != null) {
            addDeviceFab.setOnClickListener(v -> showAddDeviceDialog());
        }

        // Set initial visibility
        updateUIVisibility();
    }

    private void updateUIVisibility() {
        if (deviceList == null) return;

        boolean isEmpty = deviceList.isEmpty();
        if (addDeviceCard != null) {
            addDeviceCard.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (devicesRecyclerView != null) {
            devicesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
        if (addDeviceFab != null) {
            addDeviceFab.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void showAddDeviceDialog() {
        final Context context = getContext();
        if (context == null || !isAdded()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_device, null);
        builder.setView(dialogView);

        EditText deviceCodeInput = dialogView.findViewById(R.id.deviceCodeInput);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(v -> {
            String deviceCode = deviceCodeInput.getText().toString();
            if (!deviceCode.isEmpty()) {
                dialog.dismiss();
                showRenameDeviceDialog(deviceCode);
            } else {
                Toast.makeText(context, "Please enter a device code", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showRenameDeviceDialog(String deviceCode) {
        final Context context = getContext();
        if (context == null || !isAdded()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rename Device");

        EditText renameInput = new EditText(context);
        renameInput.setHint("Enter new device name");
        builder.setView(renameInput);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = renameInput.getText().toString();
            if (!newName.isEmpty()) {
                if (deviceList == null) {
                    deviceList = new ArrayList<>();
                }
                deviceList.add(newName);
                saveDeviceList(deviceList);
                if (adapter != null) {
                    adapter.notifyItemInserted(deviceList.size() - 1);
                }
                updateUIVisibility();
                dialog.dismiss();
                Toast.makeText(context, "Device added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(false);
        builder.show();
    }


    private List<String> getDeviceList() {
        Context context = getContext();
        if (context == null) return new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("deviceList", null);
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> list = new Gson().fromJson(json, type);
            if (list == null) {
                throw new JsonSyntaxException("Parsed list is null");
            }
            list.removeAll(Collections.singleton(null)); // Clean up any null entries from corruption
            return list;
        } catch (Exception e) {
            // Data is corrupted. Clear it and start fresh.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("deviceList");
            editor.apply();
            return new ArrayList<>();
        }
    }

    private void saveDeviceList(List<String> list) {
        Context context = getContext();
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(list);
        editor.putString("deviceList", json);
        editor.apply();
    }

    @Override
    public void onDeviceRemoved(String deviceName) {
        if (deviceList == null || !isAdded()) return;
        int index = deviceList.indexOf(deviceName);
        if (index != -1) {
            deviceList.remove(index);
            saveDeviceList(deviceList);
            if (adapter != null) {
                adapter.notifyItemRemoved(index);
            }
            updateUIVisibility();
        }
    }
}
