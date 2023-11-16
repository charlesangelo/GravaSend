package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InspectionRecords extends AppCompatActivity {
    private ImageButton backButton;



    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private LinearLayout inspectionRecordBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspectionrecords);

        // Initialize Firebase Database and Authentication
        databaseReference = FirebaseDatabase.getInstance().getReference("inspectionRecords");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        backButton = findViewById(R.id.backButton);
        inspectionRecordBox = findViewById(R.id.inspectionRecordBox);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InspectionRecords.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Load data from Firebase and set it to TextViews
        loadInspectionRecords();
    }

    private void loadInspectionRecords() {
        ArrayList<String> keys = new ArrayList<>();
        DatabaseReference userReference = databaseReference.child(currentUser.getUid());


        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each trip key and add it to the list
                    String inspectionKey = keySnapshot.getKey();
                    keys.add(inspectionKey);

                }

                // Iterate through the trip keys and read data for each key
                for (String key : keys) {
                    Log.d("inspection", key);
                    DatabaseReference inspectionRef = databaseReference.child(currentUser.getUid()).child(key);
                    inspectionRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {

                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();

                                if (dataSnapshot.exists()) {

                                    View inspectionView = getLayoutInflater().inflate(R.layout.inspection_item, null);
                                    TextView inspectionTypeTextView = inspectionView.findViewById(R.id.inspectionType);
                                    TextView nextInspectionDateTextView = inspectionView.findViewById(R.id.date);
                                    TextView verdictTextView = inspectionView.findViewById(R.id.verdict);


                                    String inspectionType = dataSnapshot.child("inspectionType").getValue(String.class);
                                    String verdict = dataSnapshot.child("verdict").getValue(String.class);
                                    String nextInspectionDate = dataSnapshot.child("nextInspectionDate").getValue(String.class);
                                    String firstTenChars = nextInspectionDate.substring(0, 10);
                                    inspectionTypeTextView.setText(inspectionType);
                                    nextInspectionDateTextView.setText(firstTenChars);
                                    verdictTextView.setText(verdict);

                                    // Add margin to the inspectionView (adjust margin value as needed)
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(0, 0, 0, 16); // Adjust the margin value as needed
                                    inspectionView.setLayoutParams(layoutParams);

                                    inspectionRecordBox.addView(inspectionView);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}
