#include "Weather.h"

Weather::Weather(){
    humidity = 0;
    temperature_celsius = 0;
    humidity_limit = 45;
    temperature_celsius_limit = 35;
}

const int Weather::get_humidity() const{
    return this->humidity;
}
void Weather::set_humidity(int humidity_){
    humidity = humidity_;
}
const int Weather::get_temperature_celsius() const{
    return this->temperature_celsius;
}
void Weather::set_temperature_celsius(int temperature_celsius_){
    temperature_celsius = temperature_celsius_;
}
int Weather::get_humidity_limit() const {
  return this->humidity_limit;
}
void Weather::set_humidity_limit(int humidity_limit_){
    humidity_limit = humidity_limit_;
}
int Weather::get_temperature_celsius_limit() const {
  return this->temperature_celsius_limit;
}
void Weather::set_temperature_celsius_limit(int temperature_celsius_limit_){
    temperature_celsius_limit = temperature_celsius_limit_;
}