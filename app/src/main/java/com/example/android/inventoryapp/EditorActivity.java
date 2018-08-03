package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Content URI for the existing items(null if its a new items)
     */

    private Uri mCurrentItemUri;

    /**
     * EditText field to enter the item name
     */
    private EditText mItemNameEditText;

    /**
     * EditText field to enter the price for the Item
     */
    private EditText mItemPriceEditText;

    /**
     * EditText field to enter the Quantity for the Item
     */
    private EditText mItemQuantityEditText;

    /**
     * EditText field to enter the Supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier phone number
     */

    private EditText mSupplierPhoneNumberEditText;

    /**
     * Boolean flag that keeps track of whether the Items have been edited (true) or not (false)
     */

    private boolean mItemHasChanged = false;

    /**
     * int for given quantity
     */
    private int givenQuantity;

    //OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity,
        // in order to figure out if we are creating a new Items product or editing an existing one.

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product_items));
            //Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_existing_product_items));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mItemNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mItemPriceEditText = (EditText) findViewById(R.id.edit_price_field);
        mItemQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name_text_field);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_phone_text_field);

        ImageButton mIncrease = (ImageButton) findViewById(R.id.edit_quantity_increase);
        ImageButton mDecrease = (ImageButton) findViewById(R.id.edit_quantity_decrease);


        mItemNameEditText.setOnTouchListener(mTouchListener);
        mItemPriceEditText.setOnTouchListener(mTouchListener);
        mItemQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);

        //increase quantity
        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantity = mItemQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    givenQuantity = Integer.parseInt(quantity);
                    mItemQuantityEditText.setText(String.valueOf(givenQuantity + 1));
                }
            }
        });

        //decrease quantity with button

        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mItemQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    givenQuantity = Integer.parseInt(quantity);
                    // to validate if quantity is greater than =
                    if ((givenQuantity - 1) >= 0) {
                        mItemQuantityEditText.setText(String.valueOf(givenQuantity - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.editor_quantity_cant_be_less_then_0, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //setting up the phone button in the editor activity to call the supplier
        final ImageButton mPhoneCallSupplierButton = (ImageButton) findViewById(R.id.call_supplier_phone_button);

        mPhoneCallSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        // If the items editing have not changed, continue with handling back button pressed
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        //Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User clicked "Discard" button, close the current activity-
                        finish();

                    }
                };
        //Show the dialog that there are unsaved changes
        showUnsavedChangedDialog(discardButtonClickListener);
    }

    private void saveProductItem() {

        String productNameItemString = mItemNameEditText.getText().toString().trim();
        String priceItemString = mItemPriceEditText.getText().toString().trim();
        String quantityItemString = mItemQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        //Check if there is aa new item
        // and check if all the fields in the editor are blank

        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(productNameItemString) && TextUtils.isEmpty(priceItemString) &&
                TextUtils.isEmpty(quantityItemString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString))

        {
            Toast.makeText(this, getString(R.string.editor_fill_in), Toast.LENGTH_LONG).show();
            //Since no fields were modified, we can return early without creating a new items.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        if (TextUtils.isEmpty(productNameItemString)) {
            mItemNameEditText.setError(getString(R.string.editor_question_for_empty_field_name));
            return;
        }
        if (TextUtils.isEmpty(priceItemString)) {
            mItemPriceEditText.setError(getString(R.string.editor_question_for_empty_field_price));
            return;
        }

        if (TextUtils.isEmpty(quantityItemString)) {
            mItemQuantityEditText.setError(getString(R.string.editor_quantity_field_cant_be_empty));
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            mSupplierNameEditText.setError(getString(R.string.editor_question_for_empty_field_supplier_name));
            return;
        }
        if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            mSupplierPhoneNumberEditText.setError(getString(R.string.editor_question_for_empty_field_supplier_phone_number));
            return;
        }

        //Create a ContentValues object where column name are the keys,
        // and item attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PRODUCT_NAME, productNameItemString);
        values.put(ItemEntry.COLUMN_PRICE, priceItemString);
        values.put(ItemEntry.COLUMN_QUANTITY, quantityItemString);
        values.put(ItemEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        //Determine if there is a new or an existing item by checking if mCurrentItemUri is null or not
        if (mCurrentItemUri == null) {
            // this is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item -
            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            if (newUri == null) {
                //If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_items_failed), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_items_successful), Toast.LENGTH_SHORT).show();
            }

            finish();

        } else {
            //Otherwise this is an existing Item, so update the clothes with content URI: mCurrentITemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify

            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            //Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                //If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_items_failed), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_items_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_editor.xml file.
        // this add menu items to the app bar
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProductItem();

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            //Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the product clothes hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setup a dialog to warn the user.
                //Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User clicked "Discard" button, navigate to parent activity-
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }

                        };
                //Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangedDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //since the editor show all the items attributes, define a projection that contains
        // all columns from the items table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //Bail early if the cursor is null or there is less that 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //Proceed with moving to the first row of the cursor and reading data from it
        // This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            //Find the columns of item attributes that we ware interested in
            int itemNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(itemNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            long supplierPhoneNumber = cursor.getLong(supplierPhoneNumberColumnIndex);

            //Update the views on the screen with the values from the database
            mItemNameEditText.setText(productName);
            mItemPriceEditText.setText(Integer.toString(price));
            mItemQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(Long.toString(supplierPhoneNumber));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, clear out all the data from the input fields.
        mItemNameEditText.setText("");
        mItemPriceEditText.setText("");
        mItemQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");

    }

    /**
     * Perform the deletion of the Item product in the database
     */

    private void deleteItem() {

        //Only perform the delete if this is an existing product.
        if (mCurrentItemUri != null) {
            //Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content Uri already identifies the product that we want.

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                //If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_items_failed), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_items_successful), Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private void showDeleteConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //user clicked the "Delete Button, so delete the clothes product.
                deleteItem();

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangedDialog(
            DialogInterface.OnClickListener discardButtonClickListener
    ) {
        //Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative button on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Keep editing" button, so dismiss the dialog
                //and continue editing the Items.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
