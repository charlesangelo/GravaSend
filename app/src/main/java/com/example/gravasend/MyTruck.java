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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyTruck extends AppCompatActivity {
    private ImageButton backButton;
    private Button doneButton;

    private EditText plateNoEditText;
    private EditText chassisNoEditText;
    private EditText engineNoEditText;
    private EditText modelEditText;
    private EditText mileageEditText;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytruck);
/*
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("trucks");

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        */

        backButton = findViewById(R.id.backButton);

        /*
        doneButton = findViewById(R.id.doneButton);

        plateNoEditText = findViewById(R.id.plateNoEditText);
        chassisNoEditText = findViewById(R.id.chassisNoEditText);
        engineNoEditText = findViewById(R.id.engineNoEditText);
        modelEditText = findViewById(R.id.modelEditText);
        mileageEditText = findViewById(R.id.mileageEditText);
*/
        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTruck.this, TruckInformation.class));
            }
        });
/*
        // Done Button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTruckData();
            }
        });
    }

    private void saveTruckData() {
        String plateNo = plateNoEditText.getText().toString().trim();
        String chassisNo = chassisNoEditText.getText().toString().trim();
        String engineNo = engineNoEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String mileage = mileageEditText.getText().toString().trim();

        if (!plateNo.isEmpty() && !chassisNo.isEmpty() && !engineNo.isEmpty() && !model.isEmpty() && !mileage.isEmpty()) {
            // Get the current authenticated user
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                // Create a unique key for the truck
                String truckId = databaseReference.push().getKey();

                // Create a Truck object with the data
                Truck truck = new Truck(truckId, plateNo, chassisNo, engineNo, model, mileage);

                // Save the truck data under the user's ID
                databaseReference.child(currentUser.getUid()).child(truckId).setValue(truck);

                Toast.makeText(MyTruck.this, "Truck data saved successfully.", Toast.LENGTH_SHORT).show();
                clearEditTextFields();
            } else {
                Toast.makeText(MyTruck.this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyTruck.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearEditTextFields() {
        plateNoEditText.setText("");
        chassisNoEditText.setText("");
        engineNoEditText.setText("");
        modelEditText.setText("");
        mileageEditText.setText("");


 */
    }
}
