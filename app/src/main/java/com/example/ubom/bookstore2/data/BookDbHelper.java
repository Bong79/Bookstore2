package com.example.ubom.bookstore2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ubom.bookstore2.data.BookContract.ProductEntry;


/**
 * Created by ubom on 9/14/18.
 */

public class BookDbHelper extends SQLiteOpenHelper {


    /**
     * Database helper for Pets app. Manages database creation and version management.
     */

        public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

        /** Name of the database file */
        private static final String DATABASE_NAME = "bookstore.db";

        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private static final int DATABASE_VERSION = 1;

        /**
         * Constructs a new instance of {@link BookDbHelper}.
         *
         * @param context of the app
         */
        public BookDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * This is called when the database is created for the first time.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create a String that contains the SQL statement to create the pets table
            String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ( "
                    + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                    + ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                    + ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                    + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL, "
                    + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                    + ProductEntry.COLUMN_AUTHOR + " TEXT NON NULL);";

            // Execute the SQL statement
            db.execSQL(SQL_CREATE_BOOKS_TABLE);
        }

        /**
         * This is called when the database needs to be upgraded.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // The database is still at version 1, so there's nothing to do be done here.
        }
    }

