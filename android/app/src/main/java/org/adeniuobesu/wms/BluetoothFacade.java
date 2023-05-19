package org.adeniuobesu.wms;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothFacade {
    AppCompatActivity activity;
    BluetoothManager manager;
    BluetoothAdapter adapter;
    Set<BluetoothDevice> pairedDevices;

    BluetoothFacade(AppCompatActivity activity) {
        this.activity = activity;
        manager = activity.getSystemService(BluetoothManager.class);
        adapter = manager.getAdapter();
        if (adapter == null) {
            // Device doesn't support Bluetooth
            System.out.println("Device does not support Bluetooth");
        } else {
            // Device supports Bluetooth
            if(!adapter.isEnabled())
                enableBluetooth();
            if(adapter.isEnabled()) {
                // Bluetooth is enabled, query the paired devices.
                queryPairedDevices();
            }
        }
    }

    private void queryPairedDevices() {
        pairedDevices = adapter.getBondedDevices();
        for(BluetoothDevice device : pairedDevices){
            System.out.println("Name=" + device.getName()
                + ", address=" + device.getAddress() + "."
            );
        }
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }
}
