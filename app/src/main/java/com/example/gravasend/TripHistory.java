package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TripHistory extends AppCompatActivity {
    private ImageButton b1;
    private EditText firstMaintenanceItem;
    private EditText secondMaintenanceItem;
    private CheckBox firstMaintenanceItemCheckBox;
    private CheckBox secondMaintenanceItemCheckBox;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triphistory);

        auth = FirebaseAuth.getInstance();
        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Initialize Firebase Database reference for the current user
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trip History").child(userId).child("trips");

        b1 = findViewById(R.id.backButton);
       // firstMaintenanceItem = findViewById(R.id.firstMaintenanceItem);
       // secondMaintenanceItem = findViewById(R.id.secondMaintenanceItem);
        //firstMaintenanceItemCheckBox = findViewById(R.id.firstMaintenanceItemCheckBox);
        //secondMaintenanceItemCheckBox = findViewById(R.id.secondMaintenanceItemCheckBox);

        // Handle the back button click
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripHistory.this, MyTrips.class);
                startActivity(intent);
            }
        });
/*
        // Save data to Firebase when the checkboxes are clicked
        firstMaintenanceItemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase(firstMaintenanceItem.getText().toString(), firstMaintenanceItemCheckBox.isChecked(), "trip1");
            }
        });

        secondMaintenanceItemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase(secondMaintenanceItem.getText().toString(), secondMaintenanceItemCheckBox.isChecked(), "trip2");
            }
        });

        // Load data from Firebase
        loadDataFromFirebase("trip1");
        loadDataFromFirebase("trip2");
    }

    private void saveDataToFirebase(String jobOrder, boolean completed, String tripId) {
        // Create a trip object
        Trip trip = new Trip(jobOrder, completed);

        // Save the trip to Firebase under the given tripId
        databaseReference.child(tripId).setValue(trip);

        Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show();
    }

    private void loadDataFromFirebase(final String tripId) {
        // Read data from Firebase under the given tripId
        databaseReference.child(tripId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        if (trip != null) {
                            if (tripId.equals("trip1")) {
                                firstMaintenanceItem.setText(trip.getJobOrder());
                                firstMaintenanceItemCheckBox.setChecked(trip.isCompleted());
                            } else if (tripId.equals("trip2")) {
                                secondMaintenanceItem.setText(trip.getJobOrder());
                                secondMaintenanceItemCheckBox.setChecked(trip.isCompleted());
                            }
                        }
                    }
                }
            }
        });*/
    }
}
