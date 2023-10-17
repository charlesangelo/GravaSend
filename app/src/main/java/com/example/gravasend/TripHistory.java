package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.util.Log;
import android.support.annotation.NonNull;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TripHistory extends AppCompatActivity {
    private ImageButton backButton;
    private TextView firstMaintenanceItem;
    private TextView firstMaintenanceDate;
    private TextView secondMaintenanceItem;
    private TextView secondMaintenanceDate;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triphistory);

        auth = FirebaseAuth.getInstance();
        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Initialize the Firebase Database reference for the current user
        databaseReference = FirebaseDatabase.getInstance().getReference().child("TripHistory").child(userId);

        backButton = findViewById(R.id.backButton);
        firstMaintenanceItem = findViewById(R.id.firstMaintenanceItem);
        firstMaintenanceDate = findViewById(R.id.firstMaintenanceDate);
        secondMaintenanceItem = findViewById(R.id.secondMaintenanceItem);
        secondMaintenanceDate = findViewById(R.id.secondMaintenanceDate);

        // Handle the back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripHistory.this, MyTrips.class);
                startActivity(intent);
            }
        });

        // Read data from the Firebase database and display it
        readDataFromFirebase();
    }

    private void readDataFromFirebase() {
        // Read data for firstMaintenanceItem
        databaseReference.child("firstMaintenanceItem").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String item = dataSnapshot.getValue(String.class);
                        firstMaintenanceItem.setText(item);
                    } else {
                        Log.d("Firebase Data", "Data for firstMaintenanceItem does not exist.");
                    }
                } else {
                    Log.e("Firebase Error", "Data retrieval for firstMaintenanceItem failed: " + task.getException());
                }
            }
        });

        // Read data for firstMaintenanceDate
        databaseReference.child("firstMaintenanceDate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String date = dataSnapshot.getValue(String.class);
                        firstMaintenanceDate.setText(date);
                    } else {
                        Log.d("Firebase Data", "Data for firstMaintenanceDate does not exist.");
                    }
                } else {
                    Log.e("Firebase Error", "Data retrieval for firstMaintenanceDate failed: " + task.getException());
                }
            }
        });

        // Read data for secondMaintenanceItem
        databaseReference.child("secondMaintenanceItem").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String item = dataSnapshot.getValue(String.class);
                        secondMaintenanceItem.setText(item);
                    } else {
                        Log.d("Firebase Data", "Data for secondMaintenanceItem does not exist.");
                    }
                } else {
                    Log.e("Firebase Error", "Data retrieval for secondMaintenanceItem failed: " + task.getException());
                }
            }
        });

        // Read data for secondMaintenanceDate
        databaseReference.child("secondMaintenanceDate").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String date = dataSnapshot.getValue(String.class);
                        secondMaintenanceDate.setText(date);
                    } else {
                        Log.d("Firebase Data", "Data for secondMaintenanceDate does not exist.");
                    }
                } else {
                    Log.e("Firebase Error", "Data retrieval for secondMaintenanceDate failed: " + task.getException());
                }
            }
        });
    }
}
