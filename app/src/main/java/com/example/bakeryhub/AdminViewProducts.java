package com.example.bakeryhub;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminViewProducts extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_products);

        listView = findViewById(R.id.admin_list_view_products);
        databaseHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();

        loadProducts();




        productAdapter = new ProductAdapter(this, productList);
        listView.setAdapter(productAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productList.get(position);
            Intent intent = new Intent(AdminViewProducts.this, UpdateProductActivity.class);
            intent.putExtra("id", selectedProduct.getId());
            intent.putExtra("name", selectedProduct.getName());
            intent.putExtra("price", selectedProduct.getPrice());
            intent.putExtra("image", selectedProduct.getImage());
            startActivity(intent);
        });
    }

    private void loadProducts() {
        Cursor cursor = databaseHelper.getAllProducts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            double price = cursor.getDouble(2);
            byte[] imageBytes = cursor.getBlob(3);
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            productList.add(new Product(id, name, price, imageBytes));
        }
        cursor.close();
    }
}