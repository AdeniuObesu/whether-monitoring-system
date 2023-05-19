package org.adeniuobesu.wms.model;

import java.io.Serializable;

public class State implements Serializable {
    private static final long serialVersionUID =1l;

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

    @Override
    public String toString() {
        return "State=[celsius= " + temperatureInCelsius
                + ", fahrenheit= " + getTemperatureInFahrenheit()
                + ", humidity= " + humidity
                +"]";
    }
}
