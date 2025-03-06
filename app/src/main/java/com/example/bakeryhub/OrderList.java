package com.example.bakeryhub;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends AppCompatActivity {

    private RadioGroup paymentRadioGroup;
    private Button confirmOrderButton;
    private double deliveryChargeValue = 20.0;
    private double totalValue;
    private List<CartItem> cartItems;
    private DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);


        dbhelper = new DatabaseHelper(this);


        paymentRadioGroup = findViewById(R.id.payment_radio_group);
        confirmOrderButton = findViewById(R.id.confirm_order_button);


        cartItems = loadCartItemsFromDatabase();


        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        confirmOrderButton.setOnClickListener(v -> {

            double subtotalValue = calculateSubtotal();
            totalValue = subtotalValue + deliveryChargeValue;


            int selectedPaymentId = paymentRadioGroup.getCheckedRadioButtonId();
            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = getPaymentMethod(selectedPaymentId);


            showConfirmationDialog(subtotalValue, paymentMethod);
        });
    }

    private List<CartItem> loadCartItemsFromDatabase() {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getCartItems();
        List<CartItem> cartItems = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));


                CartItem item = new CartItem(id, name, price, quantity, image);
                cartItems.add(item);


                Log.d("OrderListActivity", "Loaded item: " + name + ", Quantity: " + quantity);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return cartItems;
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        return subtotal;
    }

    private String getPaymentMethod(int selectedPaymentId) {
        RadioButton selectedButton = findViewById(selectedPaymentId);
        return selectedButton.getText().toString();
    }

    private void showConfirmationDialog(double subtotalValue, String paymentMethod) {
        StringBuilder message = new StringBuilder();


        message.append("Items Ordered:\n");
        for (CartItem item : cartItems) {
            message.append(item.getName())
                    .append(" - ")
                    .append(item.getQuantity())
                    .append(" x Tk")
                    .append(item.getPrice())
                    .append("\n");
        }


        message.append("\nSubtotal: Tk").append(subtotalValue)
                .append("\nDelivery Charge: Tk").append(deliveryChargeValue)
                .append("\nTotal: Tk").append(totalValue)
                .append("\nPayment Method: ").append(paymentMethod);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Confirmation");
        builder.setMessage(message.toString());

        builder.setPositiveButton("Confirm", (dialog, which) -> {

            dbhelper.clearCart();


            if (cartItems != null) {
                cartItems.clear();
            }


            Toast.makeText(this, "Order confirmed!", Toast.LENGTH_SHORT).show();


            finish();


        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}