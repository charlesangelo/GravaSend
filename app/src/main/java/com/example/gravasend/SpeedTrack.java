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
    private int speedCount = 0;
    private TextView averageSpeedTextView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtracker); // Make sure you have the correct XML layout file

        averageSpeedTextView = findViewById(R.id.averageSpeedTextView);

        auth = FirebaseAuth.getInstance();
        String userUid = auth.getCurrentUser().getUid(); // Get the UID of the current user

        // Construct the database reference using the user's UID under "SpeedTracker"
        speedRef = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(userUid).child("average_speed");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Update interval in milliseconds

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location.hasSpeed()) {
                        totalSpeed += location.getSpeed();
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;

                        // Update the user's average speed in the database
                        speedRef.setValue(averageSpeed);
                        updateAverageSpeedUI(averageSpeed);
                    }
                }
            }
        };

        fusedLocationClient = new FusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateAverageSpeedUI(double averageSpeed) {
        averageSpeedTextView.setText("Average Speed: " + averageSpeed + " m/s");
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
