package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class ItemContract {

    private ItemContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // The uri can go 2 paths later. This path is for the whole table items
    public static final String PATH_ITEMS = "items";

    public static final class ItemEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "Items";
        //Creating the constants
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_PRODUCT_NAME = "name";

        public static final String COLUMN_PRICE = "price";

        public static final String COLUMN_QUANTITY = "quantity";

        public static final String COLUMN_SUPPLIER_NAME = "supplier";

        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";

        // this must be inside the ItemEntry class Creates an Uri with BASE_CONTENT_URI, PATH_ITEMS.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;


    }
}