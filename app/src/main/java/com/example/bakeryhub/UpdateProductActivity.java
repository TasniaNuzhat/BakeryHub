package com.example.bakeryhub;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private EditText editTextPrice;
    private ImageView imageViewProduct;
    private Button buttonUpdate;
    private Button buttonSelectImage;
    private Button buttonSearch;
    private TextView textViewProductId;

    private DatabaseHelper databaseHelper;
    private byte[] productImageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);


        editTextName = findViewById(R.id.edit_text_product_name);
        editTextPrice = findViewById(R.id.edit_text_product_price);
        imageViewProduct = findViewById(R.id.image_view_product);
        buttonUpdate = findViewById(R.id.button_update);
        buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSearch = findViewById(R.id.button_search);
        textViewProductId = findViewById(R.id.edit_text_product_id);

        databaseHelper = new DatabaseHelper(this);


        buttonSearch.setOnClickListener(view -> searchProduct());
        buttonSelectImage.setOnClickListener(view -> selectImage());
        buttonUpdate.setOnClickListener(view -> updateProduct());
    }

    private void searchProduct() {
        String productName = editTextName.getText().toString().trim();

        if (productName.isEmpty()) {
            Toast.makeText(this, "Enter a product name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = databaseHelper.getProductByName(productName);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE));

                textViewProductId.setText(String.valueOf(productId));
                editTextPrice.setText(String.valueOf(price));

                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    imageViewProduct.setImageBitmap(bitmap);
                    productImageByteArray = image;
                }
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageViewProduct.setImageBitmap(bitmap);
                productImageByteArray = getBytesFromBitmap(bitmap);


                if (productImageByteArray != null) {
                    Log.d("UpdateProductActivity", "Image byte array updated: " + productImageByteArray.length + " bytes");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void updateProduct() {
        String productName = editTextName.getText().toString().trim();
        String productPrice = editTextPrice.getText().toString().trim();

        if (productName.isEmpty() || productPrice.isEmpty() || productImageByteArray == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(productPrice);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        String productIdText = textViewProductId.getText().toString().replaceAll("\\D+", "");
        int productId;
        try {
            productId = Integer.parseInt(productIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
            return;
        }


        int rowsAffected = databaseHelper.updateProduct(productId, productName, price, productImageByteArray);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            // Refresh the image view with the new image
            imageViewProduct.setImageBitmap(BitmapFactory.decodeByteArray(productImageByteArray, 0, productImageByteArray.length));
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

}