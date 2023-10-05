package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SafetyChecklist extends AppCompatActivity {
    private ImageButton b1;
    private Button b2;
    private Button b3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetychecklist);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.button11);
        b3 = findViewById(R.id.button4);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);
            }

        });
      /*  findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SafetyChecklist.this, CurrentTrip.class);
                startActivity(intent);
            }

        }); */
    }
}