package com.example.bakeryhub;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;

public class ProductsDisplay extends AppCompatActivity {


    private HashMap<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);


        fragmentMap.put(R.id.navbarHome, new HomeFragment());
        fragmentMap.put(R.id.navbarCart, new CartFragment());
        fragmentMap.put(R.id.navbarProfile, new ProfileFragment());


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                Fragment selectedFragment = fragmentMap.get(item.getItemId());


                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });


        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navbarHome);
        }
    }
}
