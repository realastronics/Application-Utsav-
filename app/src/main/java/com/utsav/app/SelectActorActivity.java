package com.utsav.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utsav.app.activities.ProfileSetupActivity;
import com.utsav.app.utils.Constants;
import java.util.HashMap;
import java.util.Map;

public class SelectActorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_actor);

        String uid   = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name  = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        findViewById(R.id.btn_host).setOnClickListener(v ->
                saveRoleAndProceed(uid, name, email, Constants.ROLE_HOST));

        findViewById(R.id.btn_manager).setOnClickListener(v ->
                saveRoleAndProceed(uid, name, email, Constants.ROLE_MANAGER));
    }

    private void saveRoleAndProceed(String uid, String name,
                                    String email, String role) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_NAME,  name  != null ? name  : "");
        data.put(Constants.FIELD_EMAIL, email != null ? email : "");
        data.put(Constants.FIELD_ROLE,  role);

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Intent intent;
                    if (Constants.ROLE_MANAGER.equals(role)) {
                        // ── NEW: manager goes to profile setup first ──────
                        intent = new Intent(this, ProfileSetupActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Setup failed. Try again.", Toast.LENGTH_SHORT).show());
    }
}