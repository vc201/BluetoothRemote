package cv.com.bluetoothremote.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import cv.com.bluetoothremote.Global.BluetoothApplication;
import cv.com.bluetoothremote.R;

public class MediaActivity extends Activity {

    private BluetoothApplication instance = BluetoothApplication.getInstance();
    private final static String LOG_TAG = "Media Activity Log";
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private ImageButton volUpButton;
    private ImageButton volDownButton;
    private ImageButton exitButton;
    private ButtonClick buttonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        buttonListener = new ButtonClick();

        playPauseButton = findViewById(R.id.play_pause);
        stopButton = findViewById(R.id.stop);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);
        volUpButton = findViewById(R.id.vol_up);
        volDownButton = findViewById(R.id.vol_down);
        exitButton = findViewById(R.id.exit);
    }

    @Override
    protected void onStart() {
        super.onStart();

        playPauseButton.setOnClickListener(buttonListener);
        stopButton.setOnClickListener(buttonListener);
        prevButton.setOnClickListener(buttonListener);
        nextButton.setOnClickListener(buttonListener);
        volUpButton.setOnClickListener(buttonListener);
        volDownButton.setOnClickListener(buttonListener);
        exitButton.setOnClickListener(buttonListener);
    }

    class ButtonClick implements View.OnClickListener {

        private void keyPress(String key) {
            try {
                instance.println(key);
                Log.d(LOG_TAG, "Sending string to server...");
                String line = instance.readLine();
                while (line == null || !line.equals("ACK")) {
                    Thread.sleep(1000);
                    line = instance.readLine();
                }
                Log.d(LOG_TAG, "Server acknowledged message...");

            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.play_pause:
                    keyPress("PLAY_PAUSE");
                    break;
                case R.id.prev:
                    keyPress("PREV");
                    break;
                case R.id.next:
                    keyPress("NEXT");
                    break;
                case R.id.vol_up:
                    keyPress("VOL_UP");
                    break;
                case R.id.vol_down:
                    keyPress("VOL_DOWN");
                    break;
                case R.id.stop:
                    keyPress("STOP");
                    break;
                case R.id.exit:
                    Log.d(LOG_TAG, "Shutdown...");
                    keyPress("EXITING");
                    Log.d(LOG_TAG, "Server acknowledged shutdown...");
                    try {
                        instance.close();

                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    }
}
