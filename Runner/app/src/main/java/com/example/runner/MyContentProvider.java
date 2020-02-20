package com.example.runner;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    private DBHelper dbHelper;
    private static final UriMatcher uriMatcher;

    //possible calls
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MyProviderContract.AUTHORITY, "runs", 1);     //runs table
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "runs/#", 2);   //runs item
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "*", 3);        //all
    }

    public MyContentProvider() {}

    //deleting records from DB
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //get DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String tablename;

        //There is only one table so a switch case for table selection is not required here

        tablename = MyProviderContract.RUNS_TABLE; //Only runs table exists

        db.delete(tablename, selection, selectionArgs);
        db.close();

        return 0;
    }

    //get type of uri
    @Override
    public String getType(Uri uri) {
        String contentType;

        if (uri.getLastPathSegment() == null) {
            contentType = MyProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = MyProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }

    //insert record into DB
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //get DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String tablename;

        //There is only one table so a switch case for table selection is not required here

        tablename = MyProviderContract.RUNS_TABLE; //Only runs table exists

        long id = db.insert(tablename, null, values);
        db.close();

        //return new Uri
        Uri xUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(xUri, null);
        return xUri;
    }

    //create new DB on initial run. Only run on install
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(this.getContext());
        return true;
    }

    //query records from DB
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //get DB
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case 1: //query runs table
                return db.query(MyProviderContract.RUNS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case 2: //query an item from runs
                selection = MyProviderContract._ID + uri.getLastPathSegment();
                return db.query(MyProviderContract.RUNS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                return null;
        }
    }

    //update records from DB
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //get DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String tablename;

        //There is only one table so a switch case for table selection is not required here

        tablename = MyProviderContract.RUNS_TABLE; //Only runs table exists

        db.update(tablename, values, selection, selectionArgs);
        db.close();

        return 0;
    }
}
