package com.example.android.clientorderlist.data;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.clientorderlist.data.OrderContract.OrderEntry.TABLE_NAME;


// Verify that OrderContentProvider extends from ContentProvider and implements required methods

public class OrderContentProvider extends ContentProvider {



    // Define final integer constants for the directory of orders and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int ORDERS = 100;
    public static final int ORDER_WITH_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Define a static buildUriMatcher method that associates URI's with their int match
    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the order directory and a single item by ID.
         */
        uriMatcher.addURI(OrderContract.AUTHORITY, OrderContract.PATH_ORDERS, ORDERS);
        uriMatcher.addURI(OrderContract.AUTHORITY, OrderContract.PATH_ORDERS + "/#", ORDER_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a OrderDbHelper that's initialized in the onCreate() method
    private OrderDbHelper mOrderDbHelper;

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        // Complete onCreate() and initialize a OrderDbHelper on startup


        Context context = getContext();
        mOrderDbHelper = new OrderDbHelper(context);
        return true;
    }


    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the order database (to write new data to)
        final SQLiteDatabase db = mOrderDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the orders directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case ORDERS:
                // Insert new values into the database
                // Inserting values into orders table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(OrderContract.OrderEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mOrderDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the orders directory
            case ORDERS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ORDER_WITH_ID:
                selection = OrderContract.OrderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    // Implement delete to delete a single row of data
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mOrderDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted orders
        int ordersDeleted; // starts as 0

        // Write the code to delete a single row of data
         switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case ORDER_WITH_ID:
                // Get the order ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                ordersDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (ordersDeleted != 0) {
            // A order was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of orders deleted
        return ordersDeleted;
    }




    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDERS:
                return updateOrder(uri, contentValues, selection, selectionArgs);
            case ORDER_WITH_ID:
                // For the ORDER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = OrderContract.OrderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateOrder(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }




    /**
     * Update orders in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more orders).
     * Return the number of rows that were successfully updated.
     */
    private int updateOrder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(OrderContract.OrderEntry.COLUMN_DESCRIPTION)) {
            String name = values.getAsString(OrderContract.OrderEntry.COLUMN_DESCRIPTION);
            if (name == null) {
                throw new IllegalArgumentException("Order requires description");
            }
        }




        if (values.containsKey(OrderContract.OrderEntry.COLUMN_PRIORITY)) {

            Integer priority = values.getAsInteger(OrderContract.OrderEntry.COLUMN_PRIORITY);
            if (priority != null && priority < 0) {
                throw new IllegalArgumentException("Order requires priority");
            }
        }



        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mOrderDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(OrderContract.OrderEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDERS:
                return OrderContract.OrderEntry.CONTENT_LIST_TYPE;
            case ORDER_WITH_ID:
                return OrderContract.OrderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }










}