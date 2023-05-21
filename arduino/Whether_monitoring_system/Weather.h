#pragma once
class Weather {
    private :
        int humidity;
        int temperature;
    public :
        Weather();
        Weather(int, int);
        const int get_humidity() const;
        const int get_temperature() const;
        void set_humidity(int);
        void set_temperature(int);
};