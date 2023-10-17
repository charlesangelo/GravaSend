package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MaintenanceHistory extends AppCompatActivity {
    private TextView dateView1;
    private TextView type1;
    private TextView dateView2;
    private TextView type2;
    private ImageButton backButton;

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
        dateView1 = findViewById(R.id.DateView1);
        type1 = findViewById(R.id.Type1);
        dateView2 = findViewById(R.id.DateView2);
        type2 = findViewById(R.id.Type2);
        backButton = findViewById(R.id.backButton);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceHistory.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Load data from Firebase and set it to TextViews
        loadMaintenanceHistory();
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
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String firstDate = dataSnapshot.child("firstDate").getValue(String.class);
                        String firstType = dataSnapshot.child("firstType").getValue(String.class);
                        String secondDate = dataSnapshot.child("secondDate").getValue(String.class);
                        String secondType = dataSnapshot.child("secondType").getValue(String.class);

                        dateView1.setText(firstDate);
                        type1.setText(firstType);
                        dateView2.setText(secondDate);
                        type2.setText(secondType);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error here if needed
                }
            });
        }
    }
}
