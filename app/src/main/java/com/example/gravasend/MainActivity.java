package com.example.gravasend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText accountNumberEditText;
    private TextInputEditText passwordEditText;
    private Button registerButton;
    private CheckBox rememberMeCheckBox;
    private Button forgotPasswordButton;
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private boolean rememberMeChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load the saved "Remember Me" state from SharedPreferences
        rememberMeChecked = sharedPreferences.getBoolean("rememberMeChecked", false);

        // If the "Remember Me" checkbox is unchecked, show the terms and conditions dialog
        if (!rememberMeChecked) {
            showTermsAndConditionsDialog();
        }

        // Initialize views
        accountNumberEditText = findViewById(R.id.accountNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.RegisterButton);
        rememberMeCheckBox = findViewById(R.id.checkBox);
        forgotPasswordButton = findViewById(R.id.button2);

        // Load the saved "Remember Me" state from SharedPreferences
        rememberMeCheckBox.setChecked(rememberMeChecked);

        // Add an OnCheckedChangeListener to the checkbox
        rememberMeCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
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

    private void showTermsAndConditionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("This document outlines the terms and conditions regarding the collection, processing, and protection of personal data under the Data Privacy Act (DPA). These terms and conditions apply to all individuals and entities interacting with JMIG" +
                "\n" +
                "1. Collection of Personal Data\n" +
                "\n" +
                "We collect personal data for specific, legitimate, and lawful purposes as allowed by the Data Privacy Act. Personal data may include, but is not limited to, the following:\n" +
                "\n" +
                "Name\n" +
                "Contact Information (email address, phone number, address)\n" +
                "Date of Birth\n" +
                "Identification Numbers (e.g., Social Security Number, National ID)\n" +
                "Financial Information\n" +
                "Employment History\n" +
                "Other data as required for our services\n" +
                "2. Processing of Personal Data\n" +
                "\n" +
                "We process personal data for the following purposes:\n" +
                "\n" +
                "To provide products and services to our customers\n" +
                "To comply with legal and regulatory obligations\n" +
                "To improve our products and services\n" +
                "For marketing and communication purposes (with your consent)\n" +
                "To ensure the security and integrity of our systems\n" +
                "3. Consent\n" +
                "\n" +
                "We seek your explicit consent before processing your personal data for marketing and communication purposes. You have the right to withdraw your consent at any time by contacting us at [contact email/phone number].\n" +
                "\n" +
                "4. Security and Confidentiality\n" +
                "\n" +
                "We take appropriate measures to protect your personal data from unauthorized access, disclosure, alteration, or destruction. We have security safeguards in place to ensure the confidentiality and integrity of your data.\n" +
                "\n" +
                "5. Data Subject Rights\n" +
                "\n" +
                "Under the Data Privacy Act, you have the following rights:\n" +
                "\n" +
                "The right to be informed\n" +
                "The right to access your personal data\n" +
                "The right to correct inaccuracies in your personal data\n" +
                "The right to object to the processing of your data\n" +
                "The right to erasure or blocking of your data\n" +
                "The right to data portability\n" +
                "To exercise these rights or make inquiries about your data, please contact us at [contact email/phone number].\n" +
                "\n" +
                "6. Data Sharing\n" +
                "\n" +
                "We may share your personal data with third parties for specific purposes, such as service providers and regulatory authorities. These third parties are also bound by data protection and confidentiality obligations.\n" +
                "\n" +
                "7. Data Retention\n" +
                "\n" +
                "We retain your personal data for the duration necessary to fulfill the purposes outlined in this document or as required by applicable laws and regulations.\n" +
                "\n" +
                "8. Changes to Terms and Conditions\n" +
                "\n" +
                "We reserve the right to amend these terms and conditions to comply with changes in the Data Privacy Act. Updated terms and conditions will be posted on our website.\n" +
                "\n" +
                "9. Contact Information\n" +
                "\n" +
                "For inquiries or requests related to your personal data and these terms and conditions, please contact us at [contact email/phone number].\n" +
                "\n" +
                "By using our services, you acknowledge and agree to these Data Privacy Act terms and conditions.");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user accepted the terms and conditions, save the state.
                rememberMeChecked = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMeChecked", true);
                editor.apply();
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user declined the terms and conditions, close the application.
                finish();
            }
        });
        builder.setCancelable(false); // Prevent the user from dismissing the dialog by tapping outside.
        builder.show();
    }
}
