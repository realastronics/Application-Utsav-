// java/com/utsav/app/activities/ManagerDashboardActivity.java

package com.utsav.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utsav.app.R;

public class ManagerDashboard extends AppCompatActivity {

    Button btnAccept, btnDecline, btnChat1, btnChat2;
    ImageView btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_dash);

        btnMenu = findViewById(R.id.btnMenu);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);
        btnChat1 = findViewById(R.id.btnChat1);
        btnChat2 = findViewById(R.id.btnChat2);

        btnMenu.setOnClickListener(v ->
                Toast.makeText(this, "Sidebar Open", Toast.LENGTH_SHORT).show());

        btnAccept.setOnClickListener(v ->
                Toast.makeText(this, "Request Accepted", Toast.LENGTH_SHORT).show());

        btnDecline.setOnClickListener(v ->
                Toast.makeText(this, "Request Declined", Toast.LENGTH_SHORT).show());

        btnChat1.setOnClickListener(v ->
                Toast.makeText(this, "Opening Chat", Toast.LENGTH_SHORT).show());

        btnChat2.setOnClickListener(v ->
                Toast.makeText(this, "Opening Chat", Toast.LENGTH_SHORT).show());
    }
}