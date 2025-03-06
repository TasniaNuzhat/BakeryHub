package com.example.bakeryhub;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private ListView productListView;
    private SearchView searchView;
    private UserProductAdapter adapter;
    private ArrayList<Product> originalProductList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        productListView = view.findViewById(R.id.list_view);
        searchView = view.findViewById(R.id.user_search);


        originalProductList = fetchProductsFromDatabase();


        adapter = new UserProductAdapter(getActivity(), originalProductList, dbHelper);
        productListView.setAdapter(adapter);


        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        return view;
    }


    private ArrayList<Product> fetchProductsFromDatabase() {
        ArrayList<Product> productList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllProducts();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("productPrice"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("productImage"));
                productList.add(new Product(id, name, price, image));
            }
            cursor.close();
        }

        return productList;
    }


    private void filterProducts(String query) {
        ArrayList<Product> filteredList = new ArrayList<>();

        if (query.isEmpty()) {

            filteredList.addAll(originalProductList);
        } else {

            for (Product product : originalProductList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }


        adapter.updateList(filteredList);
    }
}
