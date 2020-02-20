package com.example.runner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "recipeDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE runs (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "date INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "time INTEGER NOT NULL," +
                "distance INTEGER NOT NULL," +
                "speed FLOAT NOT NULL," +
                "rating INTEGER," +
                "notes VARCHAR(250));");

        //Some example sessions.
        db.execSQL("INSERT INTO runs (time, distance, speed, rating, notes) VALUES" +
                "(2400, 10510, 4.38, 2, 'This wasn''t as far as I expected.')," +
                "(610, 3300, 5.41, 5, 'I saw a deer.')," +
                "(1200, 4120, 3.42, null, '')," +
                "(4050, 18000, 4.44, 1, 'This was too far.')," +
                "(700, 4500, 6.43, 3, '')," +
                "(1380, 8700, 6.30, 3, 'Ran fast today.')");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
