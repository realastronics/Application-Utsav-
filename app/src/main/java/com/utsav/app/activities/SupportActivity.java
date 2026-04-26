package com.utsav.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utsav.app.R;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Wire dummy clicks for now
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
