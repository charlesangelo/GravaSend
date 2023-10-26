package com.example.gravasend;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
    private double maxSpeed = 0; // Added maxSpeed variable
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
        setContentView(R.layout.speedtracker); // Make sure your XML file name matches

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
                        double currentSpeedMps = location.getSpeed(); // Speed in m/s
                        double currentSpeedKph = currentSpeedMps * 3.6; // Convert to km/h

                        if (previousSpeed != -1) {
                            if (previousSpeed - currentSpeedKph > 5) {
                                harshBrakingCount++;
                                speedRef.child("harsh_braking_count").setValue(harshBrakingCount);
                                updateHarshBrakingUI(harshBrakingCount);

                                // Vibrate for 500 milliseconds when harsh braking occurs
                                vibrateDevice(500);

                                // Show a pop-up dialog for harsh braking
                                showAlertDialog("Harsh Braking", "You've experienced harsh braking.");
                            }

                            if (currentSpeedKph - previousSpeed > 5) {
                                suddenAccelerationCount++;
                                speedRef.child("sudden_acceleration_count").setValue(suddenAccelerationCount);
                                updateSuddenAccelerationUI(suddenAccelerationCount);

                                // Vibrate for 500 milliseconds when sudden acceleration occurs
                                vibrateDevice(500);

                                // Show a pop-up dialog for sudden acceleration
                                showAlertDialog("Sudden Acceleration", "You've experienced sudden acceleration.");
                            }
                        }

                        previousSpeed = currentSpeedKph;

                        totalSpeed += currentSpeedKph;
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;
                        averageSpeed = Math.round(averageSpeed * 100.0) / 100.0;

                        if (currentSpeedKph > maxSpeed) {
                            maxSpeed = currentSpeedKph; // Update maxSpeed if a new maximum is encountered
                            speedRef.child("max_speed").setValue(maxSpeed); // Update the maximum speed in Firebase
                            updateMaxSpeedUI(maxSpeed); // Update the UI
                        }

                        if (currentSpeedKph > 100.0) {
                            // Handle speeds exceeding 100 km/h
                            Log.d("SpeedTracker", "Speed exceeded 100 km/h: " + currentSpeedKph + " KM/h");
                            speedRef.child("speed_errors").push().setValue("Speed exceeded 100 km/h");

                            // Vibrate for 1000 milliseconds when speed exceeds 100 km/h
                            vibrateDevice(1000);

                            // Show a pop-up dialog for exceeding 100 km/h
                            showAlertDialog("Speed Exceeded", "You've exceeded 100 km/h.");
                        }

                        speedRef.child("average_speed").setValue(averageSpeed);
                        speedRef.child("current_speed").setValue(currentSpeedKph);

                        updateAverageSpeedUI(averageSpeed);
                        updateCurrentSpeedUI(currentSpeedKph);
                    }

                    // Update the location information
                    updateLocationInfo(location);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where the user doesn't grant the permission
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateAverageSpeedUI(double averageSpeed) {
        averageSpeedTextView.setText("Average Speed: " + averageSpeed + " KM/h");
    }

    private void updateMaxSpeedUI(double maxSpeed) {
        String formattedMaxSpeed = String.format("%.2f", maxSpeed);
        maxSpeedTextView.setText("Maximum Speed: " + formattedMaxSpeed + " KM/h");
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

    private void vibrateDevice(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(milliseconds);
        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the OK button click if needed
            }
        });
        builder.setCancelable(false); // Prevent the user from dismissing the dialog by tapping outside.
        builder.show();
    }
}
