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
import com.bumptech.glide.Glide;
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

import java.util.HashMap;

public class SafetyChecklist extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        // Get the user's UID
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize the Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Safety Check");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        Button clearButton = findViewById(R.id.button3);
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
                // Get the user's UID
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String path = "Safety Check/" + userUid;

                // Clear EditText fields
                EditText imageDescriptionInput3 = findViewById(R.id.imageDescriptionInput3);
                EditText imageDescriptionInput1 = findViewById(R.id.imageDescriptionInput1);
                EditText imageDescriptionInput4 = findViewById(R.id.imageDescriptionInput4);
                EditText imageDescriptionInput2 = findViewById(R.id.imageDescriptionInput2);
                EditText imageDescriptionInput6 = findViewById(R.id.imageDescriptionInput6);
                EditText imageDescriptionInput = findViewById(R.id.imageDescriptionInput);
                EditText plateNumberInput = findViewById(R.id.plateNumberInput);
                EditText odometerInput = findViewById(R.id.odometerInput);

                imageDescriptionInput3.setText("");
                imageDescriptionInput1.setText("");
                imageDescriptionInput4.setText("");
                imageDescriptionInput2.setText("");
                imageDescriptionInput6.setText("");
                imageDescriptionInput.setText("");
                plateNumberInput.setText("");
                odometerInput.setText("");

                // Uncheck the CheckBoxes
                CheckBox checkBox3 = findViewById(R.id.checkBox3);
                CheckBox checkBox1 = findViewById(R.id.checkBox1);
                CheckBox checkBox4 = findViewById(R.id.checkBox4);
                CheckBox checkBox2 = findViewById(R.id.checkBox2);
                CheckBox checkBox6 = findViewById(R.id.checkBox6);
                CheckBox checkBox = findViewById(R.id.checkBox);

                checkBox3.setChecked(false);
                checkBox1.setChecked(false);
                checkBox4.setChecked(false);
                checkBox2.setChecked(false);
                checkBox6.setChecked(false);
                checkBox.setChecked(false);

                // Clear the multi-line EditText fields
                EditText editTextTextMultiLine4 = findViewById(R.id.editTextTextMultiLine4);
                EditText editTextTextMultiLine1 = findViewById(R.id.editTextTextMultiLine1);
                EditText editTextTextMultiLine5 = findViewById(R.id.editTextTextMultiLine5);
                EditText editTextTextMultiLine2 = findViewById(R.id.editTextTextMultiLine2);
                EditText editTextTextMultiLine6 = findViewById(R.id.editTextTextMultiLine6);
                EditText editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

                editTextTextMultiLine4.setText("");
                editTextTextMultiLine1.setText("");
                editTextTextMultiLine5.setText("");
                editTextTextMultiLine2.setText("");
                editTextTextMultiLine6.setText("");
                editTextTextMultiLine.setText("");

                // Remove data from the database
                databaseReference.child(path).removeValue();

                // You may also want to clear the selected image if there is one
                if (selectedImageView != null) {
                    selectedImageView.setImageResource(0);
                    selectedImageUri = null;
                }
            }
        });
        // Attach click listeners to the Select Image buttons
        selectImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder3;
                openFileChooser();
            }
        });

        selectImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder1;
                openFileChooser();
            }
        });

        selectImageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder5;
                openFileChooser();
            }
        });

        selectImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder2;
                openFileChooser();
            }
        });

        selectImageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder6;
                openFileChooser();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageView = imagePlaceholder;
                openFileChooser();
            }
        });

        Button saveButton = findViewById(R.id.button4);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from EditText fields
                String description3 = imageDescriptionInput3.getText().toString();
                String description1 = imageDescriptionInput1.getText().toString();
                String description4 = imageDescriptionInput4.getText().toString();
                String description2 = imageDescriptionInput2.getText().toString();
                String description6 = imageDescriptionInput6.getText().toString();
                String description = imageDescriptionInput.getText().toString();
                String plateNumber = plateNumberInput.getText().toString();
                String odometer = odometerInput.getText().toString();

                // Get the state of checkboxes as boolean values
                boolean checked3 = checkBox3.isChecked();
                boolean checked1 = checkBox1.isChecked();
                boolean checked4 = checkBox4.isChecked();
                boolean checked2 = checkBox2.isChecked();
                boolean checked6 = checkBox6.isChecked();
                boolean checked = checkBox.isChecked();

                // Get the notes from EditText fields
                String notes4 = editTextTextMultiLine4.getText().toString();
                String notes1 = editTextTextMultiLine1.getText().toString();
                String notes5 = editTextTextMultiLine5.getText().toString();
                String notes2 = editTextTextMultiLine2.getText().toString();
                String notes6 = editTextTextMultiLine6.getText().toString();
                String notes = editTextTextMultiLine.getText().toString();

                HashMap<String, Object> data = new HashMap<>();
                data.put("Description3", description3);
                data.put("Description1", description1);
                data.put("Description4", description4);
                data.put("Description2", description2);
                data.put("Description6", description6);
                data.put("Description", description);
                data.put("PlateNumber", plateNumber);
                data.put("Odometer", odometer);
                data.put("CheckBox3", checked3);
                data.put("CheckBox1", checked1);
                data.put("CheckBox4", checked4);
                data.put("CheckBox2", checked2);
                data.put("CheckBox6", checked6);
                data.put("CheckBox", checked);
                data.put("Notes4", notes4);
                data.put("Notes1", notes1);
                data.put("Notes5", notes5);
                data.put("Notes2", notes2);
                data.put("Notes6", notes6);
                data.put("Notes", notes);

                // Update the data in Firebase under the user's path
                String path = "Safety Check/" + userUid;
                databaseReference.child(path).updateChildren(data);

                // Upload the selected image to Firebase Storage
                uploadImageToStorage(userUid);
            }
        });

        // Retrieve and populate user data from Firebase
        retrieveAndPopulateUserData(userUid, imageDescriptionInput3, imageDescriptionInput1, imageDescriptionInput4,
                imageDescriptionInput2, imageDescriptionInput6, imageDescriptionInput, plateNumberInput, odometerInput,
                checkBox3, checkBox1, checkBox4, checkBox2, checkBox6, checkBox, editTextTextMultiLine4,
                editTextTextMultiLine1, editTextTextMultiLine5, editTextTextMultiLine2, editTextTextMultiLine6,
                editTextTextMultiLine);
    }

    // Define the openFileChooser method here
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Define the uploadImageToStorage method here
    private void uploadImageToStorage(final String userUid) {
        if (selectedImageUri != null) {
            // Create a reference to the location where you want to store the image in Firebase Storage.
            StorageReference imageRef = storageReference.child("ChecklistImages/" + userUid + "/" + selectedImageUri.getLastPathSegment());

            // Upload the image to Firebase Storage.
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle a successful upload here
                            // Retrieve the download URL for the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Handle the download URL
                                    String imageUrl = downloadUri.toString();

                                    // Now you have the image URL, which you can store in the database under the user's data
                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("ImageURL", imageUrl);
                                    String path = "Safety Check/" + userUid;
                                    databaseReference.child(path).updateChildren(data);

                                    // Load and display the image in the selectedImageView
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (selectedImageView != null) {
                                                // Use Glide to load and display the image
                                                Glide.with(getApplicationContext())
                                                        .load(imageUrl)
                                                        .into(selectedImageView);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }

    // Retrieve and populate user data from Firebase
    private void retrieveAndPopulateUserData(String userUid, final EditText imageDescriptionInput3, final EditText imageDescriptionInput1, final EditText imageDescriptionInput4, final EditText imageDescriptionInput2, final EditText imageDescriptionInput6, final EditText imageDescriptionInput, final EditText plateNumberInput, final EditText odometerInput, final CheckBox checkBox3, final CheckBox checkBox1, final CheckBox checkBox4, final CheckBox checkBox2, final CheckBox checkBox6, final CheckBox checkBox, final EditText editTextTextMultiLine4, final EditText editTextTextMultiLine1, final EditText editTextTextMultiLine5, final EditText editTextTextMultiLine2, final EditText editTextTextMultiLine6, final EditText editTextTextMultiLine) {
        // Construct the reference to the user's data in Firebase
        String path = "Safety Check/" + userUid;

        // Retrieve the user's data from Firebase
        databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate the EditText fields with the retrieved data
                    imageDescriptionInput3.setText(dataSnapshot.child("Description3").getValue(String.class));
                    imageDescriptionInput1.setText(dataSnapshot.child("Description1").getValue(String.class));
                    imageDescriptionInput4.setText(dataSnapshot.child("Description4").getValue(String.class));
                    imageDescriptionInput2.setText(dataSnapshot.child("Description2").getValue(String.class));
                    imageDescriptionInput6.setText(dataSnapshot.child("Description6").getValue(String.class));
                    imageDescriptionInput.setText(dataSnapshot.child("Description").getValue(String.class));
                    plateNumberInput.setText(dataSnapshot.child("PlateNumber").getValue(String.class));
                    odometerInput.setText(dataSnapshot.child("Odometer").getValue(String.class));

                    // Set the state of the CheckBoxes
                    checkBox3.setChecked(dataSnapshot.child("CheckBox3").getValue(Boolean.class));
                    checkBox1.setChecked(dataSnapshot.child("CheckBox1").getValue(Boolean.class));
                    checkBox4.setChecked(dataSnapshot.child("CheckBox4").getValue(Boolean.class));
                    checkBox2.setChecked(dataSnapshot.child("CheckBox2").getValue(Boolean.class));
                    checkBox6.setChecked(dataSnapshot.child("CheckBox6").getValue(Boolean.class));
                    checkBox.setChecked(dataSnapshot.child("CheckBox").getValue(Boolean.class));

                    // Populate the multi-line EditText fields
                    editTextTextMultiLine4.setText(dataSnapshot.child("Notes4").getValue(String.class));
                    editTextTextMultiLine1.setText(dataSnapshot.child("Notes1").getValue(String.class));
                    editTextTextMultiLine5.setText(dataSnapshot.child("Notes5").getValue(String.class));
                    editTextTextMultiLine2.setText(dataSnapshot.child("Notes2").getValue(String.class));
                    editTextTextMultiLine6.setText(dataSnapshot.child("Notes6").getValue(String.class));
                    editTextTextMultiLine.setText(dataSnapshot.child("Notes").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }
}
