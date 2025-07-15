# Weather Monitoring System 🌦️

A smart system that monitors weather conditions (temperature and humidity) and automatically controls a garden cover based on user-defined thresholds. The project consists of an Arduino component and an Android application communicating via Bluetooth.

## Project Overview

This system:
- Continuously monitors temperature and humidity using a DHT11 sensor
- Communicates with an Android app via Bluetooth (HC-05 module)
- Automatically opens/closes a garden cover (simulated with a stepper motor) based on weather conditions
- Allows users to set custom temperature and humidity thresholds via the Android app

## Hardware Components 🛠️

- Arduino Uno R3
- HC-05 Bluetooth Module
- DHT11 Temperature & Humidity Sensor
- Stepper Motor (28BYJ-48) + ULN2003 Driver Board
- Jumper Wires
- Breadboard
- Power Supply

## Software Components 💻

- Arduino IDE (for Arduino code)
- Android Studio (for mobile app)
- C++ (Arduino)
- Java (Android)

## Setup Instructions

### Arduino Setup

1. Connect components as per circuit diagram
2. Upload `WeatherMonitoring.ino` to Arduino
3. Ensure Bluetooth module is properly paired (default password: 1234)

### Android App Setup

1. Clone this repository
2. Open project in Android Studio
3. Build and install on your Android device
4. Ensure Bluetooth is enabled on your device

## How It Works ⚙️

1. The DHT11 sensor continuously reads temperature and humidity
2. Arduino sends this data to the Android app via Bluetooth
3. The app displays current weather conditions
4. Users can set thresholds for temperature and humidity
5. When thresholds are exceeded:
   - The Arduino activates the stepper motor to close the garden cover
   - When conditions improve, it opens the cover

## Features ✨

- Real-time weather monitoring
- Threshold-based automatic cover control
- Bluetooth communication
- Simple Android interface
- Visual indicators for garden cover status

## Code Structure
```bash
weather-monitoring-system/
├── arduino/ # Arduino code
│ ├── WeatherMonitoring.ino # Main Arduino sketch
│ ├── Weather.h # Weather class header
│ └── Weather.cpp # Weather class implementation
├── android/ # Android app
│ ├── app/ # Main application code
│ └── ... # Other Android project files
└── README.md # This file
```


## Usage Instructions

1. Power on the Arduino system
2. Open the Android app
3. Connect to the HC-05 Bluetooth device
4. Set your desired temperature and humidity thresholds
5. The system will automatically manage the garden cover

## Troubleshooting 🛠️

- **Bluetooth connection issues**: Ensure devices are properly paired
- **Sensor reading errors**: Check DHT11 connections
- **Motor not moving**: Verify stepper motor wiring and power

## Future Enhancements 🚀

- Add WiFi/cloud connectivity for remote monitoring
- Implement data logging
- Add multiple sensor support
- Improve UI/UX of Android app

## Author 📄👨‍💻

This project is done with love <3 by [@Moukhafi-Anass](https://github.com/adeniuobesu)

## Acknowledgments

- Thanks to Arduino and Android developer communities
- DHT11 library authors
- Bluetooth module documentation providers
