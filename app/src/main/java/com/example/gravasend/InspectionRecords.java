package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InspectionRecords extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private EditText firstMaintenanceItemEditText;
    private EditText secondMaintenanceItemEditText;

    private CheckBox firstMaintenanceItemCheckBox;
    private CheckBox secondMaintenanceItemCheckBox;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspectionrecords);
/*
        // Initialize Firebase Database and Authentication
        databaseReference = FirebaseDatabase.getInstance().getReference("inspectionRecords");
        auth = FirebaseAuth.getInstance();

        // Initialize views
        */
        backButton = findViewById(R.id.backButton);
        /*
        doneButton = findViewById(R.id.doneButton);

        firstMaintenanceItemEditText = findViewById(R.id.firstMaintenanceItem);
        secondMaintenanceItemEditText = findViewById(R.id.secondMaintenanceItem);

        firstMaintenanceItemCheckBox = findViewById(R.id.firstMaintenanceItemCheckBox);
        secondMaintenanceItemCheckBox = findViewById(R.id.secondMaintenanceItemCheckBox);
*/
        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InspectionRecords.this, TruckInformation.class);
                startActivity(intent);
            }
        });
/*
        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInspectionRecords();
            }
        });

        // Load data from Firebase and set it to EditText and CheckBox
        loadInspectionRecords();
    }

    private void saveInspectionRecords() {
        String firstMaintenanceItem = firstMaintenanceItemEditText.getText().toString().trim();
        String secondMaintenanceItem = secondMaintenanceItemEditText.getText().toString().trim();

        boolean firstItemCompleted = firstMaintenanceItemCheckBox.isChecked();
        boolean secondItemCompleted = secondMaintenanceItemCheckBox.isChecked();

        String userId = auth.getCurrentUser().getUid();

        DatabaseReference userReference = databaseReference.child(userId);

        userReference.child("firstMaintenanceItem").setValue(firstMaintenanceItem);
        userReference.child("secondMaintenanceItem").setValue(secondMaintenanceItem);

        userReference.child("firstItemCompleted").setValue(firstItemCompleted);
        userReference.child("secondItemCompleted").setValue(secondItemCompleted);

        Toast.makeText(InspectionRecords.this, "Inspection records saved successfully.", Toast.LENGTH_SHORT).show();
    }

    private void loadInspectionRecords() {
        String userId = auth.getCurrentUser().getUid();

        DatabaseReference userReference = databaseReference.child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstMaintenanceItem = dataSnapshot.child("firstMaintenanceItem").getValue(String.class);
                    String secondMaintenanceItem = dataSnapshot.child("secondMaintenanceItem").getValue(String.class);

                    boolean firstItemCompleted = dataSnapshot.child("firstItemCompleted").getValue(Boolean.class);
                    boolean secondItemCompleted = dataSnapshot.child("secondItemCompleted").getValue(Boolean.class);

                    firstMaintenanceItemEditText.setText(firstMaintenanceItem);
                    secondMaintenanceItemEditText.setText(secondMaintenanceItem);

                    firstMaintenanceItemCheckBox.setChecked(firstItemCompleted);
                    secondMaintenanceItemCheckBox.setChecked(secondItemCompleted);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error here if needed
            }
        });*/
    }
}
