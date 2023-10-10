package com.example.gravasend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DocumentCheck extends AppCompatActivity {
    private ImageButton b1;
    private CheckBox driversLicenseCheckBox;
    private CheckBox orcrCheckBox;
    private CheckBox localTransportPermitCheckBox;
    private Button verifyLoadButton; // Add this button
    private Button clearButton; // Add the "Clear" button
    private SignatureView signatureView; // Add this SignatureView

    private DatabaseReference databaseReference; // Add this variable
    private StorageReference storageReference; // Add this variable

    private boolean loadedData = false; // Flag to track if data has been loaded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentcheck);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize the Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        b1 = findViewById(R.id.backButton);
        driversLicenseCheckBox = findViewById(R.id.driversLicenseCheckBox);
        orcrCheckBox = findViewById(R.id.orcrCheckBox);
        localTransportPermitCheckBox = findViewById(R.id.localTransportPermitCheckBox);
        verifyLoadButton = findViewById(R.id.verifyLoadButton); // Initialize the "Verify Load" button
        clearButton = findViewById(R.id.clearButton); // Initialize the "Clear" button
        signatureView = findViewById(R.id.signatureView); // Initialize the SignatureView

        // Handle the back button click
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save checkbox states to Firebase
                saveCheckboxStatesToFirebase();

                // Navigate to the next activity
                Intent intent = new Intent(DocumentCheck.this, CurrentTrip.class);
                startActivity(intent);
            }
        });

        // Handle the "Verify Load" button click
        verifyLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save checkbox states to Firebase
                saveCheckboxStatesToFirebase();

                // Get the e-signature as a Bitmap
                Bitmap signatureBitmap = signatureView.getSignatureBitmap();

                if (signatureBitmap != null) {
                    // Convert the Bitmap to a base64-encoded string (for database storage)
                    String encodedSignature = encodeBitmapToBase64(signatureBitmap);

                    // Save the encoded signature to the database and Firebase Storage
                    saveSignature(encodedSignature);

                    // Perform the "Verify Load" action (you can add your verification logic here)

                    // For example, show a toast message
                    Toast.makeText(DocumentCheck.this, "Load verified!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle the "Clear" button click
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to clear user data
                clearUserData();
                // Clear the signature view
                signatureView.clearCanvas();
            }
        });

        // Load checkbox states from Firebase only once when the activity is created
        if (!loadedData) {
            loadCheckboxStatesFromFirebase();
            loadedData = true; // Set the flag to true after loading data
        }
    }

    // Method to save checkbox states to Firebase
    private void saveCheckboxStatesToFirebase() {
        String userId = getCurrentUserId(); // Get the user's ID

        if (userId != null) {
            boolean driversLicenseChecked = driversLicenseCheckBox.isChecked();
            boolean orcrChecked = orcrCheckBox.isChecked();
            boolean localTransportPermitChecked = localTransportPermitCheckBox.isChecked();

            // Create a data object to save in the Realtime Database
            Map<String, Object> checkboxData = new HashMap<>();
            checkboxData.put("driversLicenseChecked", driversLicenseChecked);
            checkboxData.put("orcrChecked", orcrChecked);
            checkboxData.put("localTransportPermitChecked", localTransportPermitChecked);

            // Save the data to the Realtime Database under the user's UID
            databaseReference.child("Document Check").child(userId).setValue(checkboxData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data saved successfully
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to save data
                        }
                    });
        }
    }

    // Method to load checkbox states from Firebase
    private void loadCheckboxStatesFromFirebase() {
        String userId = getCurrentUserId(); // Get the user's ID

        if (userId != null) {
            databaseReference.child("users").child(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Retrieve checkbox states from the Realtime Database and update checkboxes
                                boolean driversLicenseChecked = dataSnapshot.child("driversLicenseChecked").getValue(Boolean.class);
                                boolean orcrChecked = dataSnapshot.child("orcrChecked").getValue(Boolean.class);
                                boolean localTransportPermitChecked = dataSnapshot.child("localTransportPermitChecked").getValue(Boolean.class);

                                driversLicenseCheckBox.setChecked(driversLicenseChecked);
                                orcrCheckBox.setChecked(orcrChecked);
                                localTransportPermitCheckBox.setChecked(localTransportPermitChecked);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to load data
                        }
                    });
        }
    }

    // Get the current user's ID
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Method to encode a Bitmap to base64
    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Method to save the e-signature to the database and Firebase Storage
    private void saveSignature(String encodedSignature) {
        String userId = getCurrentUserId(); // Get the user's ID

        if (userId != null) {
            // Create a unique file name for the signature image (you can use the user's UID or a timestamp)
            String fileName = "signature_" + userId + ".png";

            // Create a reference to the signature image in Firebase Storage
            StorageReference signatureRef = storageReference.child("signatures/" + fileName);

            // Decode the base64-encoded signature back to a Bitmap
            byte[] decodedBytes = Base64.decode(encodedSignature, Base64.DEFAULT);
            Bitmap signatureBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Compress the Bitmap to a PNG format
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Upload the signature image to Firebase Storage
            signatureRef.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            signatureRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            // Save the download URL to the Realtime Database
                                            String downloadUrl = downloadUri.toString();
                                            databaseReference.child("Document Check Signatures").child(userId).setValue(downloadUrl)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Signature and URL saved successfully
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Handle the failure to save the URL
                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to upload the image
                        }
                    });
        }
    }

    // Method to clear user data
    private void clearUserData() {
        String userId = getCurrentUserId(); // Get the user's ID

        if (userId != null) {
            // Remove the data associated with the user from the Realtime Database
            databaseReference.child("Document Check").child(userId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data removed successfully
                            Toast.makeText(DocumentCheck.this, "Data cleared!", Toast.LENGTH_SHORT).show();

                            // Clear the checkbox states
                            driversLicenseCheckBox.setChecked(false);
                            orcrCheckBox.setChecked(false);
                            localTransportPermitCheckBox.setChecked(false);

                            // Clear the uploaded e-signature and its URL
                            clearSignatureData(userId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to remove data
                            Toast.makeText(DocumentCheck.this, "Failed to clear data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to clear the uploaded e-signature and its URL
    private void clearSignatureData(String userId) {
        if (userId != null) {
            // Create a unique file name for the user's signature image
            String fileName = "signature_" + userId + ".png";

            // Create references to the signature image in Firebase Storage and the Realtime Database
            StorageReference signatureRef = storageReference.child("signatures/" + fileName);
            DatabaseReference signatureUrlRef = databaseReference.child("Document Check Signatures").child(userId);

            // Delete the e-signature image from Firebase Storage
            signatureRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Signature image deleted successfully

                            // Clear the URL from the Realtime Database
                            signatureUrlRef.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Signature URL removed successfully
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the failure to remove the URL
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to delete the image
                        }
                    });
        }
    }
}
