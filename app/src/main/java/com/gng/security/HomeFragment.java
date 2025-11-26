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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements DeviceInteractionListener {

    private RecyclerView devicesRecyclerView;
    private DeviceAdapter adapter;
    private List<String> deviceList;
    private CardView addDeviceCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deviceList = getDeviceList();

        if (deviceList.isEmpty()) {
            return inflater.inflate(R.layout.fragment_home_fresh, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_home, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (deviceList.isEmpty()) {
            addDeviceCard = view.findViewById(R.id.addDeviceCard);
            addDeviceCard.setOnClickListener(v -> showAddDeviceDialog());
        } else {
            devicesRecyclerView = view.findViewById(R.id.devices_recycler_view);
            devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new DeviceAdapter(deviceList, this);
            devicesRecyclerView.setAdapter(adapter);
        }
    }

    private void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_device, null);
        builder.setView(dialogView);

        EditText deviceCodeInput = dialogView.findViewById(R.id.deviceCodeInput);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(v -> {
            String deviceCode = deviceCodeInput.getText().toString();
            if (!deviceCode.isEmpty()) {
                showRenameDeviceDialog(deviceCode, dialog);
            } else {
                Toast.makeText(getContext(), "Please enter a device code", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showRenameDeviceDialog(String deviceCode, AlertDialog addDeviceDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rename Device");

        EditText renameInput = new EditText(getContext());
        renameInput.setHint("Enter new device name");
        builder.setView(renameInput);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = renameInput.getText().toString();
            if (!newName.isEmpty()) {
                deviceList.add(newName);
                saveDeviceList(deviceList);
                addDeviceDialog.dismiss();
                refreshFragment();
            } else {
                Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private List<String> getDeviceList() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("deviceList", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private void saveDeviceList(List<String> list) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GnGSecurityPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(list);
        editor.putString("deviceList", json);
        editor.apply();
    }

    @Override
    public void onDeviceRemoved() {
        refreshFragment();
    }

    private void refreshFragment() {
        getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
