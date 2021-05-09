package com.example.diytags;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    Button mGetPairedDevicesButton, mBluetoothSwitch;
    ListView lv;
    BluetoothDevice mBtDevice;
    private InputStream mInStream;
    private OutputStream mOutputStream;
    EditText mEditText;
    LinearLayout mEditTextParent;
    private BluetoothSocket mSocket = null;
    private BluetoothOperationThread bluetoothOperationThread = null;
    private final int BT_ID_LEN = 7;

    private int CURR_STATE = 0;

    // Defining states
    private final int NOT_CONNECTED = 0;
    private final int CONNECTING = 1;
    private final int CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
        mGetPairedDevicesButton = (Button) findViewById(R.id.get_paired_devices);
        mBluetoothSwitch = (Button) findViewById(R.id.switch_bt);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mEditTextParent = (LinearLayout) findViewById(R.id.edit_text_parent);
    }

    public void turnOnBluetooth (View v) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
            Log.d("BT", "Turn ON BT");
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void listDevices(View v){
        if (!BA.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Turn on BT first", Toast.LENGTH_LONG).show();
        }
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            Log.d("ListDevices", bt.getName());
            list.add(bt.getName());
        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, list);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mEditTextParent.setVisibility(View.INVISIBLE );
                Log.d("List view", "Clicked " + list.get(position));
                Log.d("List view", "Position " + position);
                mBtDevice = (BluetoothDevice) pairedDevices.toArray()[position];
                ConnectThread connectThread = new ConnectThread(mBtDevice, list.get(position).toString());
                connectThread.start();
//                connectToDevice(mBtDevice);
            }
        });
    }
    public void connectToDevice(BluetoothDevice btDevice) {
        try {
            ParcelUuid[] uuidItems = btDevice.getUuids();
            BluetoothSocket socket = btDevice.createRfcommSocketToServiceRecord(uuidItems[0].getUuid());
            socket.connect();
            mOutputStream = socket.getOutputStream();
            mInStream = socket.getInputStream();
            mEditTextParent.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTextToDevice(View v) throws IOException {
        String message = mEditText.getText().toString();
        deviceWrite(message);
    }

    public void deviceWrite(String s) throws IOException {
        Log.v("Sending to device", "Sending message: " + s);
//            mOutputStream.write(s.getBytes());
        if (bluetoothOperationThread != null) {
            bluetoothOperationThread.write(s);
        }
        else {
            Log.v("Sending to device", "Not sending as connection not established");
            Toast.makeText(getApplicationContext(), "Not connected, please connect first", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectThread extends Thread {
        /**
         * This class defines thread that should run to connect to a bluetooth device
         */
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private String mmName;

        public ConnectThread(BluetoothDevice device, String deviceName) {
            mmDevice = device;
            mmName = deviceName;
            ParcelUuid[] uuidItems = mmDevice.getUuids();
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(uuidItems[0].getUuid());
            }
            catch (IOException e) {
                mmSocket = null;
            }
        }

        public void run() {
            BA.cancelDiscovery();
            try {
                Log.v("Connecting to BT", "Trying to connect to BT");
                CURR_STATE = CONNECTING;
                mmSocket.connect();
                mSocket = mmSocket;
//                mOutputStream = mmSocket.getOutputStream();
//                mInStream = mmSocket.getInputStream();
                CURR_STATE = CONNECTED;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected to " + mmName, Toast.LENGTH_LONG).show();
                        mEditTextParent.setVisibility(View.VISIBLE);
                    }
                });
            } catch (IOException e) {
                Log.v("Connecting to BT", "Error while trying to connect to " + mmName);
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Error while connecting to " + mmName, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                e.printStackTrace();
                return;
            }
            bluetoothOperationThread = new BluetoothOperationThread(mmSocket);
            bluetoothOperationThread.start();
        }

    }

    private class BluetoothOperationThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInputStream;
        private OutputStream mmOutputStream;

        public BluetoothOperationThread(BluetoothSocket socket) {
            mmSocket = socket;
            mmInputStream = null;
            mmOutputStream = null;
            try {
                mmInputStream = mmSocket.getInputStream();
                mmOutputStream = mmSocket.getOutputStream();
                mOutputStream = mmOutputStream;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            int byteCount;
            String bluetoothID = null;

            while (true) {
                try {
                    byteCount = mmInputStream.available();
                    if (byteCount >= BT_ID_LEN) {
//                        Log.v("BluetoothRead", "Number of bytes: " + byteCount);
                        byte[] buffer = new byte[byteCount];
                        byteCount = mmInputStream.read(buffer);
                        final String bufferString = new String(buffer, "UTF-8");
                        String stringTokens[] = bufferString.split("\n");
                        for (String s: stringTokens){
                            if (s.length() == BT_ID_LEN) {
                                bluetoothID = s;
                            }
                        }
//                        Log.v("BluetoothRead", "Input read: " + bufferString);
                        Log.v("BluetoothRead", "bluetoothID: #" + bluetoothID + "#");
                    }
                    Thread.sleep(10);
                } catch (IOException | InterruptedException e) {
                    break;
                }
            }
        }

        public void write(String message) {
            try {
                Log.v("BluetothOperationThread", "Sending " + message + " to " + mmOutputStream.toString());
                mmOutputStream.write(message.getBytes());
            } catch (IOException e) {
                Log.e("Sending to device", e.toString());
            }
        }

    }

}