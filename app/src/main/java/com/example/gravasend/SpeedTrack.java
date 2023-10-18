package com.example.gravasend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpeedTrack extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference speedRef;
    private double totalSpeed = 0;
    private double maxSpeed = 0; // New variable to store max speed
    private int speedCount = 0;
    private TextView averageSpeedTextView;
    private TextView maxSpeedTextView; // TextView for displaying maximum speed
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtracker);

        averageSpeedTextView = findViewById(R.id.averageSpeedTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView); // Find the maxSpeedTextView

        auth = FirebaseAuth.getInstance();
        String userUid = auth.getCurrentUser().getUid();

        speedRef = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(userUid);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location.hasSpeed()) {
                        double currentSpeed = location.getSpeed() * 3.6; // Convert m/s to KM/s
                        totalSpeed += currentSpeed;
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;

                        // Check if the current speed exceeds 80 km/h
                        if (currentSpeed > 80.0) {
                            // Store an error in the database
                            speedRef.child("speed_errors").push().setValue("Speed exceeded 80 km/h");
                        }

                        // Update the maximum speed if necessary
                        if (currentSpeed > maxSpeed) {
                            maxSpeed = currentSpeed;
                            speedRef.child("max_speed").setValue(maxSpeed);
                        }

                        // Update the user's average speed in the database
                        speedRef.child("average_speed").setValue(averageSpeed);

                        updateAverageSpeedUI(averageSpeed);
                        updateMaxSpeedUI(maxSpeed);
                    }
                }
            }
        };

        fusedLocationClient = new FusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateAverageSpeedUI(double averageSpeed) {
        averageSpeedTextView.setText("Average Speed: " + averageSpeed + " KM/s");
    }

    // Method to update the maximum speed TextView
    private void updateMaxSpeedUI(double maxSpeed) {
        maxSpeedTextView.setText("Maximum Speed: " + maxSpeed + " KM/s");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start tracking speed
            } else {
                Log.e("SpeedTracker", "Location permission denied.");
            }
        }
    }
}
