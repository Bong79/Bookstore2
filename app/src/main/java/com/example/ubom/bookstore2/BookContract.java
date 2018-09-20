/*
  * Copyright (C) 2016 The Android Open Source Project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.example.ubom.bookstore2;

import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * Name of database table for pets
         */
        public final static String TABLE_NAME = "bookstore";

        /**
         * Unique ID number for the book (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "p_name";

        /**
         * Quantity of book
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "amt";

        /**
         * Price of the book.
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Phone number of supplier
         * Type: INTEGER
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "s_phone";

        /**
         * Name of supplier
         * Type: INTEGER
         */
        public final static String COLUMN_SUPPLIER_NAME = "s_name";

    }
}