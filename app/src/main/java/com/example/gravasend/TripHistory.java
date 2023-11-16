    package com.example.gravasend;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.ImageButton;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    public class TripHistory extends AppCompatActivity {
        private ImageButton backButton;
        private TextView Cargo;
        private TextView Date;


        private FirebaseAuth auth;
        private DatabaseReference databaseReference;
        private LinearLayout tripBox;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.triphistory);

            auth = FirebaseAuth.getInstance();
            // Get the current user's ID
            String userId = auth.getCurrentUser().getUid();

            // Initialize the Firebase Database reference for the current user
            databaseReference = FirebaseDatabase.getInstance().getReference().child("TripHistory").child(userId);

            backButton = findViewById(R.id.backButton);
            tripBox = findViewById(R.id.tripBox);


            // Handle the back button click
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TripHistory.this, MyTrips.class);
                    startActivity(intent);
                }
            });

            // Read data from the Firebase database and display it
            readDataFromFirebase();
        }

        private void readDataFromFirebase() {

            ArrayList<String> tripKeys = new ArrayList<>();
            DatabaseReference tripKeysReference = databaseReference;

            tripKeysReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                        // Retrieve each trip key and add it to the list
                        String tripKey = keySnapshot.getKey();
                        tripKeys.add(tripKey);
                    }

                    // Iterate through the trip keys and read data for each key
                    for (String key : tripKeys) {
                        DatabaseReference tripReference = databaseReference.child(key);

                        tripReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot dataSnapshot = task.getResult();
                                    Log.d("history", dataSnapshot.toString());

                                    if (dataSnapshot.exists()) {
                                        View tripItemView = getLayoutInflater().inflate(R.layout.trip_item, null);
                                        TextView cargoTextView = tripItemView.findViewById(R.id.Cargo);
                                        TextView originTextView = tripItemView.findViewById(R.id.Origin);
                                        TextView dateTextView = tripItemView.findViewById(R.id.Date);

                                        String cargo = dataSnapshot.child("cargo").getValue(String.class);
                                        String weight = dataSnapshot.child("weight").getValue(String.class);
                                        String origin = dataSnapshot.child("origin").getValue(String.class);
                                        String destination = dataSnapshot.child("destination").getValue(String.class);
                                        String date = dataSnapshot.child("dateTime").getValue(String.class);
                                        String firstTenChars = date.substring(0, 10);
                                        cargoTextView.setText(cargo+" - "+weight+" cubic");
                                        originTextView.setText(origin+" - "+destination);
                                        dateTextView.setText(firstTenChars);

                                        // Add margin to the inspectionView (adjust margin value as needed)
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        layoutParams.setMargins(0, 0, 0, 16); // Adjust the margin value as needed
                                        tripItemView.setLayoutParams(layoutParams);

                                        tripBox.addView(tripItemView);
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
