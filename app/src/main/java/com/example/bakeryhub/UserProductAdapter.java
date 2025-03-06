package com.example.bakeryhub;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakeryhub.DatabaseHelper;
import com.example.bakeryhub.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> productList;
    private DatabaseHelper dbHelper;
    private FirebaseAuth auth;

    public UserProductAdapter(Context context, ArrayList<Product> productList, DatabaseHelper dbHelper) {
        this.context = context;
        this.productList = new ArrayList<>(productList);
        this.dbHelper = dbHelper;
        this.auth=FirebaseAuth.getInstance();
    }



    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_product_section, parent, false);
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {

            Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        int transactionId = (int) (System.currentTimeMillis() / 1000);
        String userId = user.getUid();

        Product product = productList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.user_product_name);
        TextView priceTextView = convertView.findViewById(R.id.user_product_price);
        ImageView productImageView = convertView.findViewById(R.id.user_product_image);
        Button addToCartButton = convertView.findViewById(R.id.user_add_to_cart_button);

        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("Tk %.2f", product.getPrice()));


        byte[] imageBytes = product.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            productImageView.setImageBitmap(bitmap);
        }

        addToCartButton.setOnClickListener(v -> {
            dbHelper.addToCart(product.getId(), 1, userId);
            Toast.makeText(context, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }


    public void updateList(ArrayList<Product> updatedList) {
        productList.clear();
        productList.addAll(updatedList);
        notifyDataSetChanged();
    }
}
