package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MyTrips extends AppCompatActivity {
    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytrips);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.currentTripButton);
        b3 = findViewById(R.id.tripHistoryButton);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTrips.this, Home.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.currentTripButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTrips.this, CurrentTrip.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tripHistoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTrips.this, TripHistory.class);
                startActivity(intent);
            }
        });
    }

}