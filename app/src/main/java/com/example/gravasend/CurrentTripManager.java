package com.example.gravasend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentTripManager {
    private static CurrentTripManager instance;
    private Handler handler;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    public interface StatusCallback {
        void onStatusReceived(String status);
    }

    private CurrentTripManager() {
        handler = new Handler(Looper.getMainLooper());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trip Dashboard"); // Change the reference to Trip Dashboard
    }

    public static synchronized CurrentTripManager getInstance() {
        if (instance == null) {
            instance = new CurrentTripManager();
        }
        return instance;
    }

    public void CurrentTripCheck(Context context) {
        // Create a runnable for periodic checks
        Runnable tripRunnable = new Runnable() {
            @Override
            public void run() {
                checkTrip(context);
                handler.postDelayed(this, 60000);
            }
        };

        // Start the periodic checks
        handler.postDelayed(tripRunnable, 0);
    }

    private void checkTrip(Context context) {
        // Get the status text from the appropriate source
        getStatusFromDataSource(context, new CurrentTripManager.StatusCallback() {
            @Override
            public void onStatusReceived(String status) {
                // Check if the status indicates a pending trip
                if (status != null && status.equalsIgnoreCase("pending")) {
                    // Show a prompt or notification to the user
                    showTripPrompt(context);
                }
            }
        });
    }

    private void getStatusFromDataSource(Context context, CurrentTripManager.StatusCallback callback) {
        DatabaseReference userReference = databaseReference.child(currentUser.getUid());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User's UID is found, indicating a pending trip
                    callback.onStatusReceived("pending");
                } else {
                    // No pending trip found
                    callback.onStatusReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    private void showTripPrompt(Context context) {
        // Inflate the custom layout
        View tripPromptView = LayoutInflater.from(context).inflate(R.layout.custom_trip_prompt, null);

        // Build the AlertDialog
        AlertDialog tripDialog = new AlertDialog.Builder(context)
                .setView(tripPromptView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the click event
                        navigateToTripDashboard(context);
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .create();

        // Show the AlertDialog
        tripDialog.show();

        // Dismiss the dialog after 30 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tripDialog.isShowing()) {
                    tripDialog.dismiss();
                }
            }
        }, 30000);

    }
    private void navigateToTripDashboard(Context context) {
        // Assuming TripDashboard is an activity, you should replace this with your actual class
        Intent intent = new Intent(context, TripDashboard.class);
        context.startActivity(intent);
    }
}
