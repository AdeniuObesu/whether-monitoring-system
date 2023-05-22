package org.adeniuobesu.wms;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.adeniuobesu.wms.model.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {
    private TextView fahrenheitView, celsiusView, humidityView;
    private EditText celsiusField, humidityField;
    private Button submitBtn;
    // MyBluetooth is an inner class that handles bluetooth connection,
    // it is a thread that goes in parallel with the UI_Thread, and it gives us
    // the possibility to add methods to handle communication between the two devices
    // involved in the connection.
    private MyBluetooth myBluetooth;

    // Weather limit defined by the user and weather object received via bluetooth
    private Weather weather, weatherLimit;
    private void changeWeather() {
        humidityView.setText(weather.getHumidity() + "%");
        celsiusView.setText(weather.getTemperatureInCelsius() + " °C");
        fahrenheitView.setText(weather.getTemperatureInFahrenheit() + " °F");
        // TODO : if the temperature or humidity exceeds the limit, show it in the activity
        if(weather.getHumidity() >= weatherLimit.getHumidity()
            || weather.getTemperatureInCelsius() >= weatherLimit.getTemperatureInCelsius()){

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        referenceFields();

        submitBtn.setOnClickListener(new SubmitBtnListener());

        // Define the handler
        my_handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case STATUS:
                        System.out.println((String)(msg.obj));
                        break;
                    case 0:
                        changeWeather();
                        break;
                }
            }
        };

        // Identify bluetooth module in the device
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            System.out.println("Pas d'interface Bluetooth");
        } else {
            // Device supports Bluetooth
            if(!adapter.isEnabled())
                enableBluetooth();
            while (!adapter.isEnabled()) ; // Wait for bluetooth to start

            // Create weather objects
            weather = new Weather();
            weatherLimit = new Weather(26, 35);

            // Make bluetooth link
            myBluetooth = new MyBluetooth();
            myBluetooth.start();
        }

    }
    private void checkBluetooth() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, R.string.bluetooth_supported, Toast.LENGTH_LONG).show();
            finish();
        }
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_supported, Toast.LENGTH_LONG).show();
            finish();
        }

    }
    private void referenceFields() {
        fahrenheitView = findViewById(R.id.main_textview_fahrenheit);
        celsiusView = findViewById(R.id.main_textview_celsuis);
        humidityView = findViewById(R.id.main_textview_humidity);

        humidityField = findViewById(R.id.main_edittext_humidity);
        celsiusField = findViewById(R.id.main_edittext_celsius);

        submitBtn = findViewById(R.id.main_button_submit);
    }

    private class SubmitBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int humidity, temperatureInCelsius;
            try {
                humidity = Integer.parseInt(humidityField.getText().toString());
                temperatureInCelsius = Integer.parseInt(celsiusField.getText().toString());
                weatherLimit.setHumidity(humidity);
                weatherLimit.setTemperatureInCelsius(temperatureInCelsius);

                // Send the new weather limit via bluetooth
                sendData(weatherLimit.toString());

            } catch(Exception e){
                System.out.println("Error : " + e.getMessage());
            }
        }
    }

    private void sendData(String data){
        myBluetooth.writeBytes(("?" + weatherLimit.toString() + ";").getBytes(StandardCharsets.UTF_8));
    }

    // To use Bluetooth
    private BluetoothAdapter adapter;
    private BluetoothDevice HC05;
    private BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private Handler my_handler;
    private final static int STATUS = 1;

    private OutputStream out = null;
    private InputStream in = null;
    private static final String mac_address = "98:D3:21:F7:E7:74";

    private class MyBluetooth extends Thread {
        // The store for the stream
        private byte[] buffer;
        public void run() {
            boolean SOCKET_OK, CONX_OK, OUTS_OK, INPS_OK;
            // Create the bluetooth object HC05
            HC05 = adapter.getRemoteDevice(mac_address);
            // Create a socket (pipeline) to communicate with HC05 module
            SOCKET_OK = true;
            try {
                socket = HC05.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                SOCKET_OK = false;
            }
            if (SOCKET_OK) {
                // Connect the socket
                CONX_OK = true;
                try {
                    socket.connect();
                } catch (IOException e) {
                    CONX_OK = false;
                    e.printStackTrace();
                }
                if (CONX_OK) {
                    OUTS_OK = true;
                    try {
                        out = socket.getOutputStream();
                    } catch (Exception e) {
                        my_handler.obtainMessage(STATUS, -1, -1, "Failed to create OUTPUT stream !").sendToTarget();
                        OUTS_OK = false;
                    }
                    INPS_OK = true;
                    try {
                        in = socket.getInputStream();
                    } catch (Exception e) {
                        my_handler.obtainMessage(STATUS, -1, -1, "Failed to create INTPUT stream !").sendToTarget();
                        INPS_OK = false;
                    }
                    if (OUTS_OK && INPS_OK){
                        int numBytes; // bytes returned from read()
                        my_handler.obtainMessage(STATUS, -1, -1, "Connected").sendToTarget();
                        buffer = new byte[7];
                        // Keep listening to the InputStream until an exception occurs.
                        while (true) {
                            try {
                                numBytes = readBytes(buffer);
                                readWeather(buffer);
                                Message readMsg = my_handler.obtainMessage(
                                        0, numBytes, -1,
                                        buffer);
                                readMsg.sendToTarget();
                            } catch (IOException e) {
                                my_handler.obtainMessage(STATUS, -1, -1,e.getMessage()).sendToTarget();
                                break;
                            }
                        }
                    }
                } else {
                    my_handler.obtainMessage(STATUS, -1, -1, "Connection failed !").sendToTarget();
                }
            } else {
                my_handler.obtainMessage(STATUS, -1, -1, "Failed to create COMM socket !").sendToTarget();
            }
        }
        private void readWeather(byte[] buffer) {
            if((int) buffer[0] != (int)'~'){
                // Tilda represents 126: a value that exceeds 100%
                weather.setHumidity((int)buffer[0]);
                weather.setTemperatureInCelsius((int)buffer[1]);
                System.out.println(weather.toString());
            }
        }
        void writeBytes(byte[] b) {
            try {
                out.write(b);
            } catch (IOException e) {
                my_handler.obtainMessage(STATUS, -1, -1,"Error in writeBytes(byte[])").sendToTarget();
            }
        }
        int available() {
            int b = 0;
            try {
                b = in.available();
                // TODO : Delete the following line
                System.out.println(">> Inside available() : " + b);
                return b;
            } catch (Exception e) {
                my_handler.obtainMessage(STATUS, -1, -1,"Error in available").sendToTarget();
                return -1;
            }
        }
        int readByte() {
            int b = 0;
            try {
                b = in.read();
                // TODO : Delete the following line
                System.out.println(">> Inside readByte() : " + b);
                return b;
            } catch (IOException e) {
                my_handler.obtainMessage(STATUS, -1, -1,"Erreur dans readbyte").sendToTarget();
                return -1;
            }
        }
        int readBytes(byte[] inputBuffer) throws IOException{
            int b = 0;
            try {
                b = in.read(inputBuffer);
                return b;
            } catch (IOException e) {
                throw new IOException("Error in reading !");
            }
        }

        void disconnect() {
            try {
                socket.close();
            } catch (Exception e) {
                my_handler.obtainMessage(STATUS, -1, -1, "Failed to disconnect !").sendToTarget();
            }
        }
    }


    private void listUUIDs() {
        if(HC05 != null ){
            ParcelUuid[] uuids= HC05.getUuids();
            for ( ParcelUuid uuid: uuids) {
                System.out.println("UUID : " + uuid.getUuid());
            }
        } else {
            System.out.println("Please check whether the appropriate device is paired or not !");
        }
    }
    private void setPairedDevice() {
//        Set<BluetoothDevice> pairedDevices;
//        pairedDevices = adapter.getBondedDevices();
//        for(BluetoothDevice device : pairedDevices){
//            if(device.getName().equals(this.getString(R.string.bluetooth_device_name)))
//                pairedDevice = device;
//        }
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.startActivityForResult(enableBtIntent, 1);
    }
}