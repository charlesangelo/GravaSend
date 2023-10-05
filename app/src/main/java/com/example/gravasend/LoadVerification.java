package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoadVerification extends AppCompatActivity {
    private ImageButton b1;
    private Button b2;
    private Button b3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadverification);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.button55);
        b3 = findViewById(R.id.button6);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadVerification.this, CurrentTrip.class);
                startActivity(intent);
            }

        });
    }
}