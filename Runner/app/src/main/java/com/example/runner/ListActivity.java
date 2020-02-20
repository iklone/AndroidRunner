package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = findViewById(R.id.list);
        Spinner spinner = findViewById(R.id.spinner);

        initialiseSpinner(spinner, list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView list = findViewById(R.id.list);
        Spinner spinner = findViewById(R.id.spinner);

        initialiseSpinner(spinner, list);
    }

    //spinner needs to be initialised when starting or refreshing activity
    public void initialiseSpinner(Spinner spinner, final ListView list) {
        //grab possible orders from res/values/orders.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.orders, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //listener for spinner updates
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerVal = adapterView.getItemAtPosition(i).toString();
                String order;

                //select correct ordering
                switch (spinnerVal) {
                    case "Oldest First":
                        order = "date ASC";
                        break;
                    case "Longest Distance First":
                        order = "distance DESC";
                        break;
                    case "Shortest Distance First":
                        order = "distance ASC";
                        break;
                    case "Longest Duration First":
                        order = "time DESC";
                        break;
                    case "Shortest Duration First":
                        order = "time ASC";
                        break;
                    case "Highest Rated First":
                        order = "rating DESC";
                        break;
                    case "Lowest Rated First":
                        order = "rating ASC";
                        break;
                    case "Fastest First":
                        order = "speed DESC";
                        break;
                    case "Slowest First":
                        order = "speed ASC";
                        break;
                    default:
                        order = "date DESC";
                        break;
                }

                populateList(list, order);
            }

            //default order is newest first
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                populateList(list, "date DESC");
            }
        });
    }

    //populate list with sessions
    public void populateList(final ListView list, String order) {
        //get session data from DB
        Cursor c = getContentResolver().query(MyProviderContract.R_URI, null, null, null, order);

        //define required fields
        String[] display = new String[]{
                MyProviderContract.DATE,
                MyProviderContract.TIME,
                MyProviderContract.DISTANCE,
                MyProviderContract.RATING,
        };

        //define corresponding views
        int[] to = new int[] {
                R.id.xtxtDate,
                R.id.xtxtTime,
                R.id.xtxtDistance,
                R.id.xtxtRating
        };

        //populate list view
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.list_item, c, display, to, 0);
        list.setAdapter(dataAdapter);

        Log.d("mdp", "List populated by " + order);

        //listener for list clicks, sends id of selected session
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListActivity.this, ViewRunActivity.class);
                intent.putExtra("id", l);
                startActivity(intent);
            }
        });
    }

    //back to main button
    public void onBtnBackClick (View view) {
        finish();
    }
}
