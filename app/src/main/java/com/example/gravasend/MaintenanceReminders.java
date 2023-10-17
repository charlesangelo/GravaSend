package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MaintenanceReminders extends AppCompatActivity {
    private ImageButton backButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private TextView dateView1;
    private TextView type1;
    private TextView dateView2;
    private TextView type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenancereminders);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("maintenanceReminders");

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set up references to views
        dateView1 = findViewById(R.id.DateView1);
        type1 = findViewById(R.id.Type1);
        dateView2 = findViewById(R.id.DateView2);
        type2 = findViewById(R.id.Type2);

        // Back Button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceReminders.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Load data from Firebase and set it to TextViews
        loadMaintenanceReminders();
    }

    private void loadMaintenanceReminders() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userReference = databaseReference.child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String date1 = dataSnapshot.child("firstDate").getValue(String.class);
                        String type1Text = dataSnapshot.child("firstType").getValue(String.class);
                        String date2 = dataSnapshot.child("secondDate").getValue(String.class);
                        String type2Text = dataSnapshot.child("secondType").getValue(String.class);

                        dateView1.setText(date1);
                        type1.setText(type1Text);
                        dateView2.setText(date2);
                        type2.setText(type2Text);
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
