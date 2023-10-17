package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpcomingInspection extends AppCompatActivity {
    private ImageButton backButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private TextView dateView1;
    private TextView type1;
    private TextView dateView2;
    private TextView type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcominginspections);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("upcomingInspections");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        backButton = findViewById(R.id.backButton);
        dateView1 = findViewById(R.id.DateView1);
        type1 = findViewById(R.id.Type1);
        dateView2 = findViewById(R.id.DateView2);
        type2 = findViewById(R.id.Type2);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpcomingInspection.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Load data from Firebase and set it to TextViews
        loadUpcomingInspections();
    }

    private void loadUpcomingInspections() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userReference = databaseReference.child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String date1 = dataSnapshot.child("date1").getValue(String.class);
                        String type1Value = dataSnapshot.child("type1").getValue(String.class);
                        String date2 = dataSnapshot.child("date2").getValue(String.class);
                        String type2Value = dataSnapshot.child("type2").getValue(String.class);

                        // Set data to TextViews
                        dateView1.setText(date1);
                        type1.setText(type1Value);
                        dateView2.setText(date2);
                        type2.setText(type2Value);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error here if needed
                }
            });
        }
    }
}
