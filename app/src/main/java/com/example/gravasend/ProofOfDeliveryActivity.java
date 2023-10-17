package com.example.gravasend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gravasend.SignatureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class ProofOfDeliveryActivity extends AppCompatActivity {
    private SignatureView signatureView;
    private EditText dateInput, timeInput;
    private Button completeTripButton, clearButton;
    private DatabaseReference proofOfDeliveryRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String eSignatureUrl; // URL for the e-signature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proofofdelivery);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        proofOfDeliveryRef = database.getReference("ProofOfDelivery");
        currentUser = firebaseAuth.getCurrentUser();

        // Initialize views
        signatureView = findViewById(R.id.signatureView);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        completeTripButton = findViewById(R.id.completeTripButton);
        clearButton = findViewById(R.id.clearButton);

// Find the backButton as an ImageButton and set an OnClickListener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the TripDashboard activity
                Intent intent = new Intent(ProofOfDeliveryActivity.this, TripDashboard.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it
            }
        });


        // Retrieve and set retained data, if available
        if (currentUser != null) {
            DatabaseReference userRef = proofOfDeliveryRef.child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ProofOfDeliveryData deliveryData = dataSnapshot.getValue(ProofOfDeliveryData.class);
                        if (deliveryData != null) {
                            dateInput.setText(deliveryData.getDate());
                            timeInput.setText(deliveryData.getTime());

                            // Get the e-signature URL and display it
                            eSignatureUrl = deliveryData.getESignature();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database errors if needed.
                }
            });
        }

        // Complete Trip Button
        completeTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's input
                String eSignature = captureESignature();
                String date = dateInput.getText().toString();
                String time = timeInput.getText().toString();

                // Check if any field is empty
                if (eSignature.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(ProofOfDeliveryActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Store the input data in Firebase
                if (currentUser != null) {
                    DatabaseReference userRef = proofOfDeliveryRef.child(currentUser.getUid());
                    // Update the e-signature URL
                    eSignatureUrl = eSignature;
                    ProofOfDeliveryData deliveryData = new ProofOfDeliveryData(eSignatureUrl, date, time);
                    userRef.setValue(deliveryData);

                    // Optionally, reset the input fields
                    dateInput.setText("");
                    timeInput.setText("");
                    signatureView.clear();
                }
            }
        });

        // Clear Button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the signature
                signatureView.clearSignature();
            }
        });
    }

    // Implement a method to capture the e-signature
    private String captureESignature() {
        // Capture the e-signature from the signatureView
        Bitmap signatureBitmap = signatureView.getSignatureBitmap();

        // Check if the signatureBitmap is null or empty
        if (signatureBitmap == null) {
            return ""; // Return an empty string if the signature is empty
        }

        // Convert the signature to a base64 encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}

