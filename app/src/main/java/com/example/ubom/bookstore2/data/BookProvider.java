package com.example.ubom.bookstore2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.ubom.bookstore2.data.BookContract.ProductEntry;


/**
 * Created by ubom on 10/9/18.
 */

    public class BookProvider extends ContentProvider {

        /** Tag for the log messages */
        public static final String LOG_TAG = BookProvider.class.getSimpleName();


        /** URI matcher code for the content URI for the book_details table */
        private static final int BOOKS = 100;

        /** URI matcher code for the content URI for the book_details table */
        private static final int BOOK_ID = 101;

        // Creates a UriMatcher object.
        private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        static {
            uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.ProductEntry.PATH_BOOKS_DETAILS, BOOKS);
            uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.ProductEntry.PATH_BOOKS_DETAILS + "/#", BOOK_ID);
        }

       private BookDbHelper bookDbHelper;

        /**
         * Initialize the provider and the database helper object.
         */
        @Override
        public boolean onCreate() {
            //  Create and initialize the global variable for  BookStoreDBHelper object to gain access to the books database.
              bookDbHelper = new BookDbHelper(getContext());
            return true;
        }

        /**
         * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
         */
        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            // Get readable database
            SQLiteDatabase database = bookDbHelper.getReadableDatabase();

            // This cursor will hold the result of the query
            Cursor cursor = null;

            // Figure out if the URI matcher can match the URI to a specific code
            int match = uriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    // For the BOOKS code, query the BOOKS_DETAILS table directly with the given
                    // projection, selection, selection arguments, and sort order. The cursor
                    // could contain multiple rows of the books table.

                    cursor = database.query(ProductEntry.TABLE_NAME, projection, null, null,
                            null, null, sortOrder);
                    break;
                case BOOK_ID:
                    // For the BOOK_ID code, extract out the ID from the URI.
                    // For an example URI such as "content://com.example.android.books/books/3",
                    // the selection will be "_id=?" and the selection argument will be a
                    // String array containing the actual ID of 3 in this case.
                    //
                    // For every "?" in the selection, we need to have an element in the selection
                    // arguments that will fill in the "?". Since we have 1 question mark in the
                    // selection, we have 1 String in the selection arguments' String array.
                    selection = ProductEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                    // This will perform a query on the books table where the _id equals 3 to return a
                    // Cursor containing that row of the table.
                    cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }

            cursor.setNotificationUri(getContext().getContentResolver(),uri);

            return cursor;
        }

        /**
         * Insert new data into the provider with the given ContentValues.
         */
        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            int match = uriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return insertBook(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        /**
         * Insert a book into the database with the given content values. Return the new content URI
         * for that specific row in the database.
         */
        private Uri insertBook(Uri uri, ContentValues values) {

            /** Sanity checking.**/
            // Check that the name of the book is not null
            String p_name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (p_name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }

            /** Sanity checking.**/
            // If the price is provided, check that it's greater than or equal to $0
            // If the price is null, that’s fine
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
            /** Sanity checking.**/
            // check that quantity is not null and that it is greater than or equal to 0 books.
            Integer amt = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
            if (amt == null || amt < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
            /** Sanity checking.**/
            // Check that the supplier's name is not null.
            String s_name = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (s_name == null) {
                throw new IllegalArgumentException("Book requires a supplier's name");
            }
            /** Sanity checking.**/
            // Check that the supplier's phone is not null.
            String s_phone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (s_phone == null) {
                throw new IllegalArgumentException("Book requires a supplier's ph. number");
            }

            // Get writeable database
            SQLiteDatabase database = bookDbHelper.getWritableDatabase();
            // Insert the new book with the given values
            long id = database.insert(BookContract.ProductEntry.TABLE_NAME, null, values);
            // If the ID is -1, then the insertion failed. Log an error and return null.
            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }
            getContext().getContentResolver().notifyChange(uri,null);
            // Return the new URI with the ID (of the newly inserted row) appended at the end
            return ContentUris.withAppendedId(uri, id);
        }

        /**
         * Updates the data at the given selection and selection arguments, with the new ContentValues.
         */
        @Override
        public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
            final int match = uriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return updateBook(uri, contentValues, selection, selectionArgs);
                case BOOK_ID:
                    // For the BOOK_ID code, extract out the ID from the URI,
                    // so we know which row to update. Selection will be "_id=?" and selection
                    // arguments will be a String array containing the actual ID.
                    selection = ProductEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    return updateBook(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }




        /**
         * Update books in the database with the given content values. Apply the changes to the rows
         * specified in the selection and selection arguments (which could be 0 or 1 or more books).
         * Return the number of rows that were successfully updated.
         */
        private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            int rowsUpdated =0;
            // If the {@link  BookStoreEntry#COLUMN_PRODUCT_NAME} key is present,
            // check that the name value is not null.
            if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
                String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Book requires a name");
                }
            }

            // If the {@link  BookStoreEntry#COLUMN_SUPPLIER_NAME} key is present,
            // check that the COLUMN_SUPPLIER_NAME value is valid.
            if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
                String name = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Supplier requires a name");
                }
            }

            // If the {@link  BookStoreEntry#COLUMN_QUANTITY} key is present,
            // check that the quantity value is valid.
            if (values.containsKey(ProductEntry.COLUMN_QUANTITY)) {
                // Check that the quantity is greater than or equal to 0.
                Integer quantity = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
                if (quantity != null && quantity < 0) {
                    throw new IllegalArgumentException("Book requires valid quantity");
                }
            }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase database =  bookDbHelper.getWritableDatabase();
            // Returns the number of database rows affected by the update statement

//            rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

            // Perform the update on the database and get the number of rows affected
            rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            // Return the number of rows updated
            return rowsUpdated;
        }

        /**
         * Delete the data at the given selection and selection arguments.
         */
        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            // Get writeable database
            SQLiteDatabase database = bookDbHelper.getWritableDatabase();

            final int match = uriMatcher.match(uri);
            int rowsDeleted = 0;
            switch (match) {
                case BOOKS:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = database.delete( ProductEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case BOOK_ID:
                    // Delete a single row given by the ID in the URI
                    selection =  ProductEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = database.delete( ProductEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);

            }
            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        }
        /**
         * Returns the MIME type of data for the content URI.
         */
        @Override
        public String getType(Uri uri) {
            final int match = uriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return  ProductEntry.CONTENT_LIST_TYPE;
                case BOOK_ID:
                    return  ProductEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }
    }

