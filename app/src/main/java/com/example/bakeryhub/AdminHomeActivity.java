package com.example.bakeryhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_home);
        Button btnInsertProduct=findViewById(R.id.btn_insert_product);
        Button btnViewProduct=findViewById(R.id.btn_view_product);
        Button btnUpdateProduct=findViewById(R.id.btn_update_product);
        Button btnDeleteProduct=findViewById(R.id.btn_delete_product);
        Button btnAdminLogout = findViewById(R.id.btn_admin_logout);


        btnInsertProduct.setOnClickListener(v->{
            Intent intent=new Intent(AdminHomeActivity.this, InsertProductActivity.class);
            startActivity(intent);
        });

        btnViewProduct.setOnClickListener(v->{
            Intent intent=new Intent(AdminHomeActivity.this, AdminViewProducts.class);
            startActivity(intent);
        });
        btnUpdateProduct.setOnClickListener(v->{
            Intent intent=new Intent(AdminHomeActivity.this, UpdateProductActivity.class);
          startActivity(intent);
        });
        btnDeleteProduct.setOnClickListener(v->{
            Intent intent=new Intent(AdminHomeActivity.this, DeleteProductActivity.class);
            startActivity(intent);
        });

        btnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();


                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


                finish();
            }
        });
    }
}