# DIY Tags

## Design and documentation

Everything can be currently found in this document: [google docs link](https://docs.google.com/document/d/1Ppr09-gekl5JMUxhiXsnFstjVjhbimueqNX8HMSm2II/edit?usp=sharing)


## Setup

### Arduino

1. `Tx` of __HC-05__ connected to `Rx` of __Arduino__.
1. `Rx` of __HC-05__ connected to `Tx` of __Arduino__.


## Current status

1. Currently turns on bluetooth.
1. Shows paired devices.
1. Select paired device to connect.
1. Send stream to `arduino` via BT.


## To-Do

### Bluetooth communication

- [x] Need to make it work with `Tx` from arduino connected.
- [x] Need to run input stream loop along with output stream.
- [x] Make the connection in background using `AsyncTask` or similar.
- [ ] Add feature to pair device from app.
- [x] Receive data from arduino.

### DB

- [ ] Store data of `tag` and location in DB.
- [ ] Create a DB server in my PC and use it as cloud server to store/fetch data.

### Map

- [x] Integrate a basic map.
- [ ] Get current user location from app.
- [ ] Show the `tag` location on map.

### UI

- [ ] Improve screen transition from `MapActivity` to `MainActivity`.

### General

- [ ] Remove unnecessary code.
- [ ] Add docstrings.
- [ ] Move the documentation from google docs to wiki ? (Maybe later)

### Bugs & Improvements

- [ ] Stable and robust bluetooth connection, and making sure no duplicate threads are created.
- [ ] Fix this bug: `com.example.diytags E/Sending to device: java.io.IOException: Broken pipe` (I get this sometimes when I try to communicate with the BT module after connection is established)
- [ ] Fix this warning: `W/BluetoothAdapter: getBluetoothService() called with no BluetoothManagerCallback`
- [ ] Fix this bug: `E/example.diytag: Failed to open file '/data/data/com.example.diytags/code_cache/.overlay/base.apk/assets/Mapnik/16/46893/30391.png': No such file or directory`
- [ ] The BT devices list must appear as a pop up and disappear after its purpose is served.
- [ ] A check if the BT module is already connected.
- [ ] Connect to BT device only which is compatible with the application (mostly BT modules connected to Arduino)


## Arduino code reference

[Github repo for arduino code](https://github.com/25b3nk/arduino-projects/tree/master/bluetooth/diy_tags)


## References

1. https://www.tutorialspoint.com/android/android_bluetooth.htm
1. https://developer.android.com/guide/topics/connectivity/bluetooth
1. https://stackoverflow.com/a/22899728/5258060
1. https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
1. https://stackoverflow.com/a/18480297/5258060
1. [`Changing UI in non-UI thread by calling a UI thread`](https://stackoverflow.com/a/47536058/5258060)
1. https://www.electronics-lab.com/get-sensor-data-arduino-smartphone-via-bluetooth/
1. [`Using Open Street Map API`](https://github.com/osmdroid/osmdroid/wiki)
1. [`How to use the osmdroid library (Kotlin)`](https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Kotlin))
