package com.example.gravasend;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SafetyChecklist extends AppCompatActivity {
    // Define the UI elements
    private ImageButton backButton;
    private Button saveButton, clearButton, selectImageButton;
    private EditText plateNumberInput, odometerInput, imageDescriptionInput;
    private CheckBox checkBox;
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    private ImageView imagePlaceholder;
    private Uri selectedImageUri;
    private String imageUrl;

    // Request code for image selection
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not authenticated, redirect to login or sign-up
            startActivity(new Intent(SafetyChecklist.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("Safety Checklist").child(currentUser.getUid());

        // Initialize views
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.button4);
        clearButton = findViewById(R.id.button3);
        selectImageButton = findViewById(R.id.selectImageButton);
        plateNumberInput = findViewById(R.id.plateNumberInput);
        odometerInput = findViewById(R.id.odometerInput);
        imageDescriptionInput = findViewById(R.id.imageDescriptionInput);
        checkBox = findViewById(R.id.checkBox);
        imagePlaceholder = findViewById(R.id.imagePlaceholder);

        // Load existing data when the activity starts
        loadUserData();

        // Handle the "Back" button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);
            }
        });

        // Handle the "Save" button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        // Handle the "Clear" button click
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUserData();
            }
        });

        // Set an onClickListener for the imagePlaceholder
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for runtime permissions on Android Marshmallow and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SafetyChecklist.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Permission not granted, request it
                        ActivityCompat.requestPermissions(SafetyChecklist.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
                    } else {
                        // Permission already granted, launch image picker
                        launchImagePicker();
                    }
                } else {
                    // Launch image picker (no need to request permissions on older Android versions)
                    launchImagePicker();
                }
            }
        });
    }

    // Inside loadUserData method
    private void loadUserData() {
        // Read data from Firebase and populate UI fields
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);

                    // Populate UI fields with loaded data
                    plateNumberInput.setText(userData.getPlateNumber());
                    odometerInput.setText(userData.getOdometerReading());
                    imageDescriptionInput.setText(userData.getImageDescription());
                    checkBox.setChecked(userData.isChecked());

                    // Load the image URL
                    imageUrl = userData.getImageUrl();

                    // Load the image if the URL exists
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Clear Picasso's memory cache
                        Picasso.get().invalidate(imageUrl);

                        // Load the new image using Picasso
                        Picasso.get().load(imageUrl).into(imagePlaceholder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void saveUserData() {
        String plateNumber = plateNumberInput.getText().toString();
        String odometerReading = odometerInput.getText().toString();
        String imageDescription = imageDescriptionInput.getText().toString();
        boolean isChecked = checkBox.isChecked();

        // Create a data object and save it to Firebase
        UserData userData = new UserData(plateNumber, odometerReading, imageDescription, isChecked);

        // Set the existing image URL if it exists (to retain the image)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            userData.setImageUrl(imageUrl);
        }

        userReference.setValue(userData);

        Toast.makeText(SafetyChecklist.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
    }

    private void clearUserData() {
        // Clear all UI fields
        plateNumberInput.setText("");
        odometerInput.setText("");
        imageDescriptionInput.setText("");
        checkBox.setChecked(false);

        // Clear the data in Firebase by setting it to null
        userReference.setValue(null);

        // Clear the imagePlaceholder
        imagePlaceholder.setImageResource(R.drawable.ic_launcher_foreground);

        // Check if an image was previously uploaded
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Delete the image from Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Image deleted successfully from Firebase Storage
                    imageUrl = ""; // Set imageUrl to an empty string
                    Toast.makeText(SafetyChecklist.this, "Image cleared successfully.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the error
                    Toast.makeText(SafetyChecklist.this, "Failed to clear image.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No image to clear, just set imageUrl to an empty string
            imageUrl = "";
        }

        Toast.makeText(SafetyChecklist.this, "Data cleared successfully.", Toast.LENGTH_SHORT).show();
    }

    private void launchImagePicker() {
        // Launch the image picker
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // User has successfully picked an image
            selectedImageUri = data.getData();

            if (selectedImageUri != null) { // Check if selectedImageUri is not null
                // Set the selected image to the imagePlaceholder ImageView
                imagePlaceholder.setImageURI(selectedImageUri);

                // Upload the image to Firebase Storage and save its URL in Firebase Realtime Database
                uploadImageToFirebase(selectedImageUri);
            } else {
                Toast.makeText(SafetyChecklist.this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not authenticated, handle accordingly
            return;
        }

        String userUid = currentUser.getUid();

        // Create a unique filename for the image using the current timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String imageFilename = userUid + "_" + timestamp + "_" + imageUri.getLastPathSegment();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("SafetyChecklistImages").child(userUid).child(imageFilename);

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully, get the download URL
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();

                        // Log the imageUrl to check if it's correct
                        Log.d("ImageUrl", imageUrl);

                        // Update the user's data with the image URL
                        saveUserData(); // Save the user data again to include the image URL

                        Toast.makeText(SafetyChecklist.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error
                Toast.makeText(SafetyChecklist.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
