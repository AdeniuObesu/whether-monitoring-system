#include "Weather.h"

Weather::Weather() : Weather(0, 0) {
}
Weather::Weather(int humidity_, int temperature_){
    humidity = humidity_;
    temperature = temperature_;
}
const int Weather::get_humidity() const{
    return this->humidity;
}
void Weather::set_humidity(int humidity_){
    if(humidity_ > 0)
      humidity = humidity_;
}
const int Weather::get_temperature() const{
  return this->temperature;
}
void Weather::set_temperature(int temperature_){
  if(temperature_ > 0)
    temperature = temperature_;
}