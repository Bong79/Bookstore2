package com.example.ubom.bookstore2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ubom.bookstore2.data.BookContract;
import com.example.ubom.bookstore2.data.BookDbHelper;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final int BOOK_LOADER = 0;
  BookCursorAdapter mCursorAdapter;


  private BookDbHelper mDbHelper;

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mCursorAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);

  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    String[] projection = {
            BookContract.ProductEntry._ID,
            BookContract.ProductEntry.COLUMN_PRODUCT_NAME,
            BookContract.ProductEntry.COLUMN_PRICE,
            BookContract.ProductEntry.COLUMN_QUANTITY };


    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new CursorLoader(this,
            BookContract.ProductEntry.CONTENT_URI,
            projection,
            null,
            null,
            null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

     /*
      * These methods are called when the add and subtract button is clicked.
            */


    // Setup FAB to open EditorActivity
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        startActivity(intent);
      }
    });

    mDbHelper = new BookDbHelper(this);


    ListView booksListView = (ListView) findViewById(R.id.list);

    // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
    View emptyView = findViewById(R.id.empty_view);
    booksListView.setEmptyView(emptyView);

    // Setup an Adapter to create a list item for each row of book data in the Cursor.
    mCursorAdapter = new BookCursorAdapter(this, null);
    // Attach the adapter to the ListView.
    booksListView.setAdapter(mCursorAdapter);

    booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

    {

      @Override
      public void onItemClick (AdapterView < ? > adapterView, View view,int position, long id){
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        Uri selectedBookUri = ContentUris.withAppendedId(BookContract.ProductEntry.CONTENT_URI, id);
        intent.setData(selectedBookUri);
        startActivity(intent);
      }
    });

    getLoaderManager().initLoader(BOOK_LOADER,null,this);

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
    values.put(BookContract.ProductEntry.COLUMN_PRODUCT_NAME, getString(R.string.prod_name));
    values.put(BookContract.ProductEntry.COLUMN_PRICE, getString(R.string.col_price));
    values.put(BookContract.ProductEntry.COLUMN_QUANTITY, getString(R.string.col_quantity));
    values.put(BookContract.ProductEntry.COLUMN_SUPPLIER_NAME, getString(R.string.sup_name));
    values.put(BookContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, getString(R.string.sup_phone));

    // Insert a new row for Toto into the provider using the ContentResolver.
    // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
    // into the books database table.
    // Receive the new content URI that will allow us to access Toto's data in the future.


    // Insert a new row for Toto in the database, returning the ID of that new row.
    // The first argument for db.insert() is the books table name.
    // The second argument provides the name of a column in which the framework
    // can insert NULL in the event that the ContentValues is empty (if
    // this is set to "null", then the framework will not insert a row when
    // there are no values).
    // The third argument is the ContentValues object containing the info for Toto.

    Uri newUri = getContentResolver().insert(BookContract.ProductEntry.CONTENT_URI, values);
    }
  /**
   * Helper method to delete all book details data into the database.
   */
  private void deleteAll() {
    int rowsDeleted =0;
    rowsDeleted  = getContentResolver().delete(BookContract.ProductEntry.CONTENT_URI,null,null);
    if(rowsDeleted > 0){
      Toast.makeText(MainActivity.this,getString(R.string.delete_success),
              Toast.LENGTH_LONG).show();
    }else{
      Toast.makeText(MainActivity.this,getString(R.string.books_delete_failed),
              Toast.LENGTH_SHORT).show();

    }
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
        return true;
      // Respond to a click on the "Delete All data" menu option
      case R.id.action_delete_all_entries:
        deleteAll();
        return true;
      }
    return super.onOptionsItemSelected(item);
  }
}
