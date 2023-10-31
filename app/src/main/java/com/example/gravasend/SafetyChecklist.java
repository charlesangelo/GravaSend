package com.example.gravasend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class SafetyChecklist extends AppCompatActivity {

    private static final String IMAGE_URL_KEY_PREFIX = "image_url_";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView selectedImageView;
    private Uri selectedUri;
    private String userUid;
    private SharedPreferences sharedPreferences;
    private ArrayList<Uri> selectedImageUriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        // Get the user's UID
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize SharedPreferences for image URL caching
        sharedPreferences = getSharedPreferences("image_urls", MODE_PRIVATE);
        // Initialize the Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        Button clearButton = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4); // Added button4

        View backButton = findViewById(R.id.backButton);
        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SafetyChecklist.this, CurrentTrip.class));
            }
        });

        // Find the EditText fields, CheckBoxes, and Save Changes button
        final EditText imageDescriptionInput3 = findViewById(R.id.imageDescriptionInput3);
        final EditText imageDescriptionInput1 = findViewById(R.id.imageDescriptionInput1);
        final EditText imageDescriptionInput4 = findViewById(R.id.imageDescriptionInput4);
        final EditText imageDescriptionInput2 = findViewById(R.id.imageDescriptionInput2);
        final EditText imageDescriptionInput6 = findViewById(R.id.imageDescriptionInput6);
        final EditText imageDescriptionInput = findViewById(R.id.imageDescriptionInput);
        final EditText plateNumberInput = findViewById(R.id.plateNumberInput);
        final EditText odometerInput = findViewById(R.id.odometerInput);

        final CheckBox checkBox3 = findViewById(R.id.checkBox3);
        final CheckBox checkBox1 = findViewById(R.id.checkBox1);
        final CheckBox checkBox4 = findViewById(R.id.checkBox4);
        final CheckBox checkBox2 = findViewById(R.id.checkBox2);
        final CheckBox checkBox6 = findViewById(R.id.checkBox6);
        final CheckBox checkBox = findViewById(R.id.checkBox);

        final EditText editTextTextMultiLine4 = findViewById(R.id.editTextTextMultiLine4);
        final EditText editTextTextMultiLine1 = findViewById(R.id.editTextTextMultiLine1);
        final EditText editTextTextMultiLine5 = findViewById(R.id.editTextTextMultiLine5);
        final EditText editTextTextMultiLine2 = findViewById(R.id.editTextTextMultiLine2);
        final EditText editTextTextMultiLine6 = findViewById(R.id.editTextTextMultiLine6);
        final EditText editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

        // Find ImageView placeholders and Select Image buttons
        final ImageView imagePlaceholder3 = findViewById(R.id.imagePlaceholder3);
        final Button selectImageButton4 = findViewById(R.id.selectImageButton4);
        final ImageView imagePlaceholder1 = findViewById(R.id.imagePlaceholder1);
        final Button selectImageButton1 = findViewById(R.id.selectImageButton1);
        final ImageView imagePlaceholder5 = findViewById(R.id.imagePlaceholder5);
        final Button selectImageButton5 = findViewById(R.id.selectImageButton5);
        final ImageView imagePlaceholder2 = findViewById(R.id.imagePlaceholder2);
        final Button selectImageButton2 = findViewById(R.id.selectImageButton2);
        final ImageView imagePlaceholder6 = findViewById(R.id.imagePlaceholder6);
        final Button selectImageButton6 = findViewById(R.id.selectImageButton6);
        final ImageView imagePlaceholder = findViewById(R.id.imagePlaceholder);
        final Button selectImageButton = findViewById(R.id.selectImageButton);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear EditText fields
                imageDescriptionInput3.setText("");
                imageDescriptionInput1.setText("");
                imageDescriptionInput4.setText("");
                imageDescriptionInput2.setText("");
                imageDescriptionInput6.setText("");
                imageDescriptionInput.setText("");
                plateNumberInput.setText("");
                odometerInput.setText("");

                // Uncheck the CheckBoxes
                checkBox3.setChecked(false);
                checkBox1.setChecked(false);
                checkBox4.setChecked(false);
                checkBox2.setChecked(false);
                checkBox6.setChecked(false);
                checkBox.setChecked(false);

                // Clear the multi-line EditText fields
                editTextTextMultiLine4.setText("");
                editTextTextMultiLine1.setText("");
                editTextTextMultiLine5.setText("");
                editTextTextMultiLine2.setText("");
                editTextTextMultiLine6.setText("");
                editTextTextMultiLine.setText("");

                // Remove data from the database
                String path = "Safety Checklist/" + userUid;
                databaseReference.child(path).removeValue();

                // You may also want to clear the selected image if there is one
                if (selectedImageView != null) {
                    selectedImageView.setImageResource(0);
                    selectedImageUri = null;
                }
            }
        });

        // Set click listeners for the Select Image buttons
        selectImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder3;
                int imageId = 3; // Set the image ID
                openFileChooser(imageId);
            }
        });

        selectImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder1;
                int imageId = 1; // Set the image ID
                openFileChooser(imageId);
            }
        });

        selectImageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder5;
                int imageId = 5; // Set the image ID
                openFileChooser(imageId);
            }
        });

        selectImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder2;
                int imageId = 2; // Set the image ID
                openFileChooser(imageId);
            }
        });

        selectImageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder6;
                int imageId = 6; // Set the image ID
                openFileChooser(imageId);
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder;
                int imageId = 4; // Set the image ID
                openFileChooser(imageId);
            }
        });

        // Retrieve and populate user data from Firebase for each image
        retrieveAndPopulateUserData(userUid, "Image1", imageDescriptionInput3, checkBox3, editTextTextMultiLine4, plateNumberInput, odometerInput);
        retrieveAndPopulateUserData(userUid, "Image2", imageDescriptionInput1, checkBox1, editTextTextMultiLine1, plateNumberInput, odometerInput);
        retrieveAndPopulateUserData(userUid, "Image3", imageDescriptionInput4, checkBox4, editTextTextMultiLine5, plateNumberInput, odometerInput);
        retrieveAndPopulateUserData(userUid, "Image4", imageDescriptionInput2, checkBox2, editTextTextMultiLine2, plateNumberInput, odometerInput);
        retrieveAndPopulateUserData(userUid, "Image5", imageDescriptionInput6, checkBox6, editTextTextMultiLine6, plateNumberInput, odometerInput);
        retrieveAndPopulateUserData(userUid, "Image6", imageDescriptionInput, checkBox, editTextTextMultiLine, plateNumberInput, odometerInput);

        // Set click listener for the Save Changes button (button4)
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Upload data for each input field
                uploadUserData("Image1", imageDescriptionInput3, checkBox3, editTextTextMultiLine4, plateNumberInput, odometerInput);
                uploadUserData("Image2", imageDescriptionInput1, checkBox1, editTextTextMultiLine1, plateNumberInput, odometerInput);
                uploadUserData("Image3", imageDescriptionInput4, checkBox4, editTextTextMultiLine5, plateNumberInput, odometerInput);
                uploadUserData("Image4", imageDescriptionInput2, checkBox2, editTextTextMultiLine2, plateNumberInput, odometerInput);
                uploadUserData("Image5", imageDescriptionInput6, checkBox6, editTextTextMultiLine6, plateNumberInput, odometerInput);
                uploadUserData("Image6", imageDescriptionInput, checkBox, editTextTextMultiLine, plateNumberInput, odometerInput);
            }

            private void uploadUserData(String imageKey, EditText imageDescriptionInput, CheckBox checkBox, EditText editTextTextMultiLine, EditText plateNumberInput, EditText odometerInput) {
                // Construct the path in the database based on imageKey
                String path = "Safety Check/" + userUid + "/" + imageKey;

                // Extract data from the input fields
                String imageDescription = imageDescriptionInput.getText().toString();
                boolean isChecked = checkBox.isChecked();
                String notes = editTextTextMultiLine.getText().toString();
                String plateNumber = plateNumberInput.getText().toString();
                String odometerValue = odometerInput.getText().toString();

                // Create a HashMap to store the data
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("Description", imageDescription);
                userData.put("CheckBox", isChecked);
                userData.put("Notes", notes);
                userData.put("PlateNumber", plateNumber);
                userData.put("Odometer", odometerValue);

                // Upload the data to the Firebase Realtime Database
                databaseReference.child(path).setValue(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully uploaded
                                // You can add any necessary feedback or actions here
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors that occur during data upload
                                // You can provide error messages or take appropriate actions
                            }
                        });
            }
        });
    }

    // Update the openFileChooser method to add selected URIs to the list and include the image ID
    private void openFileChooser(int imageId) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("imageId", imageId); // Pass the image ID to onActivityResult
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of selecting an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedUri = data.getData(); // Assign the selected URI to the class-level selectedUri
            selectedImageUriList.add(selectedUri);
            int imageId = data.getIntExtra("imageId", 0); // Retrieve the image ID

            // You can set the selected image in the selectedImageView if needed.
            if (selectedImageView != null) {
                selectedImageView.setImageURI(selectedUri);
            }

            // Upload the selected image to Firebase Storage and cache the URL
            uploadImageToStorageAndCacheURL(userUid, selectedUri, imageId);
        }
    }

    // Define the uploadImageToStorageAndCacheURL method here
    private void uploadImageToStorageAndCacheURL(final String userUid, final Uri selectedUri, final int imageId) {
        if (selectedUri != null) {
            String imagePath = "ChecklistImages/" + userUid + "/Image" + imageId + "/" + selectedUri.getLastPathSegment();
            StorageReference imageRef = storageReference.child(imagePath);

            imageRef.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    final String imageUrl = downloadUri.toString();

                                    // Cache the image URL
                                    cacheImageURL(imageId, imageUrl);

                                    // Add the image tag to the Firebase Realtime Database
                                    addImageTagToDatabase(imageId, imageUrl);

                                    // Load and display the image
                                    loadAndDisplayImage(imageId, imageUrl, selectedImageView);
                                }
                            });
                        }
                    });
        }
    }

    private void addImageTagToDatabase(int imageId, String imageUrl) {
        // Construct the path for storing image tags in the database
        String path = "ImageTags/" + userUid + "/Image" + imageId;

        // Create a HashMap to store the image tag
        HashMap<String, Object> imageTagData = new HashMap<>();
        imageTagData.put("ImageUrl", imageUrl);

        // Upload the image tag to the Firebase Realtime Database
        databaseReference.child(path).setValue(imageTagData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image tag successfully uploaded to the database
                        // You can add any necessary feedback or actions here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during data upload
                        // You can provide error messages or take appropriate actions
                    }
                });
    }


    private void cacheImageURL(int imageId, String imageUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_URL_KEY_PREFIX + imageId, imageUrl);
        editor.apply();
    }

    private void loadAndDisplayImage(int imageId, String imageUrl, ImageView imageView) {
        if (sharedPreferences.contains(IMAGE_URL_KEY_PREFIX + imageId)) {
            String cachedImageUrl = sharedPreferences.getString(IMAGE_URL_KEY_PREFIX + imageId, "");
            if (!cachedImageUrl.isEmpty()) {
                // Use Picasso to load and display the image from the cached URL
                Picasso.get().load(cachedImageUrl).into(imageView);
            }
        } else if (!imageUrl.isEmpty()) {
            // Use Picasso to load and display the image from the Firebase Storage URL
            Picasso.get().load(imageUrl).into(imageView);
            cacheImageURL(imageId, imageUrl);
        }
    }

    // Retrieve and populate user data from Firebase
    private void retrieveAndPopulateUserData(String userUid, String imageIdentifier, final EditText imageDescriptionInput, final CheckBox checkBox, final EditText editTextTextMultiLine, final EditText plateNumberInput, final EditText odometerInput) {
        // Construct the reference to the user's data in Firebase
        String path = "Safety Check/" + userUid + "/" + imageIdentifier;

        // Retrieve the user's data from Firebase
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate the EditText fields with the retrieved data
                    imageDescriptionInput.setText(dataSnapshot.child("Description").getValue(String.class));

                    // Set the state of the CheckBox
                    checkBox.setChecked(dataSnapshot.child("CheckBox").getValue(Boolean.class));

                    // Populate the multi-line EditText field
                    editTextTextMultiLine.setText(dataSnapshot.child("Notes").getValue(String.class));

                    // Check if PlateNumber and Odometer exist and populate the corresponding fields
                    if (dataSnapshot.child("PlateNumber").exists()) {
                        String plateNumber = dataSnapshot.child("PlateNumber").getValue(String.class);
                        plateNumberInput.setText(plateNumber);
                    }
                    if (dataSnapshot.child("Odometer").exists()) {
                        String odometer = dataSnapshot.child("Odometer").getValue(String.class);
                        odometerInput.setText(odometer);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

}
