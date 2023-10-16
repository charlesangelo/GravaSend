package com.example.gravasend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class TruckInformation extends AppCompatActivity {
    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;
    private ImageButton b4;
    private ImageButton b5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.truckinformation);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.btnMyTruck);
        b3 = findViewById(R.id.btnMaintenanceHistory);
        b4 = findViewById(R.id.btnInspectionRecords);
        b5 = findViewById(R.id.btnUpcomingInspections);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, Home.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnMyTruck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, MyTruck.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnMaintenanceHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, MaintenanceHistory.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnMaintenanceReminders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, MaintenanceReminders.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnInspectionRecords).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, InspectionRecords.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnUpcomingInspections).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInformation.this, UpcomingInspection.class);
                startActivity(intent);
            }
        });
    }
}