package com.example.gravasend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference speedRef;
    private DatabaseReference locationRef;
    private double totalSpeed = 0;
    private double maxSpeed = 0;
    private double previousSpeed = -1;
    private int harshBrakingCount = 0;
    private int suddenAccelerationCount = 0;
    private int speedCount = 0;
    private TextView averageSpeedTextView;
    private TextView maxSpeedTextView;
    private TextView harshBrakingCountTextView;
    private TextView suddenAccelerationCountTextView;
    private TextView currentSpeedTextView;
    private TextView locationText;
    private FirebaseAuth auth;
    private Geocoder geocoder;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedtracker);

        averageSpeedTextView = findViewById(R.id.averageSpeedTextView);
        maxSpeedTextView = findViewById(R.id.maxSpeedTextView);
        currentSpeedTextView = findViewById(R.id.currentSpeedTextView);
        harshBrakingCountTextView = findViewById(R.id.harshBrakingCountTextView);
        suddenAccelerationCountTextView = findViewById(R.id.suddenAccelerationCountTextView);
        locationText = findViewById(R.id.addressTextView);

        auth = FirebaseAuth.getInstance();
        String userUid = auth.getCurrentUser().getUid();

        speedRef = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(userUid);
        locationRef = FirebaseDatabase.getInstance().getReference("locations").child(userUid);

        geocoder = new Geocoder(this, Locale.getDefault());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationTracking();
        }
    }

    private void startLocationTracking() {
        // Configure Location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10 seconds
                .setFastestInterval(5000); // 5 seconds

        // Initialize Location callback
        locationCallback = new LocationCallback() {
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
                                updateHarshBrakingUI(harshBrakingCount);
                            }

                            if (currentSpeed - previousSpeed > 5) {
                                suddenAccelerationCount++;
                                speedRef.child("sudden_acceleration_count").setValue(suddenAccelerationCount);
                                updateSuddenAccelerationUI(suddenAccelerationCount);
                            }
                        }

                        previousSpeed = currentSpeed;

                        totalSpeed += currentSpeed;
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;
                        averageSpeed = Math.round(averageSpeed * 100.0) / 100.0;

                        if (currentSpeed > 100.0) {
                            speedRef.child("speed_errors").push().setValue("Speed exceeded 100 km/h");
                        }

                        if (currentSpeed > maxSpeed) {
                            maxSpeed = currentSpeed;
                            speedRef.child("max_speed").setValue(maxSpeed);
                        }

                        speedRef.child("average_speed").setValue(averageSpeed);
                        speedRef.child("current_speed").setValue(currentSpeed);

                        updateAverageSpeedUI(averageSpeed);
                        updateMaxSpeedUI(maxSpeed);
                        updateCurrentSpeedUI(currentSpeed);
                    }

                    // Update the location information
                    updateLocationInfo(location);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateAverageSpeedUI(double averageSpeed) {
        averageSpeedTextView.setText("Average Speed: " + averageSpeed + " KM/h");
    }

    private void updateMaxSpeedUI(double maxSpeed) {
        maxSpeedTextView.setText("Maximum Speed: " + maxSpeed + " KM/h");
    }

    private void updateCurrentSpeedUI(double currentSpeed) {
        String formattedSpeed = String.format("%.2f", currentSpeed);
        currentSpeedTextView.setText("Current Speed: " + formattedSpeed + " KM/h");
    }

    private void updateHarshBrakingUI(int count) {
        harshBrakingCountTextView.setText("Harsh Braking Count: " + count);
    }

    private void updateSuddenAccelerationUI(int count) {
        suddenAccelerationCountTextView.setText("Sudden Acceleration Count: " + count);
    }

    private void updateLocationInfo(android.location.Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                StringBuilder addressStringBuilder = new StringBuilder();
                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    addressStringBuilder.append(addresses.get(0).getAddressLine(i)).append("\n");
                }
                locationText.append("\nAddress: " + addressStringBuilder.toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationRef.setValue(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationTracking();
            } else {
                Toast.makeText(this, "Location permission denied. App cannot function.", Toast.LENGTH_SHORT).show();
                Log.e("SpeedTracker", "Location permission denied.");
            }
        }
    }
}
