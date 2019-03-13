/*
   Copyright (c) 2015, Majenko Technologies
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification,
   are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice, this
     list of conditions and the following disclaimer.

 * * Redistributions in binary form must reproduce the above copyright notice, this
     list of conditions and the following disclaimer in the documentation and/or
     other materials provided with the distribution.

 * * Neither the name of Majenko Technologies nor the names of its
     contributors may be used to endorse or promote products derived from
     this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
   ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
   ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/* Create a WiFi access point and provide a web serverHS on it. */

#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>


// To DO:
// Use EEPROM Library to save password of wifi after device dont have power  https://www.arduino.cc/en/Reference/EEPROM
//
// Connect the device to firebase to integrate out side of wifi and to get status



#ifndef APSSID
#define APSSID "ESPap"
#define APPSK  "thereisnospoon"
#endif


// Hot spot
char* ssidHS = APSSID;    // Hot spot id
char* passwordHS = APPSK; // Hot spot password
WiFiServer serverHS(80);
bool checkWifStatus = true;  // to chack if wifi can be reconnected


// Home wifi
String ssidWF = "";           // Home wifi id
String passwordWF = "";     // Home wifi password
int ledPin = D5;    // Relay pin
int pinState = LOW;
WiFiServer serverWF(80);


// Jeneral
bool stopHotSpot = false; // hotSpot or wifi    true = wifi
int addrCounter = 0;   // EEPROM address

WiFiClient client;    // status of client
/* Just a little test message.  Go to http://192.168.4.1 in a web browser
   connected to this access point to see it.
*/


// ADD Show ip of intenet when start
// After 8 minutes disconnect



//  ------------------------setup---------------------------------------------------
void setup() {
  // More
  Serial.begin(115200);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  EEPROM.begin(512);  //Initialize EEPROM (Storege)
  Serial.println("good morning");
  byte mac[6];                     // the MAC address of your Wifi shield
  WiFi.macAddress(mac);
  Serial.print("MAC: ");    // show macc address
  Serial.print(mac[0], HEX);
  Serial.print(":");
  Serial.print(mac[1], HEX);
  Serial.print(":");
  Serial.print(mac[2], HEX);
  Serial.print(":");
  Serial.print(mac[3], HEX);
  Serial.print(":");
  Serial.print(mac[4], HEX);
  Serial.print(":");
  Serial.println(mac[5], HEX);


  // HotSpot setup
  WiFi.softAP(ssidHS, passwordHS);    // Creating the hotspot
  serverHS.begin();     // Starting the server
  Serial.println("Creating a hotspot");

  // Wifi setup

  // Pins setup

  digitalWrite(ledPin, !pinState);
  pinMode(ledPin, OUTPUT);


    
}


//  ------------------------EEROM functions-----------------------------------------------

void writingStringToEEROM(String saveS) {   // fuction that take string and saving it on the device storege (EEROM) adding & to the end of the string
  saveS = saveS + "&";
  int tempAddres = addrCounter;

  for (int i = 0; i < saveS.length(); i++) //loop upto string lenght www.length() returns length of string
  {
    tempAddres = addrCounter + i;
    EEPROM.write(0x0F + tempAddres, saveS[i]); //Write one by one with starting address of 0x0F
  }
  addrCounter = tempAddres + 1;
  EEPROM.commit();    //Store data to EEPROM
}

String getingStringFromEEROM() { // Getting all the String from the storege (EEROM)
  String eeromString = "";
  //  int addrWifiName = 0;   // EEPROM address for the wifi name
  //  int addrWifiPass = 1;   // EEPROM address for the wifi password

  for (int i = 0; i < EEPROM.length(); i++)
  {
    eeromString = eeromString + char(EEPROM.read(0x0F + i)); //Read one by one with starting address of 0x0F
  }

  return eeromString;
}

void eeromToVariabels() { // Getting all the eerom store and transfer it to local variables
  String fromEEROM = getingStringFromEEROM();

  Serial.println("Am I here");
  if (fromEEROM == "") {
    return;
  }

  String tempFromEEROM = fromEEROM;
  while (fromEEROM.indexOf('&') != -1) {
    tempFromEEROM = fromEEROM.substring(0, fromEEROM.indexOf('&'));
    if (ssidWF == "") {
      ssidWF = tempFromEEROM;
    }
    else if (passwordWF == "") {        // ????? WHAT do if WiFi is blank
      passwordWF = tempFromEEROM;
    }
    fromEEROM = fromEEROM.substring(fromEEROM.indexOf('&') + 1);
  }
  Serial.print("WiFi name is:");
  Serial.println(ssidWF);
}

char* fromStringToCharArray(String text) { // Convert String to char array (char[])
  int stringLength = text.length() + 1;
  char charArray[stringLength];
  text.toCharArray(charArray, stringLength);
  return charArray;
}

void cleanEEROM(){    // Cleaning the eerom
     for (int i = 0 ; i < EEPROM.length() ; i++) {   // Cleaning the EEROM
        EEPROM.write(i, 0);
      }
      EEPROM.commit();    //Store data to EEPROM
}




//  ------------------------Hot spot server functions-----------------------------------------------

bool disconnectClient() {   // return true if user is
  unsigned long timeout = millis() + 1000;
  while (!client.available() && millis() < timeout) {
    delay(1);
  }
  if (millis() > timeout) {
    Serial.println("timeout");
    client.flush();
    client.stop();
    return false;
  }
  return true;
}

bool isClientConnected() { // If client is connectd return true
  // Check if a client has connected
  //WiFiClientSecure client = server.available(); HTTPS
  client = serverHS.available();
  delay(500);
  Serial.print(".");
  if (!client) {
    return false;
  }
  // Wait until the client sends some data
  Serial.println("new client");
  return disconnectClient();

}



String getUrl(String request1) { // Taking the request and returning the url
  if (request1.indexOf("favicon.ico") != -1) { // redandent request for page image
    return "";
  }
  String requestURL = request1.substring(request1.indexOf("/") + 1,  request1.indexOf(" HTTP/1.1")); // cuting "GET /" and " HTTP/1.1" from the string
  Serial.println("This is the url:");
  if (requestURL.indexOf("%20") != -1) {   // Incase wifi have space in the name
    requestURL.replace("%20", " ");      // change space valiu ("%20") to space " "
  }
  Serial.print("The url is:");
  Serial.println(requestURL);
  return requestURL;
}

String getNetworkName(String url) {   // getting the url and returning the Network name that the user enterd in the text field
  return url.substring(url.indexOf("=") + 1, url.indexOf("&"));
}


String getNetworkPassword (String url) { // getting the url and returning the Network password that the user enterd in the text field
  String tempUrl = url.substring(url.indexOf("=") + 1); // to fix a bug in the substring
  tempUrl =  tempUrl.substring(tempUrl.indexOf("=") + 1);
  return tempUrl.substring(tempUrl.indexOf("=") + 1);
}


bool hotSpotLoopFunction() {  // All code of the hot spot starting here   return fals if hot spot is closed
  
  if (checkWifStatus && WiFi.status() == WL_CONNECTED) {  // If wifi reconnect get wifiName and password from storeg (EEPROM) and exit
    checkWifStatus = false;
    Serial.print("Device WiFi ip is:");
    Serial.println(WiFi.localIP());
    serverHS.stop();
    WiFi.mode(WIFI_OFF);    // stop the HotSpot
    eeromToVariabels();   // getting the data from drive to local variables
    
    Serial.print("This is ssidWF:");
    Serial.println(ssidWF);
    Serial.print("This is passwordWF:");
    Serial.println(passwordWF);
    if(ssidWF == "" || passwordWF == ""){
      Serial.print("I am in");
      return false;
    }
    return true;
  }
  
  checkWifStatus = true;
  if (!isClientConnected()) { // Continue the loop only if client is connected
    return false;
  }

  // Read the first line of the request
  String request = client.readStringUntil('\r');
  String requestUrl = getUrl(request);
  if (requestUrl != "") {
    Serial.println("This is information");

    Serial.print("This is network name:");
    ssidWF = getNetworkName(requestUrl);
    Serial.println(ssidWF);

    Serial.print("This is password:");
    passwordWF = getNetworkPassword(requestUrl);
    Serial.println(passwordWF);
    if (ssidWF != "") {
      // Prepare the response
      String s = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html><head></head><body><h2>HTML Forms</h2><h1>By By</h1></body></html>";

      // Send the response to the client
      client.print(s);

      Serial.println("Client disconnected");
      client.flush();
      client.stop();
      serverHS.stop();
      WiFi.mode(WIFI_OFF);
      cleanEEROM();
      writingStringToEEROM(ssidWF);
      writingStringToEEROM(passwordWF);
      Serial.println();
      return true;
    }
  }

  Serial.println("");

  // Prepare the response
  String s = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html><head></head><body><h2>HTML Forms</h2><form action='/'>";
  s += "Network name:<br><input type='text' name='Network_name' value='w3o'><br>Password<br><input type='text' name='Network_password' value='123'><br><br>";
  s += "<input type='submit' value='Submit'></form>";
  s += "</body></html>";

  // Send the response to the client
  client.print(s);

  Serial.println("Client disconnected");
  Serial.println("");
  delay(1);
  return false;
}





//  ------------------------Wifi server functions-----------------------------------------------

void wifiLoopFunction() {
  // Connect to WiFi network
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssidWF);
    WiFi.begin(ssidWF, passwordWF);
    Serial.println(ssidWF);
    Serial.println("wifiName");
    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }

    // Set the certificates from PMEM (if using DRAM remove the _P from the call)

    // server.setServerKeyAndCert_P(rsakey, sizeof(rsakey), x509, sizeof(x509)); HTTPS

    Serial.println("");
    Serial.println("WiFi connected");

    // Start the server
    serverWF.begin();
    Serial.println("Server started");

    // Print the IP address
    Serial.print("Use this URL : ");
    Serial.print("http://");
    Serial.print(WiFi.localIP());
    Serial.println("/");
  }




  // Check if a client has connected
  //WiFiClientSecure client = server.available(); HTTPS
  WiFiClient client = serverWF.available();
  if (!client) {
    return;
  }

  // Wait until the client sends some data
  Serial.println("new client");
  unsigned long timeout = millis() + 1000;
  while (!client.available() && millis() < timeout) {
    delay(1);
  }
  if (millis() > timeout) {
    Serial.println("timeout");
    client.flush();
    client.stop();
    return;
  }

  // Read the first line of the request
  String request = client.readStringUntil('\r');
  Serial.println(request);
  client.flush();

  // Match the request


  if (request.indexOf("/LED=ON") != -1) {
    pinState = HIGH;
    digitalWrite(ledPin, !pinState);
  }
  else if (request.indexOf("/LED=OFF") != -1) {
    pinState = LOW;
    digitalWrite(ledPin, !pinState);
  }
  else {
    // same state as befor, to get the status of the pin
  }
  // else {
  //   Serial.println("invalid request");
  //   client.print("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html><body>Not found</body></html>");
  //   return;


  // Prepare the response
  String s = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html>\r\nGPIO is now: ";
  s += (pinState) ? "On<br/>Click <a href=\"/LED=OFF\">here</a> Turn relay OFF<br>" : "Off<br/>Click <a href=\"/LED=ON\">here</a> Turn relay ON<br>";
  s += "</html>";


  // Send the response to the client
  client.print(s);

  Serial.println("Client disconnected");
  Serial.println("");
  delay(1);
  return;
}











//  ------------------------Main loop-----------------------------------------------
void loop() {
  
  if (!stopHotSpot) {
    stopHotSpot = hotSpotLoopFunction();
  }
  else {
    wifiLoopFunction();
  }
}
