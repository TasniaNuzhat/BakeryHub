package com.example.bakeryhub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CartItem> cartItems;
    private DatabaseHelper dbHelper;
    private TextView totalPriceTextView;
    private boolean isReadOnly = false;

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, DatabaseHelper dbHelper, TextView totalPriceTextView) {
        this.context = context;
        this.cartItems = cartItems;
        this.dbHelper = dbHelper;
        this.totalPriceTextView = totalPriceTextView;
        updateTotalPrice();
    }


    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        final CartItem cartItem = cartItems.get(position);

        TextView nameTextView = convertView.findViewById(R.id.cart_item_name);
        TextView priceTextView = convertView.findViewById(R.id.cart_item_price);
        TextView quantityTextView = convertView.findViewById(R.id.cart_item_quantity);
        ImageView imageView = convertView.findViewById(R.id.cart_item_image);
        View incrementButton = convertView.findViewById(R.id.cart_item_increment);
        View decrementButton = convertView.findViewById(R.id.cart_item_decrement);
        ImageView deleteButton = convertView.findViewById(R.id.cart_item_delete);


        nameTextView.setText(cartItem.getName());
        priceTextView.setText("Price: Tk" + cartItem.getPrice());
        quantityTextView.setText(String.valueOf(cartItem.getQuantity()));


        byte[] imageBytes = cartItem.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }


        if (isReadOnly) {
            incrementButton.setVisibility(View.GONE);
            decrementButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {

            incrementButton.setVisibility(View.VISIBLE);
            decrementButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

            incrementButton.setOnClickListener(v -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
                dbHelper.updateCartItemQuantity(cartItem.getId(), cartItem.getQuantity());
                updateTotalPrice();
            });

            decrementButton.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
                    dbHelper.updateCartItemQuantity(cartItem.getId(), cartItem.getQuantity());
                    updateTotalPrice();
                }
            });

            deleteButton.setOnClickListener(v -> {
                dbHelper.deleteCartItem(cartItem.getId());
                cartItems.remove(position);
                updateTotalPrice();
                notifyDataSetChanged();
            });
        }

        return convertView;
    }


    private void updateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText("Total: Tk" + String.format("%.2f", total));
    }
}
