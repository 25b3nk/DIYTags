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

- [x] Need to make it work with `Tx` from arduino connected.
- [x] Need to run input stream loop along with output stream.
- [x] Make the connection in background using `AsyncTask` or similar.
- [ ] Stable and robust bluetooth connection, and making sure no duplicate threads are created.
- [ ] Add feature to pair device from app.
- [x] Receive data from arduino.
- [ ] Store data of `tag` and location in DB.
- [ ] Show the `tag` location on map.
- [ ] Remove unnecessary code.
- [ ] Add docstrings.
- [ ] Move the documentation from google docs to wiki ? (Maybe later)


## References

1. https://www.tutorialspoint.com/android/android_bluetooth.htm
1. https://developer.android.com/guide/topics/connectivity/bluetooth
1. https://stackoverflow.com/a/22899728/5258060
1. https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
1. https://stackoverflow.com/a/18480297/5258060
1. [Changing UI in non-UI thread by calling a UI thread](https://stackoverflow.com/a/47536058/5258060)
1. https://www.electronics-lab.com/get-sensor-data-arduino-smartphone-via-bluetooth/
