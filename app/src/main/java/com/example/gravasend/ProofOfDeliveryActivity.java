package com.example.gravasend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gravasend.SignatureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProofOfDeliveryActivity extends AppCompatActivity {
    private SignatureView signatureView;
    private EditText dateInput, timeInput;
    private Button completeTripButton, clearButton;
    private DatabaseReference proofOfDeliveryRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String eSignatureUrl; // URL for the e-signature
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String commonKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proofofdelivery);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        proofOfDeliveryRef = database.getReference("ProofRecords");
        currentUser = firebaseAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

        // Complete Trip Button
        completeTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = currentUser.getUid();


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
                    String fileName = "signature_" + currentUser.getUid() + ".png";

                    // Create a reference to the signature image in Firebase Storage
                    StorageReference signatureRef = storageReference.child("signatures/" + fileName);

                    DatabaseReference userRef = proofOfDeliveryRef.child(currentUser.getUid()).child(commonKey);
                    // Update the e-signature URL
                    eSignatureUrl = eSignature;

                    // Convert the e-signature to a byte array (PNG format)
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    signatureView.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // Upload the e-signature to Firebase Storage
                    signatureRef.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Signature uploaded successfully, get the download URL
                                signatureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // URL of the uploaded e-signature
                                        String eSignatureUrl = uri.toString();

                                        // Now you can update the proofOfDeliveryData in the Realtime Database
                                        ProofOfDeliveryData deliveryData = new ProofOfDeliveryData(eSignatureUrl, date, time);



                                        // Update the proofOfDeliveryData under the user's UID with the new delivery entry
                                        userRef.setValue(deliveryData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Upload successful
                                                            Toast.makeText(ProofOfDeliveryActivity.this, "Proof of Delivery submitted successfully.", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ProofOfDeliveryActivity.this, TripDashboard.class);
                                                            startActivity(intent);
                                                            finish(); // Finish the current activity
                                                        } else {
                                                            // Handle the error if the upload fails
                                                            Toast.makeText(ProofOfDeliveryActivity.this, "Failed to submit proof of delivery.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            } else {
                                // Handle the error if the upload fails
                                Toast.makeText(ProofOfDeliveryActivity.this, "Failed to upload e-signature", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                    DatabaseReference documentCheckRef = FirebaseDatabase.getInstance().getReference("Document Check").child(uid);
                    documentCheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("Record", dataSnapshot.toString());
                            if (dataSnapshot.exists()) {

                                boolean driversLicenseChecked=dataSnapshot.child("driversLicenseChecked").getValue(Boolean.class);
                                boolean orcrChecked=dataSnapshot.child("orcrChecked").getValue(Boolean.class);
                                boolean localTransportPermitChecked=dataSnapshot.child("localTransportPermitChecked").getValue(Boolean.class);

                                Map<String, Object> checkboxData = new HashMap<>();
                                checkboxData.put("driversLicenseChecked", driversLicenseChecked);
                                checkboxData.put("orcrChecked", orcrChecked);
                                checkboxData.put("localTransportPermitChecked", localTransportPermitChecked);

                                FirebaseDatabase.getInstance().getReference("DocumentCheckRecord").child(uid).child(commonKey).setValue(checkboxData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Document Checklist Record", "Value removed successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Document Checklist Record", "Value removed failed");
                                            }
                                        });




                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    DatabaseReference documentCheckSignaturesRef = FirebaseDatabase.getInstance().getReference("Document Check Signatures").child(uid);
                    documentCheckSignaturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {
                                String url=snapshot2.getValue(String.class);

                                DatabaseReference signRef = FirebaseDatabase.getInstance().getReference("DocumentCheckRecord").child(uid);
                                signRef.child(commonKey).child("signature").setValue(url)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                } else {

                                                }
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    DatabaseReference cargoRef = FirebaseDatabase.getInstance().getReference("Cargo").child(uid);
                    cargoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {
                                String cargoType=snapshot2.child("cargoType").getValue(String.class);
                                String cargoWeight=snapshot2.child("cargoWeight").getValue(String.class);


                                Map<String, Object> cargoval = new HashMap<>();
                                cargoval.put("cargoType", cargoType);
                                cargoval.put("cargoWeight", cargoWeight);

                                DatabaseReference signRef = FirebaseDatabase.getInstance().getReference("CargoRecord").child(uid);

                                signRef.child(commonKey).setValue(cargoval)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                } else {

                                                }
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference safetyref = FirebaseDatabase.getInstance().getReference("Safety Checklist").child(uid);
                    safetyref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {
                                Boolean brake=snapshot2.child("brake").getValue(Boolean.class);
                                Boolean lights=snapshot2.child("lights").getValue(Boolean.class);
                                Boolean safetyequipment=snapshot2.child("safetyequipment").getValue(Boolean.class);
                                Boolean steering=snapshot2.child("steering").getValue(Boolean.class);
                                Boolean suspension=snapshot2.child("suspension").getValue(Boolean.class);
                                Boolean tireswheels=snapshot2.child("tireswheels").getValue(Boolean.class);



                                Map<String, Object> docuval = new HashMap<>();
                                docuval.put("brake", brake);
                                docuval.put("lights", lights);
                                docuval.put("safetyequipment", safetyequipment);
                                docuval.put("steering", steering);
                                docuval.put("suspension", suspension);
                                docuval.put("tireswheels", tireswheels);

                                DatabaseReference signRef = FirebaseDatabase.getInstance().getReference("SafetyChecklistRecord").child(uid);

                                signRef.child(commonKey).setValue(docuval)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                } else {

                                                }
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference speedRefs = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(uid);
                    speedRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {
                                Integer average_speed=snapshot2.child("average_speed").getValue(Integer.class);
                                Integer current_speed=snapshot2.child("current_speed").getValue(Integer.class);
                                Integer harsh_braking_count=snapshot2.child("harsh_braking_count").getValue(Integer.class);
                                Integer max_speed=snapshot2.child("max_speed").getValue(Integer.class);
                                    Integer sudden_acceleration_count=snapshot2.child("sudden_acceleration_count").getValue(Integer.class);


                                Map<String, Object> speedval = new HashMap<>();
                                speedval.put("average_speed", average_speed);
                                speedval.put("current_speed", current_speed);
                                speedval.put("harsh_braking_count", harsh_braking_count);
                                speedval.put("max_speed", max_speed);
                                speedval.put("sudden_acceleration_count", sudden_acceleration_count);

                                DatabaseReference speedRecRef = FirebaseDatabase.getInstance().getReference("TripHistory").child(uid);

                                speedRecRef.child(commonKey).setValue(speedval)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                } else {

                                                }
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });






                    DatabaseReference truckRef = FirebaseDatabase.getInstance().getReference("trucks").child(uid);
                    truckRef.child("status").setValue("available")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("truck status", "success");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("truck status", "failed");
                                }
                            });

                    DatabaseReference tripDashboardRef = FirebaseDatabase.getInstance().getReference("Trip Dashboard").child(uid);

                    tripDashboardRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String dateTime=dataSnapshot.child("dateTime").getValue(String.class);
                                String origin=dataSnapshot.child("origin").getValue(String.class);
                                String destination=dataSnapshot.child("destination").getValue(String.class);
                                String cargo=dataSnapshot.child("cargo").getValue(String.class);
                                String weight=dataSnapshot.child("weight").getValue(String.class);
                                String instructions=dataSnapshot.child("instructions").getValue(String.class);
                                String driverName=dataSnapshot.child("driverName").getValue(String.class);
                                String UID=dataSnapshot.child("UID").getValue(String.class);



                                TripHistoryData tripHistory = new TripHistoryData(dateTime,origin,destination,cargo,weight,instructions,driverName,UID);

                                DatabaseReference triphistory = FirebaseDatabase.getInstance().getReference("TripHistory").child(uid);

                                triphistory.child(commonKey).setValue(tripHistory)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Trip History", "Value added successfully");

                                                } else {
                                                    Log.d("Trip History", "failed");
                                                }
                                            }
                                        });


                            }


                    tripDashboardRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Trip Dashboard", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Trip Dashboard", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });






                            DatabaseReference safetyChecklistRef2 = FirebaseDatabase.getInstance().getReference("Safety Checklist").child(uid);

                            safetyChecklistRef2.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Safety Checklist", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Safety Checklist", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });




                            DatabaseReference documentCheckRef2 = FirebaseDatabase.getInstance().getReference("Document Check").child(uid);
                    documentCheckRef2.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Document Checklist", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Document Checklist", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });



                            DatabaseReference documentCheckSignaturesRef2 = FirebaseDatabase.getInstance().getReference("Document Check Signatures").child(uid);

                            documentCheckSignaturesRef2.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Document CheckSignatures", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Document CheckSignatures", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });

                    // Delete Cargo
                    DatabaseReference cargoRef = FirebaseDatabase.getInstance().getReference("Cargo").child(uid);

                    cargoRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Cargo", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Cargo", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });

                    // Delete SpeedTracker
                    DatabaseReference speedTrackerRef = FirebaseDatabase.getInstance().getReference("SpeedTracker").child(uid);

                    speedTrackerRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("SpeedTracker", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("SpeedTracker", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });

                    // Delete Locations
                    DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations").child(uid);

                    locationsRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // Removal successful
                                Log.d("Locations", "Value removed successfully");
                            } else {
                                // Error occurred
                                Log.e("Locations", "Error removing value: " + databaseError.getMessage());
                            }
                        }
                    });

                    // Optionally, reset the input fields
                    dateInput.setText("");
                    timeInput.setText("");
                    signatureView.clear();
                    Toast.makeText(ProofOfDeliveryActivity.this, "Trip Completed Successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProofOfDeliveryActivity.this, CurrentTrip.class);
                    startActivity(intent);
                }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        };


        // Clear Button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the signature
                signatureView.clearSignature();

                // Clear the input fields
                dateInput.setText("");
                timeInput.setText("");
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
});}}

