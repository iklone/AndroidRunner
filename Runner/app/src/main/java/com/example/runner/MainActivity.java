package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private RunnerService.MyBinder service = null;
    private Runner runner;
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runner = new Runner(this);

        //bind to service
        this.bindService(new Intent(this, RunnerService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        //start update thread
        myThread.start();
    }

    //when connecting to service
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("MDP", "Service connected");
            service = (RunnerService.MyBinder) iBinder;

            //pass runner object from main to supply correct context
            service.setRunner(runner);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    //thread used to schedule main activity view updates, one per second
    Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.d("mdp", e.toString());
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        updateMain();
                    }
                });
            }
        }
    });

    //update main activity views
    public void updateMain() {
        //find views
        TextView txtCurrentTime = findViewById(R.id.mtxtTime);
        TextView txtCurrentDistance = findViewById(R.id.mtxtDistance);
        TextView txtSpeed = findViewById(R.id.mtxtSpeed);

        //get values from service
        long intCurrentTime = service.getCurrentTime();
        float intCurrentDistance = service.getCurrentDistance();

        //calculate speed
        float speed = (intCurrentDistance / 1609f) / (intCurrentTime / 3600f); //1 mile = 1609 metres and 1 hour = 3600 seconds

        //Setup rounding formatting for speed. Using 2dp
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        //format values
        String strCurrentTime = (new SimpleDateFormat("mm:ss")).format(new Date(intCurrentTime * 1000)).toString();
        String strCurrentDistance = Math.round(intCurrentDistance) + "m";

        //if time = 0 then speed will be NaN. Reformat this
        String strCurrentSpeed;
        if (Float.isNaN(speed)) {
            strCurrentSpeed = "0mph";
        } else {
            strCurrentSpeed = df.format(speed) + "mph";
        }

        //send values to views
        txtCurrentTime.setText(strCurrentTime);
        txtCurrentDistance.setText(strCurrentDistance);
        txtSpeed.setText(strCurrentSpeed);

        Log.d("MDP", "Time = " + intCurrentTime + ", Distance = " + intCurrentDistance);
    }

    //start session button
    public void onBtnStartClick(View view) {
        //reformat activity to show/hide relevant buttons
        Button btnstart = findViewById(R.id.btnStart);
        Button btnpause = findViewById(R.id.btnPause);
        Button btnstop = findViewById(R.id.btnStop);

        btnstart.setVisibility(View.INVISIBLE);
        btnpause.setVisibility(View.VISIBLE);
        btnstop.setVisibility(View.VISIBLE);

        //start service and reset to new session
        service.start(true);
        Log.d("MDP", "Started new session");
    }

    //pause/play session button
    public void onBtnPauseClick(View view) {
        Button btn = view.findViewById(R.id.btnPause);
        if (service.isRecording()) { //if is recording then stop session
            btn.setText("PLAY");
            service.stop();
        } else { //if not recording then start session but do not reset values
            btn.setText("PAUSE");
            service.start(false);
        }
    }

    //terminate session button
    public void onBtnStopClick(View view) {
        //reformat activity to show/hide relevant buttons
        Button btnstart = findViewById(R.id.btnStart);
        Button btnpause = findViewById(R.id.btnPause);
        Button btnstop = findViewById(R.id.btnStop);

        btnstart.setVisibility(View.VISIBLE);
        btnpause.setVisibility(View.GONE);
        btnstop.setVisibility(View.GONE);

        //halt session (not service)
        service.stop();
        Log.d("MDP", "Stopped session");

        //get final values from service
        long cTime = service.getCurrentTime();
        float cDistance = service.getCurrentDistance();
        float cSpeed = cTime / cDistance;

        //create content value object
        ContentValues newValues = new ContentValues();
        newValues.put(MyProviderContract.TIME, cTime);
        newValues.put(MyProviderContract.DISTANCE, cDistance);
        newValues.put(MyProviderContract.DISTANCE, cSpeed);

        //Store content in DB
        getContentResolver().insert(MyProviderContract.R_URI, newValues);

        //view new data in view run activity
        Intent intent = new Intent(MainActivity.this, ViewRunActivity.class);
        startActivity(intent);
    }

    //view session history activity
    public void onBtnHistoryClick(View view) {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }
}
