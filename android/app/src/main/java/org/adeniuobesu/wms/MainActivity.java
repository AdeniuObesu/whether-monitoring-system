package org.adeniuobesu.wms;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView fahrenheitView, celsiusView, humidityView;
    private EditText celsiusField, humidityField;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        referenceFields();

        submitBtn.setOnClickListener(new SubmitBtnListener());

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
            int temperatureInCelsius, humidity;
            try {
                temperatureInCelsius = Integer.parseInt(celsiusField.getText().toString());
                humidity = Integer.parseInt(humidityField.getText().toString());
                System.out.println("Humidity " + humidity + " Temperature : " + temperatureInCelsius);
            } catch(Exception e){
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }
}