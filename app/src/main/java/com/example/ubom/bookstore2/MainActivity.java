package com.example.ubom.bookstore2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private BookDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookContract.ProductEntry._ID,
                BookContract.ProductEntry.COLUMN_PRICE,
                BookContract.ProductEntry.COLUMN_PRODUCT_NAME,
                BookContract.ProductEntry.COLUMN_QUANTITY,
                BookContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                BookContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        // Perform a query on the pets table
        Cursor cursor = db.query(
                BookContract.ProductEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_pet);


        try {
            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            //price, quantity, product name, supplier name, supplier phone
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BookContract.ProductEntry._ID + " - " +
                    BookContract.ProductEntry.COLUMN_PRICE + " - " +
                    BookContract.ProductEntry.COLUMN_QUANTITY + " - " +
                    BookContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    BookContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry._ID);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry.COLUMN_PRICE);
            int p_nameColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int amtColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry.COLUMN_QUANTITY);
            int s_nameColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int s_phoneColumnIndex = cursor.getColumnIndex(BookContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                String currentAmt = cursor.getString(amtColumnIndex);
                int currentSupName = cursor.getInt(s_nameColumnIndex);
                int currentSupPhoneNum = cursor.getInt(s_phoneColumnIndex);
                int currentProdName = cursor.getInt(p_nameColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentAmt + " - " +
                        currentPrice + " - " +
                        currentProdName + " - " +
                        currentSupName + " - " +
                        currentSupPhoneNum));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.ProductEntry.COLUMN_PRODUCT_NAME, "Toto");
        values.put(BookContract.ProductEntry.COLUMN_PRICE, "$1.00");
        values.put(BookContract.ProductEntry.COLUMN_QUANTITY, 0);
        values.put(BookContract.ProductEntry.COLUMN_SUPPLIER_NAME, "Elegushi");
        values.put(BookContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 777 - 997 - 8414);
        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
//        Uri newUri = getContentResolver().insert(BookContract.ProductEntry.CONTENT_URI, values);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the books table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.

        long newRowId = db.insert(BookContract.ProductEntry.TABLE_NAME, null, values);

//        @Override
//        public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
//                String sortOrder) {
//            // Get readable database
//            SQLiteDatabase database = mDbHelper.getReadableDatabase();
//
//            // This cursor will hold the result of the query
//            Cursor cursor;
//
//            // Figure out if the URI matcher can match the URI to a specific code
//            int match = sUriMatcher.match(uri);
//            switch (match) {
//                case BOOKS:
//                    // For the PETS code, query the pets table directly with the given
//                    // projection, selection, selection arguments, and sort order. The cursor
//                    // could contain multiple rows of the pets table.
//                    // TODO: Perform database query on pets table
//                    cursor = database.query(BookContract.ProductEntry.TABLE_NAME,
//                            projection,
//                            selection,
//                            selectionArgs,
//                            null,
//                            null,
//                            sortOrder);
//
//                    break;
//                case BOOK_ID:
//                    // For the PET_ID code, extract out the ID from the URI.
//                    // For an example URI such as "content://com.example.android.pets/pets/3",
//                    // the selection will be "_id=?" and the selection argument will be a
//                    // String array containing the actual ID of 3 in this case.
//                    //
//                    // For every "?" in the selection, we need to have an element in the selection
//                    // arguments that will fill in the "?". Since we have 1 question mark in the
//                    // selection, we have 1 String in the selection arguments' String array.
//                    selection = BookContract.ProductEntry._ID + "=?";
//                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
//
//                    // This will perform a query on the pets table where the _id equals 3 to return a
//                    // Cursor containing that row of the table.
//                    cursor = database.query(BookContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
//                            null, null, sortOrder);
//                    break;
//                default:
//                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
//            }
//            return cursor;
//        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu options from the res/menu/menu_catalog.xml file.
            // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_catalog, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // User clicked on a menu option in the app bar overflow menu
            switch (item.getItemId()) {
                // Respond to a click on the "Insert dummy data" menu option
                case R.id.action_insert_dummy_data:
                    insertBook();
                    displayDatabaseInfo();
                    return true;
//                // Respond to a click on the "Delete all entries" menu option
//                case R.id.action_delete_all_entries:
//                    // Do nothing for now
//                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
