package com.example.bakeryhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText etUsername = findViewById(R.id.et_register_username);
        EditText etEmail = findViewById(R.id.et_register_email);
        EditText etPassword = findViewById(R.id.et_register_password);
        EditText etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        EditText etMobile = findViewById(R.id.et_register_mobile_number);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView loginText = findViewById(R.id.tv_login);


        auth = FirebaseAuth.getInstance();


        dbHelper = new DatabaseHelper(this);


        loginText.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });


        btnRegister.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String mobile = etMobile.getText().toString().trim();


            if (!checkCredentials(username, email, password, confirmPassword, mobile)) {
                return;
            }


            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                                String userId = user.getUid();
                                                storeUserInSQLite(userId, username, email, password, mobile);
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void storeUserInSQLite(String userId, String username, String email, String password, String mobile) {
        boolean isInserted = dbHelper.insertUser(userId, username, email, password, mobile);
        if (isInserted) {
            Toast.makeText(RegisterActivity.this, "User data stored in SQLite successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Failed to store user data in SQLite", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkCredentials(String username, String email, String password, String confirmPassword, String mobile) {

        Pattern phonePattern = Pattern.compile("^(\\+88)?01[2-9][0-9]{8}$");
        Pattern namePattern = Pattern.compile("[a-zA-Z._]+");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@(gmail|yahoo|outlook)\\.com$");
        Pattern passwordPattern = Pattern.compile("^.{8,}$");


        if (username.isEmpty() || username.length() < 8 || !namePattern.matcher(username).matches()) {
            showError(findViewById(R.id.et_register_username), "Username must be at least 8 characters.");
            return false;
        }


        if (email.isEmpty() || !emailPattern.matcher(email).matches()) {
            showError(findViewById(R.id.et_register_email), "Enter a valid Gmail, Yahoo, or Outlook email.");
            return false;
        }


        if (password.isEmpty() || password.length() < 8) {
            showError(findViewById(R.id.et_register_password), "Password must be at least 8 characters long.");
            return false;
        }


        if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            showError(findViewById(R.id.et_register_confirm_password), "Passwords do not match.");
            return false;
        }


        if (!phonePattern.matcher(mobile).matches()) {
            showError(findViewById(R.id.et_register_mobile_number), "Enter a valid Bangladeshi phone number.");
            return false;
        }

        return true;
    }


    private void showError(EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
}
