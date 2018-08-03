package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "items.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ItemDbHelper}.
     *
     * @param context of the app
     */
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the items table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " ("

                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PRICE + " REAL NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
