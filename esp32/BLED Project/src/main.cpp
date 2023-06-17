#include "Arduino.h"
#include "connection/bluetooth.h"


void setup()
{
  Serial.begin(9600);
  setupBluetooth();
}

void loop()
{
  loopBluetooth();
}
