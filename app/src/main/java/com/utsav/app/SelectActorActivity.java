package com.utsav.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectActorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_actor);

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CardView btnManager = findViewById(R.id.btn_manager);
        CardView btnHost    = findViewById(R.id.btn_host);

        btnManager.setOnClickListener(v -> saveRoleAndNavigate("manager"));
        btnHost.setOnClickListener(v -> saveRoleAndNavigate("host"));
    }

    private void saveRoleAndNavigate(String role) {
        db.collection("users").document(uid)
                .update("role", role)
                .addOnSuccessListener(unused -> {
                    if (role.equals("host")) {
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        Toast.makeText(this,
                                "Manager dashboard coming soon", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save role. Try again.",
                                Toast.LENGTH_SHORT).show()
                );
    }
}