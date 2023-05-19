#include "Arduino.h";
#include <SoftwareSerial.h>
#include "Weather.h"

Weather weather;
const byte rxPin = 9;
const byte txPin = 8;
SoftwareSerial BTSerial(rxPin, txPin); // RX TX

const char START_TOKEN = '?';
const char END_TOKEN = ';';
const char DELIMIT_TOKEN = '&';
const int CHAR_TIMEOUT = 20;
bool waitingForStartToken = true;
String msgBuffer = "";

void setWeatherLimits(String);
String getNextNumber(String, int);

void setup() {
  // define pin modes for tx, rx:
  pinMode(rxPin, INPUT);
  pinMode(txPin, OUTPUT);
  BTSerial.begin(9600);
  Serial.begin(9600);
}

String messageBuffer = "";
String message = "";
char dataByte;

void loop() {
  // Handle bluetooth link
  char nextData;
  if(Serial.available()) {
    // check for start of message
    if(waitingForStartToken){
      do{
        nextData = Serial.read();
      } while(nextData != START_TOKEN && Serial.available());
      if(nextData == START_TOKEN){
        Serial.println("Message starts !");
        waitingForStartToken = false;
      }
    }

    // Read command
    if(!waitingForStartToken && Serial.available()){
      do{
        nextData = Serial.read();
        Serial.println(nextData);
        msgBuffer += nextData;
      } while((nextData != END_TOKEN) && Serial.available());

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
  }
  
}

void setWeatherLimits(String message){
  /*
  Coming Message should in be in the format: h=xxx&t=xxx
  */
  int textCursor = 0;
  String humidity_limit, temperature_celsius_limit;
  if(message.startsWith("h=")){
    // Correct starting message
    textCursor = 2;
    humidity_limit = getNextNumber(message, textCursor);

    textCursor += humidity_limit.length() + 1;
    message = message.substring(textCursor);
  }
  weather.set_humidity_limit(humidity_limit.toInt());
  if(message.startsWith("t=")){
    // Correct starting message
    textCursor = 2;
    temperature_celsius_limit = getNextNumber(message, textCursor);

    textCursor += temperature_celsius_limit.length() + 1;
    message = message.substring(textCursor);
  }
  weather.set_temperature_celsius_limit(temperature_celsius_limit.toInt());
}

String getNextNumber(String text, int textCursor){
  String number = "";
  while((text[textCursor] >= '0') && (text[textCursor] <= '9') && (textCursor < text.length())){
    number += text[textCursor++];
  }
  return number;
}
