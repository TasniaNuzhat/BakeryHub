package com.example.bakeryhub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InsertProductActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private EditText productNameEditText;
    private EditText productPriceEditText;

    private ImageView selectedImageView;
    private Button selectImageButton;
    private Button insertProductButton;
    private DatabaseHelper databaseHelper;
    private byte[] image;
    private Bitmap selectedImageBitmap;

    private ActivityResultLauncher<Intent> imagePickerLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        productNameEditText = findViewById(R.id.et_product_name);
        productPriceEditText = findViewById(R.id.et_product_price);
        selectedImageView = findViewById(R.id.iv_selected_image);
        selectImageButton = findViewById(R.id.btn_select_image);
        insertProductButton = findViewById(R.id.btn_insert_product);




        databaseHelper = new DatabaseHelper(this);




        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    selectedImageView.setImageBitmap(selectedImageBitmap);
                    image = bitmapToByteArray(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        selectImageButton.setOnClickListener(view -> showImageSelectionDialog());

        insertProductButton.setOnClickListener(view -> insertProduct());
    }

    private void showImageSelectionDialog() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        imagePickerLauncher.launch(pickIntent);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void insertProduct() {
        String name = productNameEditText.getText().toString().trim();
        String price = productPriceEditText.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || image == null) {
            Toast.makeText(this, "Please fill all the fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double prices;
        try {
            prices = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInsertedProduct = databaseHelper.insertProduct(name, prices, image);
        if (isInsertedProduct) {
            Toast.makeText(this, "Product inserted successfully!", Toast.LENGTH_SHORT).show();

            productNameEditText.setText("");
            productPriceEditText.setText("");
            selectedImageView.setImageResource(android.R.color.transparent);
            image = null;
        } else {
            Toast.makeText(this, "Product not inserted!", Toast.LENGTH_SHORT).show();
        }
    }

}