package com.example.gravasend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DatabaseReference locationRef;
    private TextView locationText;

    // Firebase
    private FirebaseAuth mAuth;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationText = findViewById(R.id.locationText);
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication
        geocoder = new Geocoder(this, Locale.getDefault()); // Initialize Geocoder

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        locationRef = database.getReference("locations");

        // Configure Location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)  // 10 seconds
                .setFastestInterval(5000);  // 5 seconds

        // Initialize Location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    android.location.Location location = locationResult.getLastLocation();
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

                    // Get the current user's UID
                    String currentUserId = mAuth.getCurrentUser().getUid();

                    // Push Location data to Firebase with UID as the key
                    locationRef.child(currentUserId).setValue(location);
                }
            }
        };

        // Request Location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Handle permission request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Handle permission denial
                    Toast.makeText(this, "Location permission denied. App cannot function.", Toast.LENGTH_SHORT).show();
                    return;
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                Toast.makeText(this, "Location permission denied. App cannot function.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
