#include "Weather.h"

Weather::Weather(){
    humidity = 0;
    temperature = 0;
}
const int Weather::get_humidity() const{
    return this->humidity;
}
void Weather::set_humidity(int humidity_){
    humidity = humidity_;
}
const int Weather::get_temperature() const{
  return this->temperature;
}
void Weather::set_temperature(int temperature_){
    temperature = temperature_;
}