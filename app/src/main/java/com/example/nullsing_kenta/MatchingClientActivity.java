package com.example.nullsing_kenta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class MatchingClientActivity extends Activity {

    String matchingType;

    static final String TAG = "BTTEST1";
    BluetoothAdapter bluetoothAdapter;

    TextView btStatusTextView;
    TextView tempTextView;

    BTClientThread btClientThread;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            String s;

            switch(msg.what){
                case Constants.MESSAGE_BT:
                    s = (String) msg.obj;
                    if(s != null){
                        btStatusTextView.setText(s);
                    }
                    break;
                case Constants.MESSAGE_TEMP:
                    s = (String) msg.obj;
                    if(s != null){
                        tempTextView.setText(s);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_client);

        Intent intent = this.getIntent();
        matchingType = intent.getStringExtra("matchingType");
        TextView textView = (TextView)this.findViewById(R.id.text_matching_type);
        textView.setText(matchingType);

        LinearLayout menu_home = (LinearLayout) findViewById(R.id.menu_home_l);
        menu_home.setClickable(true);
        menu_home.setOnClickListener(new MatchingClientActivity.MenuHomeOnClickListener());

        LinearLayout menu_mylist = (LinearLayout) findViewById(R.id.menu_myList_l);
        menu_mylist.setClickable(true);
        menu_mylist.setOnClickListener(new MatchingClientActivity.MenuMyListOnClickListener());

        LinearLayout menu_addlist = (LinearLayout)findViewById(R.id.menu_addList_l);
        menu_addlist.setClickable(true);
        menu_addlist.setOnClickListener(new MatchingClientActivity.MenuAddListOnClickListener());

        // Find Views
        btStatusTextView = (TextView) findViewById(R.id.btStatusTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        tempTextView.setOnClickListener(new MatchingClientActivity.MatchingPasswordOnClickListener());

        if(savedInstanceState != null){
            String temp = savedInstanceState.getString(Constants.STATE_TEMP);
            tempTextView.setText(temp);
        }

        // Initialize Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( bluetoothAdapter == null ){
            Log.d(TAG, "This device doesn't support Bluetooth.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btClientThread = new BTClientThread();
        btClientThread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(btClientThread != null){
            btClientThread.interrupt();
            btClientThread = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.STATE_TEMP, tempTextView.getText().toString());
    }

    public class BTClientThread extends Thread {

        InputStream inputStream;
        OutputStream outputStrem;
        BluetoothSocket bluetoothSocket;

        public void run() {

            byte[] incomingBuff = new byte[64];

            BluetoothDevice bluetoothDevice = null;
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice device : devices){
                if(device.getName().equals(Constants.BT_DEVICE)) {
                    bluetoothDevice = device;
                    break;
                }
            }

            if(bluetoothDevice == null){
                Log.d(TAG, "No device found.");
                return;
            }

            try {

                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                        Constants.BT_UUID);

                while(true) {

                    if(Thread.interrupted()){
                        break;
                    }

                    try {
                        bluetoothSocket.connect();

                        handler.obtainMessage(
                                Constants.MESSAGE_BT,
                                "CONNECTED " + bluetoothDevice.getName())
                                .sendToTarget();

                        inputStream = bluetoothSocket.getInputStream();
                        outputStrem = bluetoothSocket.getOutputStream();

                        while (true) {

                            if (Thread.interrupted()) {
                                break;
                            }

                            // Send Command
                            String command = "GET:TEMP";
                            outputStrem.write(command.getBytes());
                            // Read Response
                            int incomingBytes = inputStream.read(incomingBuff);
                            byte[] buff = new byte[incomingBytes];
                            System.arraycopy(incomingBuff, 0, buff, 0, incomingBytes);
                            String s = new String(buff, StandardCharsets.UTF_8);

                            // Show Result to UI
                            handler.obtainMessage(
                                    Constants.MESSAGE_TEMP,
                                    s)
                                    .sendToTarget();

                            // Update again in a few seconds
                            Thread.sleep(3000);
                        }

                    } catch (IOException e) {
                        // connect will throw IOException immediately
                        // when it's disconnected.
                        Log.d(TAG, e.getMessage());
                    }

                    handler.obtainMessage(
                            Constants.MESSAGE_BT,
                            "DISCONNECTED")
                            .sendToTarget();

                    // Re-try after 3 sec
                    Thread.sleep(3 * 1000);
                }

            }catch (InterruptedException e){
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if(bluetoothSocket != null){
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {}
                bluetoothSocket = null;
            }

            handler.obtainMessage(
                    Constants.MESSAGE_BT,
                    "DISCONNECTED - Exit BTClientThread")
                    .sendToTarget();
        }
    }

    private class MenuHomeOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MatchingClientActivity.this, MainActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MenuMyListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MatchingClientActivity.this, MyListActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MenuAddListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MatchingClientActivity.this, AddListActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MenuMathcingOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MatchingClientActivity.this, MathcingActivity.class);
            // Activityの移動
            startActivity(intent);
        }
    }

    private class MatchingPasswordOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 引数1：自身のActivity、引数2:移動先のActivity名
            Intent intent = new Intent(MatchingClientActivity.this, ResultActivity.class);
            intent.putExtra("matchingType", matchingType);
            // Activityの移動
            startActivity(intent);
        }
    }

}
