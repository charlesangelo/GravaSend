package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripDashboard extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private EditText sampleLocationText, cargoDetailsDescription, specialInstructionsDescription;

    private DatabaseReference tripDashboardRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tripdashboard);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        tripDashboardRef = database.getReference("Trip Dashboard");

        // Initialize views
        backButton = findViewById(R.id.backButton);
        doneButton = findViewById(R.id.doneButton);

        sampleLocationText = findViewById(R.id.sampleLocationText);
        cargoDetailsDescription = findViewById(R.id.cargoDetailsDescription);
        specialInstructionsDescription = findViewById(R.id.specialInstructionsDescription);

        // Load existing data from Firebase and populate the EditText fields
        loadExistingData();

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTripData();
            }
        });
    }

    private void saveTripData() {
        String location = sampleLocationText.getText().toString().trim();
        String cargoDetails = cargoDetailsDescription.getText().toString().trim();
        String specialInstructions = specialInstructionsDescription.getText().toString().trim();

        if (!location.isEmpty() && !cargoDetails.isEmpty() && !specialInstructions.isEmpty()) {
            // Get the current authenticated user
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                // Create a unique key for the trip
                String tripId = tripDashboardRef.push().getKey();

                // Create a TripData object with the data
                TripData tripData = new TripData(location, cargoDetails, specialInstructions);

                // Save the trip data under the user's ID
                tripDashboardRef.child(currentUser.getUid()).child(tripId).setValue(tripData)
                        .addOnCompleteListener(TripDashboard.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(TripDashboard.this, "Trip data saved successfully.", Toast.LENGTH_SHORT).show();
                                clearEditTextFields();
                            } else {
                                Toast.makeText(TripDashboard.this, "Failed to save trip data.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(TripDashboard.this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(TripDashboard.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExistingData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            tripDashboardRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        TripData tripData = dataSnapshot.getValue(TripData.class);
                        if (tripData != null) {
                            sampleLocationText.setText(tripData.getLocation());
                            cargoDetailsDescription.setText(tripData.getCargoDetails());
                            specialInstructionsDescription.setText(tripData.getSpecialInstructions());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors here
                    Toast.makeText(TripDashboard.this, "Failed to load trip data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearEditTextFields() {
        sampleLocationText.setText("");
        cargoDetailsDescription.setText("");
        specialInstructionsDescription.setText("");
    }
}
