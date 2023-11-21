package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MaintenanceHistory extends AppCompatActivity {

    private ImageButton backButton;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private LinearLayout maintenanceRecordBox;
    private MaintenanceManager maintenanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenancehistory);

        maintenanceManager = MaintenanceManager.getInstance();
        maintenanceManager.startMaintenanceCheck(this);

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


        backButton = findViewById(R.id.backButton);
        maintenanceRecordBox = findViewById(R.id.maintenanceRecordBox);
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


                    DatabaseReference maintenanceRef = databaseReference.child(currentUser.getUid()).child(key);
                    maintenanceRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {

                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();



                                if (dataSnapshot.exists()) {

                                    View maintenanceView = getLayoutInflater().inflate(R.layout.maintenance_item, null);
                                    TextView serviceTextView = maintenanceView.findViewById(R.id.service);
                                    TextView mileageTextView = maintenanceView.findViewById(R.id.nextduemileage);
                                    TextView statusTextView = maintenanceView.findViewById(R.id.status);


                                    String serviceText = dataSnapshot.child("service").getValue(String.class);
                                    String statusText = dataSnapshot.child("status").getValue(String.class);
                                    int mileage = dataSnapshot.child("mileage").getValue(Integer.class);
                                    String frequency = dataSnapshot.child("frequency").getValue(String.class);
                                    int frequencyValue = Integer.parseInt(frequency);
                                    int nextDueMileage=mileage + frequencyValue;
                                    String nextDueMileageString = String.valueOf(nextDueMileage);

                                    serviceTextView.setText(serviceText);
                                    mileageTextView.setText(nextDueMileageString+" km");
                                    statusTextView.setText(statusText);

                                    // Add margin to the inspectionView (adjust margin value as needed)
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(0, 0, 0, 16); // Adjust the margin value as needed
                                    maintenanceView.setLayoutParams(layoutParams);

                                    maintenanceRecordBox.addView(maintenanceView);
                                }
                            }
                        }
                    });
                } }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        }
    }

