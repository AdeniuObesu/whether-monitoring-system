#include "Arduino.h";
#include <SoftwareSerial.h>
#include <DHT.h>
#include <Stepper.h>
#include "Weather.h"
# define PAUSE 3000
#define Type DHT11
int sensePin = 2;
DHT HT(sensePin, Type);
int setTime = 500;

int stepsPerRevolution = 2048;
Stepper myStepper(stepsPerRevolution, 7, 6, 5, 3);
int motSpeed = 10;
boolean closed = false;


Weather weather; // The current weather that the DHT11 reads.
Weather weatherLimit(55, 25); // The limit of weather received via Bluetooth.
const byte rxPin = 9;
const byte txPin = 8;
SoftwareSerial BTSerial(rxPin, txPin); // RX TX

const char START_TOKEN = '?';
const char END_TOKEN = ';';
const char DELIMIT_TOKEN = '&';
const int CHAR_TIMEOUT = 40;
bool waitingForStartToken = true;
String msgBuffer = "";

void setWeatherLimits(String);
String getNextNumber(String, int);
void compare_weather();
void debug();

void setup() {
  // define pin modes for tx, rx:
  pinMode(rxPin, INPUT);
  pinMode(txPin, OUTPUT);
  BTSerial.begin(9600);
  Serial.begin(9600);
  HT.begin();
  myStepper.setSpeed(motSpeed);
  delay(setTime);
}

String messageBuffer = "";
String message = "";
char dataByte;

void loop() {
  // Handle bluetooth link
  char nextData;
  if(BTSerial.available() > 0) {
    // check for start of message
    if(waitingForStartToken){
      do{
        nextData = BTSerial.read();
      } while(nextData != START_TOKEN && BTSerial.available());
      if(nextData == START_TOKEN){
        Serial.println("Message starts !");
        waitingForStartToken = false;
      }
    }

    // Read command
    if(!waitingForStartToken && BTSerial.available()){
      do{
        nextData = BTSerial.read();
        Serial.println(nextData);
        msgBuffer += nextData;
      } while((nextData != END_TOKEN) && BTSerial.available());

      // Check if message is complete
      if(nextData == END_TOKEN){
        // Remove last character
        msgBuffer = msgBuffer.substring(0, msgBuffer.length() - 1);
        Serial.println("Message complete - " + msgBuffer);
        setWeatherLimits(msgBuffer);
        msgBuffer = "";
        waitingForStartToken = true;
      }

      // Check for char timeout
      if(msgBuffer.length() > CHAR_TIMEOUT) {
        Serial.println("Message data timeout - " + msgBuffer);
        msgBuffer = "";
        waitingForStartToken = true;
      }
    }
  } else {
    delay(PAUSE);
    // Send the current weather to the device.
    BTSerial.write('~');
    weather.set_humidity(HT.readHumidity());
    weather.set_temperature(HT.readTemperature());
    BTSerial.write(weather.get_humidity());
    BTSerial.write(weather.get_temperature());
    compare_weather();
  }
  
}
void debug() {
  Serial.print("H=");
  Serial.print(weather.get_humidity());
  Serial.print(", C=");
  Serial.println(weather.get_temperature());
  Serial.print("Limits H=");
  Serial.print(weatherLimit.get_humidity());
  Serial.print(", C=");
  Serial.println(weatherLimit.get_temperature());
  Serial.print("And The Door is ");
  if(closed) {
    Serial.println("closed");
  } else {
    Serial.println("open");
  }
}
void compare_weather() {
  debug();
  if(
    ( weather.get_temperature() >= weatherLimit.get_temperature()
    || weather.get_humidity() >= weatherLimit.get_humidity() )
    && !closed){
      close_door();
      return;
  }
  if(weather.get_temperature() < weatherLimit.get_temperature()
    && weather.get_humidity() < weatherLimit.get_humidity()
    && closed){
      open_door();
      return;
  }
}

void close_door() {
  Serial.println("The door is closing...");
  closed = true;
  myStepper.step(stepsPerRevolution);
  delay(setTime);
}

void open_door() {
  Serial.println("The door is opening...");
  closed = false;
  myStepper.step(-stepsPerRevolution);
  delay(setTime);
}

void setWeatherLimits(String message){
  /*
  Coming Message should in be in the format: Weather{humidity=xxx,temperature=xxx}
  */
  int textCursor = 0;
  String humidity_limit, temperature_limit;
  if(message.startsWith("Weather{humidity=")){
    // Correct starting message
    textCursor = 17;
    humidity_limit = getNextNumber(message, textCursor);
    Serial.println("Limit for Humidity : " + humidity_limit);

    textCursor += humidity_limit.length() + 1; // 1 to skip comma.
    message = message.substring(textCursor);
  }
  weatherLimit.set_humidity(humidity_limit.toInt());
  if(message.startsWith("temperature=")){
    // Correct starting message
    textCursor = 12;
    temperature_limit = getNextNumber(message, textCursor);
    Serial.println("Limit for Temperature : " + temperature_limit);
  }
  weatherLimit.set_temperature(temperature_limit.toInt());
}

String getNextNumber(String text, int textCursor){
  String number = "";
  while((text[textCursor] >= '0') && (text[textCursor] <= '9') && (textCursor < text.length()))
    number += text[textCursor++];
  return number;
}
