#pragma once
class Weather {
    private :
        int humidity, humidity_limit;
        int temperature_celsius, temperature_celsius_limit;
    public :
        Weather();
        const int get_humidity() const;
        const int get_temperature_celsius() const;
        void set_humidity(int);
        void set_temperature_celsius(int);
        int get_humidity_limit() const;
        int get_temperature_celsius_limit() const;
        void set_humidity_limit(int);
        void set_temperature_celsius_limit(int);
};