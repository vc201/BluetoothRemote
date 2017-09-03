package cv.com.bluetoothremote.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cv.com.bluetoothremote.Global.BluetoothApplication;
import cv.com.bluetoothremote.R;

import java.io.*;
import java.util.UUID;

// https://github.com/googlesamples/android-BluetoothChat/#readme
// https://stackoverflow.com/questions/10929767/send-text-through-bluetooth-from-java-server-to-android-client
// https://stackoverflow.com/questions/14228289/android-pair-devices-via-bluetooth-programmatically

public class BluetoothActivity extends Activity {

    private final static String serverUUID = "00001101-0000-1000-8000-00805F9B34FB";
    private final static String serverAddress = "40:E2:30:2F:91:62";
    private final static int REQUEST_ENABLE_BT = 1;
    private final static String LOG_TAG = "Bluetooth Activity Log";
    private BluetoothDevice server;
    private BluetoothSocket serverSocket;
    private BluetoothAdapter bluetoothAdapter;
    private Button connectButton;
    private Button testButton;
    private Button stopButton;
    private ButtonClick buttonListener;
    private BluetoothApplication instance = BluetoothApplication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        buttonListener = new ButtonClick();

        connectButton = findViewById(R.id.connectButton);
        testButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        testButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        makeDiscoverable();
        enableBluetooth();

        connectButton.setOnClickListener(buttonListener);
        testButton.setOnClickListener(buttonListener);
        stopButton.setOnClickListener(buttonListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void enableBluetooth() {
        Log.d(LOG_TAG, "Enabling bluetooth...");
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    protected void makeDiscoverable() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
        startActivity(discoverableIntent);
    }

    class ButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.connectButton:
                    bluetoothAdapter.startDiscovery();
                    Log.d(LOG_TAG, "Finding server...");
                    server = bluetoothAdapter.getRemoteDevice(serverAddress);

                    if (server != null) {
                        try {
                            serverSocket = server.createRfcommSocketToServiceRecord(UUID.fromString(serverUUID));
                            serverSocket.connect();
                            instance.initStreams(serverSocket);
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "Server found", Toast.LENGTH_SHORT);
                        toast.show();
                        testButton.setEnabled(true);
                        stopButton.setEnabled(true);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Server not found", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    bluetoothAdapter.cancelDiscovery();
                    break;
                case R.id.startButton:
                        Intent intent = new Intent(BluetoothActivity.this, MediaActivity.class);
                        startActivity(intent);
                    break;
                case R.id.stopButton:
                    try {
                        Log.d(LOG_TAG, "Shutdown...");

                        instance.println("EXITING");
                        String line = instance.readLine();
                        while (line == null || !line.equals("ACK")) {
                            Thread.sleep(1000);
                            line = instance.readLine();
                        }
                        Log.d(LOG_TAG, "Server acknowledged shutdown...");
                        instance.close();

                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    }
}
