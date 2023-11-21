package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentTrip extends AppCompatActivity {
    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;
    private ImageButton b4;
    private ImageButton b5;
    private MaintenanceManager maintenanceManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currenttrip);
        maintenanceManager = MaintenanceManager.getInstance();
        maintenanceManager.startMaintenanceCheck(this);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.btnMyTruck);
        b3 = findViewById(R.id.btnMaintenanceHistory);
        b4 = findViewById(R.id.btnMaintenanceReminders);
        b5 = findViewById(R.id.btnInspectionRecords);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentTrip.this, MyTrips.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.btnMyTruck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentTrip.this, TripDashboard.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.btnMaintenanceHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentTrip.this, SafetyChecklist.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.btnMaintenanceReminders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentTrip.this, DocumentCheck.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.btnInspectionRecords).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentTrip.this, LoadVerification.class);
                startActivity(intent);
            }

        });
    }
}
