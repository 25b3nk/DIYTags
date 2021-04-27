package com.example.diytags;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
                try {
                    connectToDevice(mBtDevice);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("connectToDevice", e.toString());
                    Toast.makeText(getApplicationContext(), "Error while connecting to " + list.get(position),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void connectToDevice(BluetoothDevice btDevice) throws IOException {
        ParcelUuid[] uuidItems = btDevice.getUuids();
        BluetoothSocket socket = btDevice.createRfcommSocketToServiceRecord(uuidItems[0].getUuid());
        socket.connect();
        mOutputStream = socket.getOutputStream();
        mInStream = socket.getInputStream();
        mEditTextParent.setVisibility(View.VISIBLE);
    }

    public void sendTextToDevice(View v) throws IOException {
        String message = mEditText.getText().toString();
        deviceWrite(message);
    }

    public void deviceWrite(String s) throws IOException {
        try {
            Log.v("Sending to device", mOutputStream.toString());
            mOutputStream.write(s.getBytes());
        } catch (IOException e) {
            Log.e("Sending to device", e.toString());
        }
    }
}