package org.adeniuobesu.wms;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
@Deprecated
public class BluetoothService {
//    BluetoothService(AppCompatActivity activity) {
//        this.activity = activity;
//        manager = activity.getSystemService(BluetoothManager.class);
//        adapter = manager.getAdapter();
//        if (adapter == null) {
//            // Device doesn't support Bluetooth
//            System.out.println("Device does not support Bluetooth");
//        } else {
//            // Device supports Bluetooth
//            if(!adapter.isEnabled())
//                enableBluetooth();
//            if(adapter.isEnabled()) {
//                // Bluetooth is enabled
//                setPairedDevice();
//                if(pairedDevice != null){
//                    try {
//                        MY_UUID = pairedDevice.getUuids()[1].getUuid();
//                        socket = pairedDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                        if(socket != null) {
//                            System.out.println("Yeah boy ! Got the socket.");
//                        }
//                    } catch(Exception e) {
//                        System.out.println("Exception was raised : " + e.getMessage());
//                    }
//                }
//            }
//        }
//    }
}
