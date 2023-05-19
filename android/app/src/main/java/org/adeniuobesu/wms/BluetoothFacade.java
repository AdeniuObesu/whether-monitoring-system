package org.adeniuobesu.wms;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothFacade {
    AppCompatActivity activity;
    BluetoothManager manager;
    BluetoothAdapter adapter;
    BluetoothDevice pairedDevice;
    BluetoothSocket socket;

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
                // Bluetooth is enabled
                setPairedDevice();
                if(pairedDevice != null){
                    try {
                        socket = pairedDevice.createRfcommSocketToServiceRecord(pairedDevice.getUuids()[1].getUuid());
                        if(socket != null) {
                            System.out.println("Yeah boy ! Got the socket.");
                        }
                    } catch(Exception e) {
                        System.out.println("Exception was raised : " + e.getMessage());
                    }
                }
            }
        }
    }

    private void listUUIDs() {
        if(pairedDevice != null ){
            ParcelUuid[] uuids= pairedDevice.getUuids();
            for ( ParcelUuid uuid: uuids) {
                System.out.println("UUID : " + uuid.getUuid());
            }
        } else {
            System.out.println("Please check whether the appropriate device is paired or not !");
        }
    }

    private void setPairedDevice() {
        Set<BluetoothDevice> pairedDevices;
        pairedDevices = adapter.getBondedDevices();
        for(BluetoothDevice device : pairedDevices){
            if(device.getName().equals(activity.getString(R.string.bluetooth_device_name)))
                pairedDevice = device;
        }
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }
}
