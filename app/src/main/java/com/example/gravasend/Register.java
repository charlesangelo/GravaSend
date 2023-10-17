package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gravasend.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button returnButton; // Add this button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        returnButton = findViewById(R.id.returnID); // Initialize the return button

        // Set an OnClickListener for the return button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the Login activity
                Intent intent = new Intent(Register.this, MainActivity.class); // Replace "LoginActivity" with your actual class name.

                // Start the LoginActivity
                startActivity(intent);
            }
        });

        // Set an OnClickListener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                // Check if email and password are not empty
                if (!email.isEmpty() && !password.isEmpty()) {
                    // Register the user with Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registration success, go to the main activity
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                        finish();
                                    } else {
                                        // Registration failed, display an error message
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(Register.this, "Registration failed: " + errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Register.this, "Please enter both email and password.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
