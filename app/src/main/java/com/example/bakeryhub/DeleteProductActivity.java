package com.example.bakeryhub;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private TextView textViewProductPrice;
    private TextView textViewProductQuantity;
    private ImageView imageViewProduct;
    private Button buttonDelete;
    private Button buttonSelectImage;
    private Button buttonSearch;
    private TextView textViewProductId;

    private DatabaseHelper databaseHelper;
    private byte[] productImageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        editTextName = findViewById(R.id.text_view_product_name);
        textViewProductPrice = findViewById(R.id.text_view_product_price);

        textViewProductId = findViewById(R.id.text_view_product_id);
        imageViewProduct = findViewById(R.id.image_view_product);
        buttonDelete = findViewById(R.id.button_delete);

        buttonSearch = findViewById(R.id.button_search);


        databaseHelper = new DatabaseHelper(this);

        buttonSearch.setOnClickListener(view -> searchProduct());
        buttonDelete.setOnClickListener(view -> deleteProduct());
    }

    private void searchProduct() {
        String productName = editTextName.getText().toString().trim();
        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter a product name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = databaseHelper.getProductByName(productName);
        if (cursor != null && cursor.moveToFirst()) {
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));

            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE));

            textViewProductPrice.setText(String.valueOf(price));

            textViewProductId.setText("Product ID: " + productId);

            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageViewProduct.setImageBitmap(bitmap);
                productImageByteArray = image;
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        String productName = editTextName.getText().toString().trim();

        databaseHelper.deleteProduct(productName);
        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
    }
}