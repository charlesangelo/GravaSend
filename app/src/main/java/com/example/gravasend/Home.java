package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;
    private FirebaseAuth mAuth; // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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

        // Handle mapsButton click
        findViewById(R.id.mapsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the SpeedTracker activity
                Intent intent = new Intent(Home.this, SpeedTrack.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the SpeedTracker activity
                Intent intent = new Intent(Home.this, Location.class);
                startActivity(intent);
            }
        });

        // Handle logout button click
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase
                mAuth.signOut();

                // Redirect to the MainActivity (login screen)
                Intent intent = new Intent(Home.this, MainActivity.class);
                // Clear the back stack so that the user can't navigate back to the Home activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
