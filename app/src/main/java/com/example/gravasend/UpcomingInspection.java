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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpcomingInspection extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private EditText date1EditText;
    private EditText date2EditText;

    private CheckBox dueTodayCheckBox1;
    private CheckBox dueTodayCheckBox2;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcominginspections);
/*
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("upcomingInspections");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        */
        backButton = findViewById(R.id.backButton);
        /*
        doneButton = findViewById(R.id.doneButton);

        date1EditText = findViewById(R.id.TB2);
        date2EditText = findViewById(R.id.TB1);

        dueTodayCheckBox1 = findViewById(R.id.CB1);
        dueTodayCheckBox2 = findViewById(R.id.CB2);
*/
        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpcomingInspection.this, TruckInformation.class);
                startActivity(intent);
            }
        });
/*
        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpcomingInspections();
            }
        });

        // Load data from Firebase and set it to EditText and CheckBox
        loadUpcomingInspections();
    }

    private void saveUpcomingInspections() {
        String date1 = date1EditText.getText().toString().trim();
        String date2 = date2EditText.getText().toString().trim();

        boolean dueToday1 = dueTodayCheckBox1.isChecked();
        boolean dueToday2 = dueTodayCheckBox2.isChecked();

        if (currentUser != null) {
            // Save data to Firebase under the user's ID
            String userId = currentUser.getUid();

            DatabaseReference userReference = databaseReference.child(userId);
            userReference.child("date1").setValue(date1);
            userReference.child("date2").setValue(date2);

            userReference.child("dueToday1").setValue(dueToday1);
            userReference.child("dueToday2").setValue(dueToday2);

            Toast.makeText(UpcomingInspection.this, "Upcoming inspections saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UpcomingInspection.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUpcomingInspections() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userReference = databaseReference.child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String date1 = dataSnapshot.child("date1").getValue(String.class);
                        String date2 = dataSnapshot.child("date2").getValue(String.class);

                        boolean dueToday1 = dataSnapshot.child("dueToday1").getValue(Boolean.class);
                        boolean dueToday2 = dataSnapshot.child("dueToday2").getValue(Boolean.class);

                        date1EditText.setText(date1);
                        date2EditText.setText(date2);

                        dueTodayCheckBox1.setChecked(dueToday1);
                        dueTodayCheckBox2.setChecked(dueToday2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error here if needed
                }
            });
        }*/
    }
}
