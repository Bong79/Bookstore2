package com.example.ubom.bookstore2;


/**
 * Created by ubom on 9/21/18.
 */

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ubom.bookstore2.data.BookContract.ProductEntry;

//import android.database.data;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    // Bail early if the data is null or there is less than 1 row in the data
    //        (talk; aissur)
        if (data == null || data.getCount() < 1) {
            return;
        }
        // first move the data to it’s first item position.
        // Even though it only has one item, it st  arts at position -1.
        // Proceed with moving to the first row of the data and reading data from it
        // (This should be the only row in the data)
        if (data.moveToFirst()) {
            //Then I’ll get the data out of the data by getting the index of each data item,
            //and then using the indexes and the get methods to grab the actual integers and strings.
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the data for the given column index
            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            String phone = data.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierPhoneNumberEditText.setText(phone);
        }
    }

    BookCursorAdapter mdataAdapter;


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                ProductEntry.COLUMN_SUPPLIER_NAME

        };

        // Now create and return a dataLoader that will take care of
        // creating a data for the data being displayed.
        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }


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
     * EditText field to enter the name of book's supplier's num
     */
    private EditText mSupplierPhoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

//            public void dialPhoneNumber(String phoneNumber) {
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:" + phoneNumber));
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }

//        called when phone icon is pressed.
    Button callActionButton = (Button) findViewById(R.id.action_call);
        callActionButton.setOnClickListener(new OnClickListener() {
        public void onClick (View v){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                    mSupplierPhoneNumberEditText.getText().toString(), null));
            startActivity(intent);
            finish();
        }
    });

    //called when decrease button is clicked.
    Button decreaseBookQuantityButton = (Button) findViewById(R.id.action_decrease);
        decreaseBookQuantityButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
            int bookQuantity = 0;
            String bookQuantityString = mBookQuantityEditText.getText().toString();
            if(!TextUtils.isEmpty(bookQuantityString)) {
                bookQuantity  = Integer.parseInt(bookQuantityString);
                if(bookQuantity > 0) {
                    //update the flag to true, as the bookQuantity data is changed.
                    mBookHasChanged = true;
                    bookQuantity = bookQuantity - 1;
                    mBookQuantityEditText.setText(Integer.toString(bookQuantity));
                }else{
                    mBookQuantityEditText.setText(Integer.toString(bookQuantity));
                }
            }
        }
    });

    //called when increase button is clicked.
    Button increaseBookQuantityButton = (Button) findViewById(R.id.action_increase);
        increaseBookQuantityButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
            //update the flag to true, as the bookQuantity data is changed.
            mBookHasChanged = true;
            int bookQuantity = 0;
            String bookQuantityString = mBookQuantityEditText.getText().toString();
            if(!TextUtils.isEmpty(bookQuantityString)) {
                bookQuantity  = Integer.parseInt(bookQuantityString);
                bookQuantity = bookQuantity + 1;
                mBookQuantityEditText.setText(Integer.toString(bookQuantity));
            } else if(currentBookUri == null){
                bookQuantity = bookQuantity + 1;
                mBookQuantityEditText.setText(Integer.toString(bookQuantity));
            }
        }
    });


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

            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) ||
                    TextUtils.isEmpty(priceString) ||TextUtils.isEmpty(supplierString) ||
                    TextUtils.isEmpty(phoneNumberOfSupplierString) ){

                Toast.makeText(this, "Please fill the required fields",
                        Toast.LENGTH_SHORT).show();
                return;
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
        }       finish();
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

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

