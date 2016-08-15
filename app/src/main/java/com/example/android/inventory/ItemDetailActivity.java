package com.example.android.inventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventory.ItemContract.ItemEntry;

import java.io.FileDescriptor;
import java.io.IOException;

public class ItemDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        String URL = ItemEntry.CONTENT_TYPE;
        Uri items = Uri.parse(URL);

        Cursor c = getContentResolver().query(items, null, null, null, null);

        String name = c.getString(c.getColumnIndexOrThrow(ItemEntry.PRODUCT));
        double cost = Double.parseDouble(c.getString(c.getColumnIndexOrThrow(ItemEntry.PRICE)));
        final int inStock = Integer.parseInt(c.getString
                (c.getColumnIndexOrThrow(ItemEntry.QUANTITY)));

        c.close();

        TextView itemName = (TextView) findViewById(R.id.product_name);
        TextView itemPrice = (TextView) findViewById(R.id.product_price);
        final TextView itemQuantity = (TextView) findViewById(R.id.product_quantity);
        ImageView itemImage = (ImageView) findViewById(R.id.product_image);

        itemName.setText(name);
        itemPrice.setText(R.string.dollar_sign + String.valueOf(cost));
        itemQuantity.setText(inStock);
        itemImage.setImageBitmap(getBitMapFromUri(ItemEntry.CONTENT_URI));

        Button increase = (Button) findViewById(R.id.increase);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = inStock + 1;
                itemQuantity.setText(qty);
            }
        });

        Button decrease = (Button) findViewById(R.id.decrease);
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inStock > 0) {
                    int qty = inStock - 1;
                    itemQuantity.setText(qty);
                }
            }
        });

        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ItemDetailActivity.this);
                alert.setMessage("Do you want to delete this item?");
                alert.setCancelable(true);


                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
                        Intent intent = new Intent(ItemDetailActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                alert.create().show();
            }
        });

        Button order = (Button) findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setType("*/*");
                email.putExtra(Intent.EXTRA_SUBJECT, "Order more of product!");
                if (email.resolveActivity(getPackageManager()) != null) {
                    startActivity(email);
                }
            }
        });
    }

    private Bitmap getBitMapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, ItemEntry.IMAGE);
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e("Failed Image Load", "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                Log.e("Error Closing PFile", "Error closing ParcelFile Descriptor", e);
            }
        }
    }
}