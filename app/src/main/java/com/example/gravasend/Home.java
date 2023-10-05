package com.example.gravasend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private Button b1;
    private Button b2;
    private Button b3;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialize views
        b1 = findViewById(R.id.truckInfoButton);
        b2 = findViewById(R.id.myTripsButton);
        b3 = findViewById(R.id.logoutButton);

        // Initialize the SignInButton and set an OnClickListener
        findViewById(R.id.truckInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, TruckInformation.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.myTripsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MyTrips.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
