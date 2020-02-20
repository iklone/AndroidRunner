package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewRunActivity extends AppCompatActivity {

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_run);

        id = getIntent().getLongExtra("id", -1);

        //default load most recent session
        if (id != -1) {
            loadRun();
        } else {
            loadNewRun();
        }
    }

    //load most recent session
    private void loadNewRun() {
        Cursor c = getContentResolver().query(MyProviderContract.R_URI, null, null, null, "date DESC");
        c.moveToFirst();

        id = c.getInt(c.getColumnIndex(MyProviderContract._ID));

        loadRun();
    }

    private void loadRun() {
        //get activity views
        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtDistance = findViewById(R.id.txtDistance);
        TextView txtMiles = findViewById(R.id.txtMiles);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtSpeed = findViewById(R.id.txtSpeed);
        TextView txtRating = findViewById(R.id.txtRating);
        TextView txtNotes = findViewById(R.id.txtNotes);
        SeekBar seekRating = findViewById(R.id.seekRating);

        //query DB
        String selection ="_ID = " + id;
        Cursor c = getContentResolver().query(MyProviderContract.R_URI, null, selection, null, null);
        c.moveToFirst();

        //format date
        long intDate = c.getInt(c.getColumnIndex(MyProviderContract.DATE));
        Log.d("mdp", "date = " + intDate);
        String strDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(intDate * 1000L));

        //format distance
        int distance = c.getInt(c.getColumnIndex(MyProviderContract.DISTANCE));
        float miles = distance / 1609f; //Ratio of metres to miles. 1 mile ~= 1609 metres

        //format time
        int time = c.getInt(c.getColumnIndex(MyProviderContract.TIME));
        float minutes = time / 60; //60 seconds in a minute

        //calculate speed
        float speed = miles / (minutes / 60);

        //decimal point format. To many is unnecessarily complex. Using 2dp
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        //format rating/notes
        int intRating = c.getInt(c.getColumnIndex(MyProviderContract.RATING));
        String notes = c.getString(c.getColumnIndex(MyProviderContract.NOTES));

        Log.d("mdp", miles + " / " + "( " + minutes + " / 60 ) = " + speed);

        //format star rating
        String strRating = "";
        for (int i = 0; i < intRating; i++) {
            strRating = strRating + "★";
        }

        Log.d("mdp", strRating);

        //pass values to views
        //txtDate.setText(strDate);
        txtDistance.setText(distance + "m");
        txtMiles.setText(df.format(miles) + " miles");
        txtTime.setText((new SimpleDateFormat("mm:ss")).format(new Date(time * 1000)).toString());
        txtSpeed.setText("Average speed: " + df.format(speed) + "mph");
        txtRating.setText(strRating);
        txtNotes.setText(notes);

        //format null rating for seek bar
        if (intRating == 0) {
            intRating++;
        }

        //pass rating to seekBar
        seekRating.setProgress(intRating - 1);

        Log.d("mdp", "Loaded Session");
    }

    //save current session data
    public void onBtnSaveClick(View view) {
        //get activity views
        SeekBar seekRating = findViewById(R.id.seekRating);
        TextView txtNotes = findViewById(R.id.txtNotes);

        //create content object. The user may only update rating and notes
        ContentValues newValues = new ContentValues();
        newValues.put(MyProviderContract.RATING, seekRating.getProgress() + 1);
        newValues.put(MyProviderContract.NOTES, txtNotes.getText().toString());

        //update DB
        getContentResolver().update(MyProviderContract.R_URI, newValues, "_ID = " + id, null);

        finish();
    }

    //delete current session data
    public void onBtnDeleteClick(View view) {
        getContentResolver().delete(MyProviderContract.R_URI, "_ID = " + id, null);

        finish();
    }
}
