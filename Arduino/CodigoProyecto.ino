#define TINY_GSM_MODEM_SIM800
#define TINY_GSM_RX_BUFFER 256
 
#include <TinyGsmClient.h> //https://github.com/vshymanskyy/TinyGSM
#include <ArduinoHttpClient.h> //https://github.com/arduino-libraries/ArduinoHttpClient
#include <DHT.h>
#include <SoftwareSerial.h>
#include <MKRGSM.h>
#define WIFI_SSID "NETLIFE-Luna"               
#define WIFI_PASSWORD "XXXXXXXXXX"
#define rxPin 7
#define txPin 8
#include <SoftwareSerial.h>
const int pulsadorPin1 = 1;
const int pulsadorPin2 = 2;

int valorPulsador1 = 0;
int valorPulsador2 = 0;
SoftwareSerial gps(4,3);
GSMLocation location;
GPRS gprs;
GSM gsmAccess;
char dato;
 
SoftwareSerial sim800(txPin, rxPin);
  
const char FIREBASE_HOST = "https://transporte-alimentos-g1-astm-default-rtdb.firebaseio.com/";
const String FIREBASE_AUTH  = "1hXbLJ2XS0nphlMsyEINhZpTRDrIWeimzATT4Zap";
const String FIREBASE_PATH  = "/";
const int SSL_PORT          = 443;
 
char apn[]  = "internet.claro.com.ec";//airtel ->"airtelgprs.com"
char user[] = "afluna@espol.edu.ec";
char pass[] = "ak8ka002";

double latitud;   //valor para la latitud
double longitud;  //valor para la longitud

TinyGsm modem(sim800);
 
TinyGsmClientSecure gsm_client_secure_modem(modem, 0);
HttpClient http_client = HttpClient(gsm_client_secure_modem, FIREBASE_HOST, SSL_PORT);
 
unsigned long previousMillis = 0;
 
 
void setup()
{ 
  pinMode(pulsadorPin1, INPUT);
  Serial.begin(9600);
  gps.begin(9600);
  
  sim800.begin(9600);
  Serial.println("SIM800L serial initialize");
 
  Serial.println("Initializing modem...");
  modem.restart();
  String modemInfo = modem.getModemInfo();
  Serial.print("Modem: ");
  Serial.println(modemInfo);

  bool connected = false
  while (!connected) {
    if ((gsmAccess.begin(PINNUMBER) == GSM_READY) &&
        (gprs.attachGPRS(GPRS_APN, GPRS_LOGIN, GPRS_PASSWORD) == GPRS_READY)) {
      connected = true;
    } else {
      Serial.println("Not connected");
      delay(1000);
    }
  }

  location.begin();
 
  http_client.setHttpResponseTimeout(10 * 1000); //^0 secs timeout
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);   // Se realiza la conexión con la base de datos enviando los datos del Host y la Key
}
 
void loop()
{
    if(gps.available())
  {
  dato=gps.read();
  Serial.print(dato);
  } 
  http_client.connect(FIREBASE_HOST, SSL_PORT);

  while (true) {
    if (!http_client.connected())
    {
      Serial.println();
      http_client.stop();// Shutdown
      Serial.println("HTTP  not connect");
      break;
    }

 
  }

  latitud = location.latitude();
  longitud = location.longitude();
  valorPulsador1 = digitalRead(pulsadorPin1);
  if (valorPulsador1 == HIGH) {
      Serial.print("Se envio la informacion correctamente");
      Firebase.set("/UsersRegis/Usuarios/aemorah/localizacion/latitud",latitud);
      Firebase.set("/UsersRegis/Usuarios/aemorah/localizacion/longitud",longitud);
  }
  valorPulsador2 = digitalRead(pulsadorPin2);
  if (valorPulsador2 == HIGH) {
      Serial.print("Babahoyo,Latitud:  -1.830809,Longitud:  -79.546698");
      Serial.print("Se envio la informacion correctamente");
      Firebase.set("/UsersRegis/Usuarios/aemorah/localizacion/latitud",-1.830809);
      Firebase.set("/UsersRegis/Usuarios/aemorah/localizacion/longitud",-79.546698);
  }
 
}
 
 
 

