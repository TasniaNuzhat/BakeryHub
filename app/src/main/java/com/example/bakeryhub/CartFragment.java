package com.example.bakeryhub;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private CartAdapter cartAdapter;
    private ListView cartListView;
    private TextView totalPriceTextView;
    private Button btnCheckOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        cartListView = rootView.findViewById(R.id.cart_list_view);
        totalPriceTextView = rootView.findViewById(R.id.cart_total_price);
        btnCheckOut = rootView.findViewById(R.id.checkout_button);

        loadCartItems();

        // Set up the checkout button listener
        btnCheckOut.setOnClickListener(v -> {
            if (cartAdapter == null || cartAdapter.getCount() == 0) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent intent = new Intent(requireContext(), OrderList.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void loadCartItems() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getCartItems();

            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(getContext(), "No items in the cart", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<CartItem> cartItems = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

                cartItems.add(new CartItem(id, name, price, quantity, image));
            }


            cartAdapter = new CartAdapter(requireContext(), cartItems, dbHelper, totalPriceTextView);
            cartListView.setAdapter(cartAdapter);

            updateTotalPrice();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void updateTotalPrice() {
        double total = dbHelper.getCartTotal();
        if (totalPriceTextView != null) {
            totalPriceTextView.setText("Total: Tk" + String.format("%.2f", total));
        }
    }
}