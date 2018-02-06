package com.example.android.clientorderlist.data;



import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public class OrderContract {
    /* Add content provider constants to the Contract
     Clients need to know how to access the order data, and it's your job to provide
     these content URI's for the path to that data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the orders directory
        4) Content URI for data in the OrderEntry class
      */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.clientorderlist";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "orders" directory
    public static final String PATH_ORDERS = "orders";

    /* OrderEntry is an inner class that defines the contents of the order table */
    public static final class OrderEntry implements BaseColumns {

        // OrderEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS).build();


        // Order table and column names
        public static final String TABLE_NAME = "orders";

        // Since OrderEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of orders.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ORDERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single order.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ORDERS;


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        orders
         - - - - - - - - - - - - - - - - - - - - - -
        | _id  |    description     |    priority   |
         - - - - - - - - - - - - - - - - - - - - - -
        |  1   |  Math  |       1       |
         - - - - - - - - - - - - - - - - - - - - - -
        |  2   |   Biology     |       3       |
         - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - -
        | 43   |  Grammar     |       2       |
         - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}

