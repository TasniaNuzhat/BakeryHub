package com.example.bakeryhub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private int progress;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TextView welcomeText = findViewById(R.id.welcomeText);
        SpannableStringBuilder spannable = new SpannableStringBuilder("Welcome to BAKERYHUB");
        spannable.setSpan(
                new ForegroundColorSpan(Color.RED),
                11,
                spannable.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        welcomeText.setText(spannable);

        progressBar = findViewById(R.id.progressBarId);


        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                navigateToNextPage();
            }
        }).start();
    }


    public void doWork() {
        for (progress = 20; progress <= 100; progress += 20) {
            try {
                Thread.sleep(1000);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void navigateToNextPage() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
