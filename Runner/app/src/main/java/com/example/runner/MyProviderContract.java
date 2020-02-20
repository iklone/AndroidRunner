package com.example.runner;

import android.net.Uri;

public class MyProviderContract {
    //authority
    public static final String AUTHORITY = "com.example.runner.MyContentProvider";

    //tables
    public static final Uri R_URI =Uri.parse("content://" + AUTHORITY + "/runs");

    //strings
    public static final String RUNS_TABLE = "runs";

    //universal fields
    public static final String _ID = "_id";

    //specific fields
    public static final String DATE ="date";
    public static final String TIME ="time";
    public static final String DISTANCE ="distance";
    public static final String RATING ="rating";
    public static final String NOTES ="notes";

    //content types
    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyContentProvider.data.text";
}
