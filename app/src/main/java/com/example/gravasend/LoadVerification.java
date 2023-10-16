package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoadVerification extends AppCompatActivity {
    private ImageButton b1;
    private Button verifyButton;
    private Button clearButton;
    private EditText cargoTypeInput;
    private EditText cargoWeightInput;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadverification);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the current authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        b1 = findViewById(R.id.backButton);
        /*
        verifyButton = findViewById(R.id.VerifyLV);
        clearButton = findViewById(R.id.clearLV);
        cargoTypeInput = findViewById(R.id.cargoTypeInput);
        cargoWeightInput = findViewById(R.id.cargoWeightInput);


         */
        // Handle the back button click
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadVerification.this, CurrentTrip.class);
                startActivity(intent);
            }
        });

/*
        // Handle the "Verify" button click
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save cargo type and weight to Firebase
                saveCargoData();
            }
        });

        // Handle the "Clear" button click
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the input fields
                cargoTypeInput.getText().clear();
                cargoWeightInput.getText().clear();

                // Add any additional data clearing logic here if needed
            }
        });
    }

    // Method to save cargo type and weight to Firebase
    private void saveCargoData() {
        String cargoType = cargoTypeInput.getText().toString().trim();
        String cargoWeight = cargoWeightInput.getText().toString().trim();

        if (!cargoType.isEmpty() && !cargoWeight.isEmpty() && currentUser != null) {
            // Use the current user's UID as a key to separate data
            String userId = currentUser.getUid();

            // Create a unique key for the cargo data
            String cargoId = databaseReference.child("Cargo").child(userId).push().getKey();

            // Create a Cargo object
            Cargo cargo = new Cargo(cargoId, cargoType, cargoWeight);

            // Save cargo data to Firebase under the user's UID
            databaseReference.child("Cargo").child(userId).child(cargoId).setValue(cargo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Data saved successfully
                                cargoTypeInput.getText().clear();
                                cargoWeightInput.getText().clear();
                            } else {
                                // Handle the failure to save data
                            }
                        }
                    });
        }

 */
    }
}
