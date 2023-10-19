package com.example.gravasend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import android.content.Intent; // Import Intent class for navigation

public class TripDashboard extends AppCompatActivity {
    private ImageButton backButton;
    private Button completeTripButton;
    private TextView date, idDelivery, location, destination, cargoDetailsDescription, cargoDetailsDescription2, specialInstructionsDescription;

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
        completeTripButton = findViewById(R.id.doneButton);
        date = findViewById(R.id.date);
        idDelivery = findViewById(R.id.id_delivery);
        location = findViewById(R.id.location);
        destination = findViewById(R.id.destination);
        cargoDetailsDescription = findViewById(R.id.cargoDetailsDescription);
        cargoDetailsDescription2 = findViewById(R.id.cargoDetailsDescription2);
        specialInstructionsDescription = findViewById(R.id.specialInstructionsDescription);

        // Load and display data
        loadAndDisplayData();

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Complete Trip Button
        completeTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the action for completing the trip if needed
                // For example, you can add code to mark the trip as completed in the database.
                Toast.makeText(TripDashboard.this, "Trip completed.", Toast.LENGTH_SHORT).show();

                // Redirect to the ProofOfDeliveryActivity
                Intent intent = new Intent(TripDashboard.this, ProofOfDeliveryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadAndDisplayData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            tripDashboardRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        date.setText(dataSnapshot.child("date").getValue(String.class));
                        idDelivery.setText(dataSnapshot.child("id_delivery").getValue(String.class));
                        location.setText(dataSnapshot.child("Location").getValue(String.class));
                        destination.setText(dataSnapshot.child("destination").getValue(String.class));
                        cargoDetailsDescription.setText(dataSnapshot.child("cargoDetailsDescription").getValue(String.class));
                        cargoDetailsDescription2.setText(dataSnapshot.child("cargoDetailsDescription2").getValue(String.class));
                        specialInstructionsDescription.setText(dataSnapshot.child("specialInstructionsDescription").getValue(String.class));
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
}
