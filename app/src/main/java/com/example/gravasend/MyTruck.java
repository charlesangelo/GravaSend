package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyTruck extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private TextView plateNoTextView;
    private TextView chassisNoTextView;
    private TextView engineNoTextView; // Change this ID to match your XML
    private TextView modelTextView;
    private TextView mileageTextView;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytruck);

        databaseReference = FirebaseDatabase.getInstance().getReference("trucks");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        backButton = findViewById(R.id.backButton);
        plateNoTextView = findViewById(R.id.plateNoEditText);
        chassisNoTextView = findViewById(R.id.chassisNoEditText);
        engineNoTextView = findViewById(R.id.engineNoEditText); // Change this ID to match your XML
        modelTextView = findViewById(R.id.modelEditText);
        mileageTextView = findViewById(R.id.mileageEditText);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTruck.this, TruckInformation.class));
            }
        });

        doneButton = findViewById(R.id.doneButton);

        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh the data from Firebase
                fetchDataFromFirebase();
            }
        });

        // Fetch data from Firebase method
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        if (currentUser != null) {
            String uid = currentUser.getUid();

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Truck truck = snapshot.getValue(Truck.class);
                        if (truck != null) {
                            plateNoTextView.setText("Plate No: " + truck.getPlateNo());
                            chassisNoTextView.setText("Chassis No: " + truck.getChassisNo());
                            engineNoTextView.setText("Engine No: " + truck.getEngineNo());
                            modelTextView.setText("Model: " + truck.getModel());
                            mileageTextView.setText("Mileage: " + truck.getMileage());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors here
                }
            });
        }
    }
}
