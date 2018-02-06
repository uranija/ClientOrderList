package com.example.android.clientorderlist.data;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class OrderDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "ordersDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    OrderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the orders database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create orders table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + OrderContract.OrderEntry.TABLE_NAME + " (" +
                OrderContract.OrderEntry._ID                + " INTEGER PRIMARY KEY, " +
                OrderContract.OrderEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                OrderContract.OrderEntry.COLUMN_PRIORITY    + " INTEGER NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OrderContract.OrderEntry.TABLE_NAME);
        onCreate(db);
    }
}

