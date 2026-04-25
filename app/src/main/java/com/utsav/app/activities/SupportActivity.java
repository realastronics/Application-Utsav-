// java/com/example/utsav/SupportActivity.java
package com.utsav.app.activities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utsav.app.R;

public class SupportActivity extends AppCompatActivity {

    Button btnSupport, btnEmail, btnChat;
    TextView tvViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support);

        btnSupport = findViewById(R.id.btnSupport);
        btnEmail = findViewById(R.id.btnEmail);
        btnChat = findViewById(R.id.btnChat);
        tvViewAll = findViewById(R.id.tvViewAll);

        btnSupport.setOnClickListener(v ->
                Toast.makeText(this, "Support Request Clicked", Toast.LENGTH_SHORT).show());

        btnEmail.setOnClickListener(v ->
                Toast.makeText(this, "Email Support Clicked", Toast.LENGTH_SHORT).show());

        btnChat.setOnClickListener(v ->
                Toast.makeText(this, "Live Chat Clicked", Toast.LENGTH_SHORT).show());

        tvViewAll.setOnClickListener(v ->
                Toast.makeText(this, "View All Tickets", Toast.LENGTH_SHORT).show());
    }
}