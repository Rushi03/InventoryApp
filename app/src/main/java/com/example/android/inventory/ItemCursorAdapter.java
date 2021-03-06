package com.example.android.inventory;

import android.app.Activity;
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

import com.example.android.inventory.ItemContract.ItemEntry;

public class ItemCursorAdapter extends CursorAdapter {

    private Context mContext;
    private ContentValues values;

    public ItemCursorAdapter(Activity context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView item = (TextView) view.findViewById(R.id.product);
        TextView price = (TextView) view.findViewById(R.id.price);
        final TextView quantity = (TextView) view.findViewById(R.id.product_quantity);

        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemEntry._ID));

        item.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.PRODUCT)));
        price.setText("$" + cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.PRICE)));
        quantity.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.QUANTITY)));

        Button itemSale = (Button) view.findViewById(R.id.sold);
        itemSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] selectionArgs = new String[]{Long.toString(id)};
                Cursor queryResult;
                queryResult = mContext.getContentResolver().query(
                        ItemEntry.CONTENT_URI,
                        null,
                        "_ID=?",
                        selectionArgs,
                        null
                );

                queryResult.moveToFirst();
                int qty = Integer.parseInt(queryResult.getString
                        (queryResult.getColumnIndexOrThrow(ItemEntry.QUANTITY)));
                queryResult.close();

                if (qty > 0) {
                    qty--;
                }

                quantity.setText(String.valueOf(qty));

                values = new ContentValues();
                values.put(ItemEntry.QUANTITY, String.valueOf(qty));
                Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                mContext.getContentResolver().update(uri, values, null, null);
            }
        });
    }
}
