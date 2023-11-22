package com.example.gravasend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import android.content.SharedPreferences;
import android.widget.Toast;

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
    private FirebaseUser currentUser;
    private Button button4;
    private EditText odometerInput;
    private EditText plateNumberInput;
    private MaintenanceManager maintenanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        maintenanceManager = MaintenanceManager.getInstance();
        maintenanceManager.startMaintenanceCheck(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize SharedPreferences for image URL caching
        sharedPreferences = getSharedPreferences("image_urls", MODE_PRIVATE);
        // Initialize the Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");


        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        Button clearButton = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4); // Added button4
        checkIfUserHasTripDashboard();
        databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");
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
        odometerInput = findViewById(R.id.odometerInput);
        plateNumberInput=findViewById(R.id.plateNumberInput);

        final CheckBox checkBox3 = findViewById(R.id.checkBox3);
        final CheckBox checkBox1 = findViewById(R.id.checkBox1);
        final CheckBox checkBox4 = findViewById(R.id.checkBox4);
        final CheckBox checkBox2 = findViewById(R.id.checkBox2);
        final CheckBox checkBox6 = findViewById(R.id.checkBox6);
        final CheckBox checkBox = findViewById(R.id.checkBox);


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


                odometerInput.setText("");

                // Uncheck the CheckBoxes
                checkBox3.setChecked(false);
                checkBox1.setChecked(false);
                checkBox4.setChecked(false);
                checkBox2.setChecked(false);
                checkBox6.setChecked(false);
                checkBox.setChecked(false);



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
        retrieveAndPopulateUserData(userUid, "Suspension System", imageDescriptionInput3, checkBox3);
        retrieveAndPopulateUserData(userUid, "Brake System", imageDescriptionInput1, checkBox1);
        retrieveAndPopulateUserData(userUid, "Steering System", imageDescriptionInput4, checkBox4);
        retrieveAndPopulateUserData(userUid, "Tires and Wheels", imageDescriptionInput2, checkBox2);
        retrieveAndPopulateUserData(userUid, "Safety Equipments", imageDescriptionInput6, checkBox6);
        retrieveAndPopulateUserData(userUid, "Lights and Reflectors", imageDescriptionInput, checkBox);
        retrieveAndPopulateMileageData();


        // Set click listener for the Save Changes button (button4)
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                uploadUserData("suspension", imageDescriptionInput3, checkBox3);
                uploadUserData("brake", imageDescriptionInput1, checkBox1);
                uploadUserData("steering", imageDescriptionInput4, checkBox4);
                uploadUserData("tireswheels", imageDescriptionInput2, checkBox2);
                uploadUserData("safetyequipment", imageDescriptionInput6, checkBox6);
                uploadUserData("lights", imageDescriptionInput, checkBox);
                uploadOdometerPlateValue(odometerInput);
                databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");
                // Upload images for each image placeholder
                uploadImageForPlaceholder(imagePlaceholder3, 3);
                uploadImageForPlaceholder(imagePlaceholder1, 1);
                uploadImageForPlaceholder(imagePlaceholder5, 5);
                uploadImageForPlaceholder(imagePlaceholder2, 2);
                uploadImageForPlaceholder(imagePlaceholder6, 6);
                uploadImageForPlaceholder(imagePlaceholder, 4);
                Toast.makeText(SafetyChecklist.this, "Safety Checklist Saved.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);

            }


            private void uploadOdometerPlateValue( EditText odometerInput) {
                // Construct the path in the database based on imageKey
                String path = userUid  + "/mileage";

                // Extract the odometer value from the input field
                String odometerValueString = odometerInput.getText().toString();
                int odometerValue = Integer.parseInt(odometerValueString);

                databaseReference = FirebaseDatabase.getInstance().getReference("trucks");
                databaseReference.child(path).setValue(odometerValue)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Odometer value successfully uploaded
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


            private void uploadUserData(String imageKey, EditText imageDescriptionInput, CheckBox checkBox) {
                // Construct the path in the database based on imageKey
                String path = userUid ;


                boolean isChecked = checkBox.isChecked();

                // Create a HashMap to store the data
                HashMap<String, Object> userData = new HashMap<>();
                userData.put(imageKey, isChecked);



                databaseReference.child(path).updateChildren(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Handle success if needed
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors that occur during data update
                                // You can provide error messages or take appropriate actions
                            }
                        });

                databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");

            }
        });
    }

    private void checkIfUserHasTripDashboard() {
        String userId = currentUser.getUid();

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Trip Dashboard");
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {

                        button4.setEnabled(false);
                        Toast.makeText(SafetyChecklist.this, "You do not have a trip.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error here if needed
                }
            });
        }
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
// Define the uploadImageToStorageAndCacheURL method as previously shown

    // Define the uploadImageForPlaceholder method to handle image uploads
    private void uploadImageForPlaceholder(ImageView imageView, int imageId) {
        if (selectedImageUriList.size() > imageId - 1) {
            Uri selectedUri = selectedImageUriList.get(imageId - 1);
            if (selectedUri != null) {
                uploadImageToStorageAndCacheURL(userUid, selectedUri, imageId);
                // You can also set the selected image in the selectedImageView if needed.
                if (imageView != null) {
                    imageView.setImageURI(selectedUri);
                }
            }
        }
    }

// Continue with the rest of your code, such as the button4 click listener

    private void addImageTagToDatabase(int imageId, String imageUrl) {
        // Construct the path for storing image tags in the database
        String path =  userUid + "/Image" + imageId;

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

    private void retrieveAndPopulateMileageData() {


        databaseReference = FirebaseDatabase.getInstance().getReference("trucks");
        databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String odometerValue = dataSnapshot.child("mileage").getValue(Integer.class).toString();
                    odometerInput.setText(odometerValue);
                    String plateNo = dataSnapshot.child("plateNo").getValue(String.class);
                    plateNumberInput.setText((plateNo));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Safety Checklist");
    }

    // Retrieve and populate user data from Firebase
    private void retrieveAndPopulateUserData(String userUid, String imageIdentifier, final EditText imageDescriptionInput, final CheckBox checkBox    ) {
        // Construct the reference to the user's data in Firebase
        String path =  userUid + "/" + imageIdentifier;

        // Retrieve the user's data from Firebase
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate the EditText fields with the retrieved data
                    //imageDescriptionInput.setText(dataSnapshot.child("Description").getValue(String.class));

                    // Set the state of the CheckBox
                    //checkBox.setChecked(dataSnapshot.child("CheckBox").getValue(Boolean.class));




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }




}
