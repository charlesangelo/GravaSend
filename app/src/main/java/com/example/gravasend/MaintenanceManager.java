package com.example.gravasend;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;

public class MaintenanceManager {

    private static MaintenanceManager instance;
    private Handler handler;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    // Callback interface for asynchronous status retrieval
    public interface StatusCallback {
        void onStatusReceived(String status);
    }

    private MaintenanceManager() {
        handler = new Handler(Looper.getMainLooper());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("maintenanceReminders");
    }

    public static synchronized MaintenanceManager getInstance() {
        if (instance == null) {
            instance = new MaintenanceManager();
        }
        return instance;
    }

    public void startMaintenanceCheck(Context context) {
        // Create a runnable for periodic checks
        Runnable maintenanceCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkMaintenanceStatus(context);
                handler.postDelayed(this, 60000);
            }
        };

        // Start the periodic checks
        handler.postDelayed(maintenanceCheckRunnable, 0);
    }

    private void checkMaintenanceStatus(Context context) {
        // Get the status text from the appropriate source
        getStatusFromDataSource(context, new StatusCallback() {
            @Override
            public void onStatusReceived(String status) {
                // Check if the status is due or overdue
                if (status != null && (status.equalsIgnoreCase("due") || status.equalsIgnoreCase("overdue"))) {
                    // Show a prompt or notification to the user
                    showMaintenancePrompt(context);
                }
            }
        });
    }

    private void getStatusFromDataSource(Context context, StatusCallback callback) {
        ArrayList<String> keys = new ArrayList<>();
        DatabaseReference userReference = databaseReference.child(currentUser.getUid());

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each trip key and add it to the list
                    String maintenanceKey = keySnapshot.getKey();
                    keys.add(maintenanceKey);
                }

                // Iterate through the trip keys and read data for each key
                for (String key : keys) {
                    DatabaseReference maintenanceRef = userReference.child(key);
                    maintenanceRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.exists()) {
                                    String status = dataSnapshot.child("status").getValue(String.class);
                                    if (status != null && (status.equalsIgnoreCase("due") || status.equalsIgnoreCase("overdue"))) {
                                        // Notify the callback with the found status
                                        callback.onStatusReceived(status);
                                        return;
                                    }
                                }
                                // If no due or overdue status is found, notify the callback with null
                                callback.onStatusReceived(null);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    private void showMaintenancePrompt(Context context) {
        // Inflate the custom layout
        View maintenancePromptView = LayoutInflater.from(context).inflate(R.layout.custom_maintenance_prompt, null);

        // Build the AlertDialog
        AlertDialog maintenanceDialog = new AlertDialog.Builder(context)
                .setView(maintenancePromptView)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        // Show the AlertDialog
        maintenanceDialog.show();

        // Dismiss the dialog after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (maintenanceDialog.isShowing()) {
                    maintenanceDialog.dismiss();
                }
            }
        }, 30000);
    }
}