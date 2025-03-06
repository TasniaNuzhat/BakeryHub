package com.example.bakeryhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_LAST_SCREEN = "lastScreen";

    private EditText etLoginMail, etLoginPassword;
    private Button btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lastScreen = sharedPreferences.getString(KEY_LAST_SCREEN, null);

        if (lastScreen != null) {
            navigateToLastScreen(lastScreen);
            return;
        }


        setContentView(R.layout.activity_main);


        etLoginMail = findViewById(R.id.et_login_mail);
        etLoginPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        TextView signInText = findViewById(R.id.signInText);


        auth = FirebaseAuth.getInstance();


        signInText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        btnLogin.setOnClickListener(v -> {
            String mail = etLoginMail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();


            if (mail.isEmpty()) {
                etLoginMail.setError("Please enter your email");
                return;
            }

            if (password.isEmpty()) {
                etLoginPassword.setError("Please enter your password");
                return;
            }


            if (mail.equals("admin@gmail.com") && password.equals("admin")) {
                saveLastScreen("AdminHomeActivity");
                Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {

                auth.signInWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                saveLastScreen("ProductsDisplay");
                                Intent intent = new Intent(MainActivity.this, ProductsDisplay.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private void saveLastScreen(String screenName) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_SCREEN, screenName);
        editor.apply();
    }


    private void navigateToLastScreen(String screenName) {
        Intent intent;
        switch (screenName) {
            case "AdminHomeActivity":
                intent = new Intent(this, AdminHomeActivity.class);
                break;
            case "ProductsDisplay":
                intent = new Intent(this, ProductsDisplay.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
