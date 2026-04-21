package com.utsav.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AuthActivity extends AppCompatActivity {

    // UI
    private TextView tabLogin, tabRegister;
    private TextInputLayout tilName, tilEmail, tilPassword;
    private TextInputEditText etName, etEmail, etPassword;
    private Button btnAuth;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Firebase first — needed by routeByRole before any UI is inflated
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Fast-path: returning user — skip auth UI entirely
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setContentView(R.layout.activity_splash); // reuse splash as silent loading bg
            routeByRole(currentUser.getUid());
            return;
        }

        // New / logged-out user — show the auth card normally
        setContentView(R.layout.activity_auth);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        tabLogin     = findViewById(R.id.tab_login);
        tabRegister  = findViewById(R.id.tab_register);
        tilName      = findViewById(R.id.til_name);
        tilEmail     = findViewById(R.id.til_email);
        tilPassword  = findViewById(R.id.til_password);
        btnAuth      = findViewById(R.id.btn_auth);
        progressBar  = findViewById(R.id.progress_bar);

        // Get the inner EditTexts from TextInputLayouts
        etName     = (TextInputEditText) tilName.getEditText();
        etEmail    = (TextInputEditText) tilEmail.getEditText();
        etPassword = (TextInputEditText) tilPassword.getEditText();
    }

    private void setListeners() {
        tabLogin.setOnClickListener(v -> switchMode(true));
        tabRegister.setOnClickListener(v -> switchMode(false));
        btnAuth.setOnClickListener(v -> handleAuth());
    }

    private void handleAuth() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (isLoginMode) {
            login(email, password);
        } else {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                setLoading(false);
                return;
            }
            register(email, password, name);
        }
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    routeByRole(uid);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void register(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    // Save basic user document to Firestore
                    // Role is not set yet — SelectActorActivity will set it
                    saveNewUserToFirestore(uid, name, email);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void saveNewUserToFirestore(String uid, String name, String email) {
        // Build the user document — no role yet
        java.util.Map<String, Object> user = new java.util.HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", "");   // empty until SelectActorActivity sets it

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    // New user — go pick a role
                    startActivity(new Intent(AuthActivity.this, SelectActorActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Failed to save user data",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void routeByRole(String uid) {
        // Check Firestore for this user's saved role
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    // Guard: progressBar is null when coming from the returning-user
                    // fast-path (activity_splash inflated, bindViews() never called)
                    if (progressBar != null) setLoading(false);

                    if (document.exists()) {
                        String role = document.getString("role");
                        if (role == null || role.isEmpty()) {
                            // Role not set yet — send to SelectActor
                            startActivity(new Intent(this, SelectActorActivity.class));
                        } else if (role.equals("host")) {
                            startActivity(new Intent(this, MainActivity.class));
                        } else if (role.equals("manager")) {
                            // Route to ManagerDashboardActivity when Farhan builds it
                            // Temporary: toast for now
                            Toast.makeText(this, "Manager dashboard coming soon",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        finish();
                    } else {
                        // Document missing — send to SelectActor to be safe
                        startActivity(new Intent(this, SelectActorActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Same guard for the failure path
                    if (progressBar != null) setLoading(false);
                    Toast.makeText(this, "Network error. Try again.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void switchMode(boolean loginMode) {
        isLoginMode = loginMode;
        if (loginMode) {
            tabLogin.setTextColor(0xFF7C5CBF);
            tabLogin.setTypeface(null, android.graphics.Typeface.BOLD);
            tabRegister.setTypeface(null, android.graphics.Typeface.NORMAL);
            tabRegister.setTextColor(0xFFAAAAAA);
            tilName.setVisibility(View.GONE);
            btnAuth.setText("Login");
        } else {
            tabRegister.setTextColor(0xFF7C5CBF);
            tabLogin.setTextColor(0xFFAAAAAA);
            tilName.setVisibility(View.VISIBLE);
            btnAuth.setText("Create Account");
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAuth.setEnabled(!loading);
    }
}