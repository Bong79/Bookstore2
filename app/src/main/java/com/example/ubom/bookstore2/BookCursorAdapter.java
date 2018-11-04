package com.example.ubom.bookstore2;

/**
 * Created by ubom on 9/28/18.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubom.bookstore2.data.BookContract;
import com.example.ubom.bookstore2.data.BookContract.ProductEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView name = (TextView) view.findViewById(R.id.name);
//        TextView author = (TextView) view.findViewById(R.id.author);
        final TextView quantity = (TextView) view.findViewById(R.id.bookQuantity);
        TextView price = (TextView) view.findViewById(R.id.price);
        // Find sell button
        Button sell = view.findViewById(R.id.sellBkButton);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
//        int authorColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_AUTHOR);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
//        String bookAuthor = cursor.getString(authorColumnIndex);
        String booksInStock = cursor.getString(quantityColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);
        String priceOfTheBook = cursor.getString(priceColumnIndex);
        final int bookPrice = cursor.getInt(priceColumnIndex);

        if (bookQuantity == 0) {
            sell.setEnabled(false);
            sell.setText("Out of stock");

        } else {
            sell.setEnabled(true);
            sell.setText(R.string.sell);
            final String id = cursor.getString(cursor.getColumnIndex(BookContract.ProductEntry._ID));

            sell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri currentBookUri = ContentUris.withAppendedId(BookContract.ProductEntry.CONTENT_URI, Long.parseLong(id));
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_QUANTITY, bookQuantity - 1);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                    swapCursor(cursor);
                    // Check if out of stock to display toast
                    if (bookQuantity == 1) {
                        Toast.makeText(context, R.string.out_of_stock_toast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Update the TextViews with the attributes for the current book
        name.setText(bookName);
//        author.setText(bookAuthor);
//        Log.d(LOG_TAG, "bindView() returned: " + quantity);

//        Since it it on line 121 and it is the setText method
//        that threw the exception on a null object (TextView), we should check to make sure that the TextView is
//        initialized and that its id matches the layout file, and the proper layout is being used.
        quantity.setText(booksInStock);
        price.setText(priceOfTheBook);
    }
}
