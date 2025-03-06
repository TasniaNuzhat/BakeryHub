package com.example.bakeryhub;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private TextView tvRatingBar;
    private RatingBar ratingBar;
    private Button logoutButton;
    private Dialog dialog;
    private Button btnDialogCancel, btnDialogLogout;

    private static final String PREF_NAME = "UserSession";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        tvRatingBar = view.findViewById(R.id.ratingBarTitle);
        ratingBar = view.findViewById(R.id.ratingBar);
        logoutButton = view.findViewById(R.id.btn_logout);


        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_background));
        dialog.setCancelable(false);


        btnDialogLogout = dialog.findViewById(R.id.dialog_logout_button);
        btnDialogCancel = dialog.findViewById(R.id.dialog_cancel_button);

        btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        btnDialogLogout.setOnClickListener(v -> {
            logoutUser();
            dialog.dismiss();
        });



        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String ratingText = "You rated: " + rating + " stars!";
            tvRatingBar.setText(ratingText);
        });


        logoutButton.setOnClickListener(v -> dialog.show());

        return view;
    }

    private void logoutUser() {
        // Clear user session and last screen from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved data (session)
        editor.apply();

        // Sign out the user from Firebase
        FirebaseAuth.getInstance().signOut();

        // Redirect to login screen (MainActivity) and clear the back stack
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Close the current activity
        requireActivity().finish();

        Toast.makeText(requireContext(), "Logout Successful!", Toast.LENGTH_SHORT).show();
    }

}
