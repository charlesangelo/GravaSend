package com.example.gravasend;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

public class LocationService extends Service {
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
    private MediaPlayer mediaPlayer;
    private NotificationManagerCompat notificationManager;

    private static final String CHANNEL_ID = "LocationServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        auth = FirebaseAuth.getInstance();
        String userUid = auth.getCurrentUser().getUid();

        speedRef = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(userUid);
        locationRef = FirebaseDatabase.getInstance().getReference("locations").child(userUid);

        geocoder = new Geocoder(this, Locale.getDefault());

        requestLocationPermissions(); // Request location permissions

        startLocationTracking();

        notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Location Service Channel";
            String description = "Channel for Location Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(CHANNEL_ID, name, description, importance);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = createNotification();
        startForeground(1, notification);
        return START_STICKY;
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logofinal) // Replace with your notification icon
                .setContentTitle("Location Service")
                .setContentText("Location Service is running in the background")
                .setPriority(NotificationCompat.PRIORITY_LOW);
        return builder.build();
    }

    private void createNotificationChannel(String id, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startLocationTracking() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location.hasSpeed()) {
                        double currentSpeedMps = location.getSpeed();
                        double currentSpeedKph = currentSpeedMps * 3.6;

                        if (previousSpeed != -1) {
                            if (previousSpeed - currentSpeedKph > 20) {
                                harshBrakingCount++;
                                speedRef.child("harsh_braking_count").setValue(harshBrakingCount);
                                updateHarshBrakingUI(harshBrakingCount);

                                vibrateAndPlaySound(500);

                                showAlertDialog("Harsh Braking", "You've experienced harsh braking.");
                            }

                            if (currentSpeedKph - previousSpeed > 20) {
                                suddenAccelerationCount++;
                                speedRef.child("sudden_acceleration_count").setValue(suddenAccelerationCount);
                                updateSuddenAccelerationUI(suddenAccelerationCount);

                                vibrateAndPlaySound(500);

                                showAlertDialog("Sudden Acceleration", "You've experienced sudden acceleration.");
                            }
                        }

                        previousSpeed = currentSpeedKph;

                        totalSpeed += currentSpeedKph;
                        speedCount++;
                        double averageSpeed = totalSpeed / speedCount;
                        averageSpeed = Math.round(averageSpeed * 100.0) / 100.0;

                        if (currentSpeedKph > maxSpeed) {
                            maxSpeed = currentSpeedKph;
                            speedRef.child("max_speed").setValue(maxSpeed);
                            updateMaxSpeedUI(maxSpeed);
                        }

                        if (currentSpeedKph > 60.0) {
                            Log.d("SpeedTracker", "Speed exceeded 100 km/h: " + currentSpeedKph + " KM/h");
                            speedRef.child("speed_errors").push().setValue("Speed exceeded 100 km/h");
                            vibrateAndPlaySound(1000);
                            showAlertDialog("Speed Exceeded", "You've exceeded 100 km/h.");
                        }

                        speedRef.child("average_speed").setValue(averageSpeed);
                        speedRef.child("current_speed").setValue(currentSpeedKph);

                        updateAverageSpeedUI(averageSpeed);
                        updateCurrentSpeedUI(currentSpeedKph);
                    }

                    updateLocationInfo(location);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateAverageSpeedUI(double averageSpeed) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)
    }

    private void updateMaxSpeedUI(double maxSpeed) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)
    }

    private void updateCurrentSpeedUI(double currentSpeed) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)
    }

    private void updateHarshBrakingUI(int count) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)
    }

    private void updateSuddenAccelerationUI(int count) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)
    }

    private void updateLocationInfo(android.location.Location location) {
        // Update the UI here (e.g., send a broadcast or use other methods to update UI elements)

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                StringBuilder addressStringBuilder = new StringBuilder();
                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    addressStringBuilder.append(addresses.get(0).getAddressLine(i)).append("\n");
                }
                // Update the UI with the address (e.g., send a broadcast or use other methods)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationRef.setValue(location);
    }

    private void vibrateAndPlaySound(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(milliseconds);
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound); // Replace with your sound resource
        }

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
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
        builder.setCancelable(false);
        builder.show();
    }

    private void requestLocationPermissions() {
        // Moved the permission request to the MainActivity, do not request here in the Service.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
