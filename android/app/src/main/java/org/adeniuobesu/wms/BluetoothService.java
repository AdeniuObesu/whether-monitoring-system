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
import org.adeniuobesu.wms.enums.MessageConstants;

@SuppressLint("MissingPermission")
public class BluetoothService {
    private AppCompatActivity activity;
    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private BluetoothDevice pairedDevice;
    private BluetoothSocket socket;
    private UUID MY_UUID;
    private Handler handler;

    BluetoothService(AppCompatActivity activity) {
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
                        MY_UUID = pairedDevice.getUuids()[1].getUuid();
                        Thread connectThread = new ConnectedThread();
                        if(socket != null) {
                            System.out.println("Yeah boy ! Got the socket.");
                            connectThread.start();
                        }
                    } catch(Exception e) {
                        System.out.println("Exception was raised : " + e.getMessage());
                    }
                }
            }
        }
    }
    private class ConnectThread extends Thread {

        public ConnectThread() {
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                socket = pairedDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                System.out.println("Socket#create() method failed !");
            }
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            adapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();
                System.out.println("Connection has been established !");
            } catch (Exception connectException) {
                // Unable to connect; close the socket and return.
                try {
                    socket.close();
                } catch (Exception closeException) {
                    System.out.println("Could not close the client socket :" + closeException.getMessage());
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket();
        }

        private void manageMyConnectedSocket() {
            Thread connectedThread = new ConnectedThread();
            connectedThread.start();
            System.err.println("Yeah boy !");
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Could not close the client socket : " + e.getMessage());
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream in;
        private final OutputStream out;
        private byte[] buffer; // mmBuffer store for the stream
        public ConnectedThread() {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (Exception e) {
                System.out.println("Error occurred when creating input stream");
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (Exception e) {
                System.out.println("Error occurred when creating output stream");
            }

            in = tmpIn;
            out = tmpOut;
        }
        @Override
        public void run() {
            buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = in.read(buffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ.getValue(), numBytes, -1,
                            buffer);
                    readMsg.sendToTarget();
                } catch (Exception e) {
                    System.out.println("Input stream was disconnected : " + e.getMessage());
                    break;
                }
            }
        }
        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                out.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE.getValue(), -1, -1, buffer);
                writtenMsg.sendToTarget();
            } catch (Exception e) {
                System.out.println("Error occurred when sending data : " + e.getMessage());
                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST.getValue());
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }
        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Could not close the connect socket : " + e.getMessage());
                e.printStackTrace();
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
