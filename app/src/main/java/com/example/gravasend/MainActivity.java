package com.example.gravasend;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox; // Change to CheckBox
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout unameInput;
    private TextInputLayout passInput;
    private TextInputEditText accountNumberEditText;
    private TextInputEditText passwordEditText;
    private Button registerButton;
    private CheckBox rememberMeCheckBox; // Changed to CheckBox
    private Button forgotPasswordButton;
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize views
        unameInput = findViewById(R.id.unameInput);
        passInput = findViewById(R.id.passInput);
        accountNumberEditText = findViewById(R.id.accountNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.RegisterButton);
        rememberMeCheckBox = findViewById(R.id.checkBox); // Changed to CheckBox
        forgotPasswordButton = findViewById(R.id.button2);

        // Load the saved "Remember Me" state from SharedPreferences
        boolean rememberMeChecked = sharedPreferences.getBoolean("rememberMeChecked", false);
        rememberMeCheckBox.setChecked(rememberMeChecked);

        // Add an OnCheckedChangeListener to the checkbox
        rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the "Remember Me" state to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMeChecked", isChecked);
                editor.apply();
            }
        });

        // Check if the "Remember Me" checkbox is checked and populate email and password if needed
        if (rememberMeChecked) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            accountNumberEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
        }

        // Initialize the Register button and set an OnClickListener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity when the "Register" button is clicked
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        // Initialize the "Forgot Password" button and set an OnClickListener
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's email from the email EditText
                String email = accountNumberEditText.getText().toString().trim();

                if (!email.isEmpty()) {
                    // Send a password reset email to the user's email address
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Password reset email sent. Check your email inbox.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize the SignInButton and set an OnClickListener
        findViewById(R.id.SignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = accountNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if the "Remember Me" checkbox is checked
                if (rememberMeChecked) {
                    // Save email and password to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();
                } else {
                    // Clear saved email and password from SharedPreferences when unchecked
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("email");
                    editor.remove("password");
                    editor.apply();
                }

                // Rest of your sign-in logic
                if (!email.isEmpty() && !password.isEmpty()) {
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
                                        Log.e(TAG, "signInWithEmail:failure", task.getException());
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
    }

    private void startHomeActivity() {
        // Create an Intent to start the HomeActivity
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
