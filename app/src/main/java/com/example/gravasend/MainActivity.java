package com.example.gravasend;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout unameInput;
    private TextInputLayout passInput;
    private TextInputEditText accountNumberEditText;
    private TextInputEditText passwordEditText;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(); // Replace with your desired reference name

        DatabaseReference mydb = myRef.child("login");

        // Initialize views
        unameInput = findViewById(R.id.unameInput);
        passInput = findViewById(R.id.passInput);
        accountNumberEditText = findViewById(R.id.accountNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Initialize the SignInButton and set an OnClickListener
        findViewById(R.id.SignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = accountNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if email and password are not empty
                if (!email.isEmpty() && !password.isEmpty()) {
                    // Sign in with email and password
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startHomeActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Read data from Firebase
        // Read data from Firebase as a HashMap
        mydb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the data exists
                if (dataSnapshot.exists()) {
                    // Read data as a HashMap
                    HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    // Now, you can access specific values from the HashMap
                    if (dataMap != null) {
                        // For example, if you have a key called "someKey" in the HashMap
                        Object someValue = dataMap.get("someKey");
                        if (someValue instanceof String) {
                            String stringValue = (String) someValue;
                            Log.d(TAG, "Data from Firebase: " + stringValue);
                        } else {
                            Log.d(TAG, "Data from Firebase is not a String.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
                Log.e(TAG, "Database read failed: " + error.getMessage());
            }
        });

    }
        private void startHomeActivity () {
            // Create an Intent to start the HomeActivity
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }
