package com.example.ubom.bookstore2;


/**
 * Created by ubom on 9/21/18.
 */

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ubom.bookstore2.data.BookContract.ProductEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public abstract class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mCursorAdapter.swapCursor(data);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                BookContract.ProductEntry._ID,
//                BookContract.ProductEntry.COLUMN_PRODUCT_NAME,
//                BookContract.ProductEntry.COLUMN_PRICE,
//                BookContract.ProductEntry.COLUMN_QUANTITY };
//
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        return new CursorLoader(this,
//                BookContract.ProductEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null);
//    }


    // Identifies a particular Loader being used in this component
    private static final int CURRENT_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentBookUri;
    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quantity of books in store
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the name of book's supplier
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the name of book's supplier
     */
    private EditText mSupplierPhoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing the existing book
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // IF the intent DOES NOT contain a book content Uri,
        // then we know that we're creating a new book

        if (currentBookUri == null) {
            setTitle(R.string.add_new_book);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(CURRENT_BOOK_LOADER, null, this);

        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
    }

    /**
     * get user input and save new book into database
     **/
    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim(); /**  .trim erase spaces**/
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneNumberOfSupplierString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentBookUri == null &&
                TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(supplierString)&& TextUtils.isEmpty(phoneNumberOfSupplierString))
        {            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        if (currentBookUri == null){
            if (TextUtils.isEmpty(nameString)
                    || TextUtils.isEmpty(quantityString)){
                Toast.makeText(this, "Please fill the required fields",
                        Toast.LENGTH_SHORT).show();
            }
        }

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);/**we want to store an integer, not a String.
             parseInt method convert the String into an Integer**/
        }
        values.put(ProductEntry.COLUMN_PRICE, price);

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);/**we want to store an integer, not a String.
             parseInt method convert the String into an Integer**/
        }

        values.put(ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phoneNumberOfSupplierString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (currentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            /** One major difference between the SQLiteDatabase insert() method
             and the ContentResolver insert() method is that one returns a row ID,
             while the other returns a Uri, respectively.*/
            // Insert a new row for book in the database, returning the ID of that new row.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI:
            // currentBookUri and pass in the new ContentValues.
            // Pass in null for the selection and selection args because currentBookUri
            // will already identify the correct row in the database that we want to modify.
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);
            //Now this returns a number of updated rows.
            // To display whether the update was successful or not,
            // we can check the number of updated rows.
            // If it was 0, then the update was not successful,
            // and we can show an error toast. Otherwise we show a success toast.

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //set book to database
                saveBook();
                //Exit activity and return to the CatalogActivity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
