package com.example.gravasend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity {

    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;
    private ImageView imageView6; // Display profile image
    private EditText edittext11;
    private Button editProfileButton;
    private FirebaseAuth mAuth; // Firebase Authentication
    private DatabaseReference databaseReference; // Firebase Realtime Database reference
    private StorageReference storageReference; // Firebase Storage reference for user images
    private static final String PREF_NAME = "DriverPrefs";
    private static final String KEY_DRIVER_NAME = "driverName";

    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views
        b1 = findViewById(R.id.truckInfoButton);
        b2 = findViewById(R.id.myTripsButton);
        b3 = findViewById(R.id.logoutButton);
        imageView6 = findViewById(R.id.imageView6); // Display profile image
        edittext11 = findViewById(R.id.edittext11);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Make imageView6 clickable
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow the user to select an image from the gallery
                pickImageFromGallery();
            }
        });

        // Load user profile data
        loadUserProfileData();

        // Set a click listener for the editProfileButton
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow the user to edit their profile name
                String newName = edittext11.getText().toString();

                if (!newName.isEmpty()) {
                    // Update the "Driver details" path in Firebase Realtime Database
                    updateDriverDetailsInFirebase(newName);
                    // Update the local SharedPreferences with the new name
                    saveDriverNameLocally(newName);
                } else {
                    Toast.makeText(Home.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateDriverDetailsInFirebase(String newName) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    // Create a reference to the "Driver Name" path in the database for the current user
                    DatabaseReference userReference = databaseReference.child("Driver Name").child(userId);

                    // Update the user's name
                    userReference.child("Driver details").setValue(newName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // User's name updated successfully
                                    Toast.makeText(Home.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Home.this, "Failed to update name in the database", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        });

        // Initialize the SignInButton and set an OnClickListener
        findViewById(R.id.truckInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.myTripsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MyTrips.class);
                startActivity(intent);
            }
        });

        // Handle mapsButton click
        findViewById(R.id.mapsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the SpeedTracker activity
                Intent intent = new Intent(Home.this, Location.class);
                startActivity(intent);
            }
        });

        // Handle logout button click
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase
                mAuth.signOut();

                // Redirect to the MainActivity (login screen)
                Intent intent = new Intent(Home.this, MainActivity.class);
                // Clear the back stack so that the user can't navigate back to the Home activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    // Implement the `pickImageFromGallery` method to pick an image from the gallery

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the selected image
            Uri imageUri = data.getData();

            // Upload the selected image to Firebase Storage
            uploadImageToFirebaseStorage(imageUri);
        }
    }

    // Implement the `uploadImageToFirebaseStorage` method to upload the selected image to Firebase Storage

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            StorageReference userImageRef = storageReference.child("profile_images").child(userId);

            userImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully, get the download URL
                            userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Store the image URL in the Realtime Database
                                    DatabaseReference userReference = databaseReference.child("Driver Name").child(userId);
                                    userReference.child("ProfileImageURL").setValue(imageUrl)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Image URL stored in the database
                                                    Picasso.get().load(imageUrl).into(imageView6);
                                                    Toast.makeText(Home.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Home.this, "Failed to store image URL in the database", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Home.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // ...

    private void loadUserProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            DatabaseReference userReference = databaseReference.child("Driver Name").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String driverName = dataSnapshot.child("Driver details").getValue(String.class);
                        edittext11.setText(driverName);

                        // Load the profile image
                        String imageUrl = dataSnapshot.child("ProfileImageURL").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl)
                                    .fit() // Set the fit() method to fit the image within the ImageView
                                    .into(imageView6);
                        }

                    } else {
                        edittext11.setText("");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Home.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getDriverNameLocally() {
        // Get the driver's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVER_NAME, "");
    }

    private void saveDriverNameLocally(String driverName) {
        // Save the driver's name in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DRIVER_NAME, driverName);
        editor.apply();
    }
}
