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
    private double maxSpeed = 0;
    private double previousSpeed = -1;
    private int harshBrakingCount = 0;
    private int suddenAccelerationCount = 0;
    private int speedCount = 0;
    private TextView averageSpeedTextView;
    private TextView maxSpeedTextView;
    private TextView harshBrakingTextView; // TextView for displaying harsh braking incidents
    private TextView suddenAccelerationTextView; // TextView for displaying sudden acceleration incidents
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtracker);

        averageSpeedTextView = findViewById(R.id.averageSpeedTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView);
        harshBrakingTextView = findViewById(R.id.harshBrakingTextView); // Find the harshBrakingTextView
        suddenAccelerationTextView = findViewById(R.id.suddenAccelerationTextView); // Find the suddenAccelerationTextView

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
                        double currentSpeed = location.getSpeed() * 3.6;

                        if (previousSpeed != -1) {
                            if (previousSpeed - currentSpeed > 5) {
                                harshBrakingCount++;
                                speedRef.child("harsh_braking_count").setValue(harshBrakingCount);
                                updateHarshBrakingUI(harshBrakingCount); // Update the UI
                            }

                            if (currentSpeed - previousSpeed > 5) {
                                suddenAccelerationCount++;
                                speedRef.child("sudden_acceleration_count").setValue(suddenAccelerationCount);
                                updateSuddenAccelerationUI(suddenAccelerationCount); // Update the UI
                            }
                        }

                        previousSpeed = currentSpeed;

                        totalSpeed += currentSpeed;
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;

                        if (currentSpeed > 100.0) {
                            speedRef.child("speed_errors").push().setValue("Speed exceeded 100 km/h");
                        }

                        if (currentSpeed > maxSpeed) {
                            maxSpeed = currentSpeed;
                            speedRef.child("max_speed").setValue(maxSpeed);
                        }

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

    private void updateMaxSpeedUI(double maxSpeed) {
        maxSpeedTextView.setText("Maximum Speed: " + maxSpeed + " KM/s");
    }

    // Method to update the harsh braking TextView
    private void updateHarshBrakingUI(int count) {
        harshBrakingTextView.setText("Harsh Braking Incidents: " + count);
    }

    // Method to update the sudden acceleration TextView
    private void updateSuddenAccelerationUI(int count) {
        suddenAccelerationTextView.setText("Sudden Acceleration Incidents: " + count);
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
