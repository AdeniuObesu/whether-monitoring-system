package org.adeniuobesu.wms.model;

import androidx.annotation.NonNull;

public class State {
    private int humidity;
    private int temperatureInCelsius;

    public int getHumidity() {
        return humidity;
    }

    public int getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setTemperatureInCelsius(int celsius) {
        temperatureInCelsius = celsius;
    }

    public int getTemperatureInFahrenheit() {
        return (temperatureInCelsius + 32);
    }

    @NonNull
    @Override
    public String toString() {
        return "State=[celsius= " + temperatureInCelsius
                + ", fahrenheit= " + getTemperatureInFahrenheit()
                + ", humidity= " + humidity
                +"]";
    }
}
