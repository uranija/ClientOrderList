package com.example.android.clientorderlist;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.clientorderlist.data.OrderContract;



public class EditOrderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // Declare a member variable to keep track of a order's selected mPriority
    private int mPriority;
    EditText mDescriptionEditText;

    /**
     * Identifier for the Item data loader
     */
    private static final int EXISTING_Item_LOADER = 0;

    /**
     * Content URI for the existing Item (null if it's a new Item)
     */
    private Uri mCurrentItemUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        mDescriptionEditText=(EditText) findViewById(R.id.editTextOrderDescription);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        setTitle("Edit item");

        getLoaderManager().initLoader(EXISTING_Item_LOADER, null, this);
    }


    /**
     * onClickAddTask is called when the "SAVE" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickEditOrder(View view) {

        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = ((EditText) findViewById(R.id.editTextOrderDescription)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Insert new order data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the order description and selected mPriority into the ContentValues
        contentValues.put(OrderContract.OrderEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(OrderContract.OrderEntry.COLUMN_PRIORITY, mPriority);


        // Insert the content values via a ContentResolver
        int rowsAffected = getContentResolver().update(mCurrentItemUri, contentValues, null, null);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(rowsAffected != 0) {
            Toast.makeText(getBaseContext(), "Order saved", Toast.LENGTH_LONG).show();

            // Finish activity (this returns back to MainActivity)
            finish();
        }
        else {
            Toast.makeText(getBaseContext(), "Order saving failed", Toast.LENGTH_LONG).show();
        }
    }







    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the order table
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_DESCRIPTION,
                OrderContract.OrderEntry.COLUMN_PRIORITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of order attributes that we're interested in
            int descriptionColumnIndex = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_DESCRIPTION);
            int priorityColumnIndex = cursor.getColumnIndex(OrderContract.OrderEntry.COLUMN_PRIORITY);


            // Extract out the value from the Cursor for the given column index
            String description = cursor.getString(descriptionColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);

            // Update the views on the screen with the values from the database
            mDescriptionEditText.setText(description);
            setPriority(priority);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setPriority(int priority) {
        mPriority = priority;

        switch (priority) {
            case 1:
                mPriority = 1;
                ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
                break;
            case 2:
                ((RadioButton) findViewById(R.id.radButton2)).setChecked(true);
                break;
            case 3:
                ((RadioButton) findViewById(R.id.radButton3)).setChecked(true);
                break;
        }
    }
}



