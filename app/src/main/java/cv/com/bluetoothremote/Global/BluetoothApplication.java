package cv.com.bluetoothremote.Global;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;

public class BluetoothApplication extends Application {
    private static BluetoothApplication singleton;
    private static final String TAG = "Bluetooth App Log";
    private PrintWriter out;
    private BufferedReader in;
    private BluetoothSocket socket;


    public static BluetoothApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public void initStreams(BluetoothSocket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public void println(String s) {
        out.println(s);
        out.flush();
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
