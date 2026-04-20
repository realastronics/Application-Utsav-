package com.example.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SelectActorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_actor);

        CardView btnManager = findViewById(R.id.btn_manager);
        CardView btnHost    = findViewById(R.id.btn_host);

        btnManager.setOnClickListener(v -> {
            // Manager flow — route to ManagerDashboardActivity (Farhan's work)
            // For now, placeholder toast until that activity exists
            android.widget.Toast.makeText(this,
                    "Manager flow coming soon", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnHost.setOnClickListener(v -> {
            // User/Host flow — route to MainActivity (Mitali's home screen)
            Intent intent = new Intent(SelectActorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}