package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MyTruck extends AppCompatActivity {
    private ImageButton b1;
    private Button b2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytruck);

        b1 = findViewById(R.id.backButton);
        b2 = findViewById(R.id.doneButton);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTruck.this, TruckInformation.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyTruck.this, TruckInformation.class);
                startActivity(intent);
            }
        });
    }
}