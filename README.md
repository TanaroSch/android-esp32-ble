# android-esp32-ble
This serves as a project template for a bluetooth le connection between an ESP32 and and android app.
## features:
- ESP32 BLE uart server
	- easy usage with `sendMessage(String message)` and `messageHandler(String message)` functions
- Android App template
	- checking for permissions before start and requesting them with dialog
	- scans for nearby devices
	- connect to BLE devices
	- 
## components
### ESP32
The ESP32 acts as BLE server and provides a services with two characteristics.
In the messageHandler the code to handle incomming messages has to be inserted:

    void  messageHandler(String  message)
    {
	    // ADD YOUR CODE HERE
	    Serial.println(message);
    }
To send a message `void sendMessage(String message)` can be called. Then the characteristic will be updated and the android app will be notified.

### Android
The android app scans for bluetooth devices. After connection to the ESP32 incoming messages 
## credits
Based on ESP_UART by https://wiki.hackerspace-bremen.de/sonstiges/tutorials/esp32/bluetooth_le_-_uart
