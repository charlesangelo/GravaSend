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
    private ImageButton backButton;
    private Button saveButton, clearButton, selectImageButton;
    private EditText plateNumberInput, odometerInput, imageDescriptionInput;
    private CheckBox checkBox;
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    private ImageView imagePlaceholder;
    private Uri selectedImageUri;
    private String imageUrl;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(SafetyChecklist.this, MainActivity.class));
            finish();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("SafetyChecklist").child(currentUser.getUid());

        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.button4);
        clearButton = findViewById(R.id.button3);
        selectImageButton = findViewById(R.id.selectImageButton);
        plateNumberInput = findViewById(R.id.plateNumberInput);
        odometerInput = findViewById(R.id.odometerInput);
        imageDescriptionInput = findViewById(R.id.imageDescriptionInput);
        checkBox = findViewById(R.id.checkBox1);
        imagePlaceholder = findViewById(R.id.imagePlaceholder);

        loadUserData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUserData();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });
    }

    private void loadUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    plateNumberInput.setText(userData.getPlateNumber());
                    odometerInput.setText(userData.getOdometerReading());
                    imageDescriptionInput.setText(userData.getImageDescription());
                    checkBox.setChecked(userData.isChecked());
                    imageUrl = userData.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().invalidate(imageUrl);
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

        UserData userData = new UserData(plateNumber, odometerReading, imageDescription, isChecked);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            userData.setImageUrl(imageUrl);
        }

        userReference.setValue(userData);

        Toast.makeText(SafetyChecklist.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
    }

    private void clearUserData() {
        plateNumberInput.setText("");
        odometerInput.setText("");
        imageDescriptionInput.setText("");
        checkBox.setChecked(false);

        userReference.setValue(null);

        imagePlaceholder.setImageResource(R.drawable.ic_launcher_foreground);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageUrl = "";
                    Toast.makeText(SafetyChecklist.this, "Image cleared successfully.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SafetyChecklist.this, "Failed to clear image.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageUrl = "";
        }

        Toast.makeText(SafetyChecklist.this, "Data cleared successfully.", Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
            } else {
                launchImagePicker();
            }
        } else {
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                imagePlaceholder.setImageURI(selectedImageUri);
                uploadImageToFirebase(selectedImageUri);
            } else {
                Toast.makeText(SafetyChecklist.this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String userUid = currentUser.getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String imageFilename = userUid + "_" + timestamp + "_" + imageUri.getLastPathSegment();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("SafetyChecklistImages").child(userUid).child(imageFilename);

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        Log.d("ImageUrl", imageUrl);
                        saveUserData();
                        Toast.makeText(SafetyChecklist.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SafetyChecklist.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
