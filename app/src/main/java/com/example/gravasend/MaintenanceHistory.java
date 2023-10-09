package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MaintenanceHistory extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private EditText firstMaintenanceItemEditText;
    private EditText thirdMaintenanceItemEditText;

    private CheckBox firstMaintenanceItemCheckBox;
    private CheckBox thirdMaintenanceItemCheckBox;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenancehistory);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("maintenanceHistory");

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if the user is authenticated
        if (currentUser == null) {
            // If not authenticated, redirect to login or signup
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Set up references to views
        backButton = findViewById(R.id.backButton);
        doneButton = findViewById(R.id.doneButton);

        firstMaintenanceItemEditText = findViewById(R.id.firstMaintenanceItem);
        thirdMaintenanceItemEditText = findViewById(R.id.thirdMaintenanceItem);

        firstMaintenanceItemCheckBox = findViewById(R.id.checkB1);
        thirdMaintenanceItemCheckBox = findViewById(R.id.checkB2);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceHistory.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMaintenanceHistory();
            }
        });

        // Load data from Firebase and set it to EditText and CheckBox
        loadMaintenanceHistory();
    }

    private void saveMaintenanceHistory() {
        String firstMaintenanceItem = firstMaintenanceItemEditText.getText().toString().trim();
        String thirdMaintenanceItem = thirdMaintenanceItemEditText.getText().toString().trim();

        boolean firstItemCompleted = firstMaintenanceItemCheckBox.isChecked();
        boolean thirdItemCompleted = thirdMaintenanceItemCheckBox.isChecked();

        // Check if the current user is authenticated
        if (currentUser != null) {
            // Create a unique key for the user's maintenance history
            String userId = currentUser.getUid();

            // Update the database reference to store data under the user's ID and "Maintenance History"
            DatabaseReference userMaintenanceRef = databaseReference.child(userId);

            // Save data to Firebase
            userMaintenanceRef.child("firstMaintenanceItem").setValue(firstMaintenanceItem);
            userMaintenanceRef.child("thirdMaintenanceItem").setValue(thirdMaintenanceItem);

            userMaintenanceRef.child("firstItemCompleted").setValue(firstItemCompleted);
            userMaintenanceRef.child("thirdItemCompleted").setValue(thirdItemCompleted);

            Toast.makeText(MaintenanceHistory.this, "Maintenance history saved successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMaintenanceHistory() {
        // Check if the current user is authenticated
        if (currentUser != null) {
            // Get the user's ID
            String userId = currentUser.getUid();

            // Get the reference to the user's maintenance history data
            DatabaseReference userMaintenanceRef = databaseReference.child(userId);

            userMaintenanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String firstMaintenanceItem = dataSnapshot.child("firstMaintenanceItem").getValue(String.class);
                        String thirdMaintenanceItem = dataSnapshot.child("thirdMaintenanceItem").getValue(String.class);

                        boolean firstItemCompleted = dataSnapshot.child("firstItemCompleted").getValue(Boolean.class);
                        boolean thirdItemCompleted = dataSnapshot.child("thirdItemCompleted").getValue(Boolean.class);

                        firstMaintenanceItemEditText.setText(firstMaintenanceItem);
                        thirdMaintenanceItemEditText.setText(thirdMaintenanceItem);

                        firstMaintenanceItemCheckBox.setChecked(firstItemCompleted);
                        thirdMaintenanceItemCheckBox.setChecked(thirdItemCompleted);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error here if needed
                }
            });
        }
    }
}
