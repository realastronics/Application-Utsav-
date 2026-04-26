package com.utsav.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.R;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileSetupActivity extends AppCompatActivity {

    // ── Fields ────────────────────────────────────────────────────────────────
    private TextInputEditText etBio, etLocation, etPhone,
            etInstagram, etLinkedin, etWebsite,
            etPortfolio1, etPortfolio2, etPortfolio3;

    // Specialisation checkboxes
    private CheckBox cbWedding, cbCorporate, cbBirthday,
            cbConcert, cbConference, cbPrivate;

    private MaterialButton btnSave;
    private ProgressBar    progressBar;

    // ── Firebase ──────────────────────────────────────────────────────────────
    private FirebaseFirestore db;
    private String uid;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bindViews();
        setupSkipButton();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    // ── Bind ──────────────────────────────────────────────────────────────────
    private void bindViews() {
        etBio         = findViewById(R.id.et_bio);
        etLocation    = findViewById(R.id.et_location);
        etPhone       = findViewById(R.id.et_phone);
        etInstagram   = findViewById(R.id.et_instagram);
        etLinkedin    = findViewById(R.id.et_linkedin);
        etWebsite     = findViewById(R.id.et_website);
        etPortfolio1  = findViewById(R.id.et_portfolio_url_1);
        etPortfolio2  = findViewById(R.id.et_portfolio_url_2);
        etPortfolio3  = findViewById(R.id.et_portfolio_url_3);

        cbWedding     = findViewById(R.id.cb_wedding);
        cbCorporate   = findViewById(R.id.cb_corporate);
        cbBirthday    = findViewById(R.id.cb_birthday);
        cbConcert     = findViewById(R.id.cb_concert);
        cbConference  = findViewById(R.id.cb_conference);
        cbPrivate     = findViewById(R.id.cb_private);

        btnSave       = findViewById(R.id.btn_save_profile);
        progressBar   = findViewById(R.id.progressBar);
    }

    // ── Skip ──────────────────────────────────────────────────────────────────
    private void setupSkipButton() {
        findViewById(R.id.btn_skip).setOnClickListener(v -> goToDashboard());
    }

    // ── Save ──────────────────────────────────────────────────────────────────
    private void saveProfile() {
        // Collect event types
        List<String> eventTypes = new ArrayList<>();
        if (cbWedding.isChecked())    eventTypes.add("Wedding");
        if (cbCorporate.isChecked())  eventTypes.add("Corporate");
        if (cbBirthday.isChecked())   eventTypes.add("Birthday");
        if (cbConcert.isChecked())    eventTypes.add("Concert");
        if (cbConference.isChecked()) eventTypes.add("Conference");
        if (cbPrivate.isChecked())    eventTypes.add("Private Party");

        // Collect portfolio URLs (filter empties)
        List<String> portfolioUrls = new ArrayList<>();
        for (String url : Arrays.asList(
                text(etPortfolio1), text(etPortfolio2), text(etPortfolio3))) {
            if (!url.isEmpty()) portfolioUrls.add(url);
        }

        // Social links map
        Map<String, String> socials = new HashMap<>();
        socials.put("instagram", text(etInstagram));
        socials.put("linkedin",  text(etLinkedin));
        socials.put("website",   text(etWebsite));

        // Merge everything into the users/{uid} document
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("bio",          text(etBio));
        profileData.put("location",     text(etLocation));
        profileData.put("phone",        text(etPhone));
        profileData.put("eventTypes",   eventTypes);
        profileData.put("portfolioUrls", portfolioUrls);
        profileData.put("socials",      socials);
        profileData.put("profileComplete", true);

        setLoading(true);

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update(profileData)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this,
                            "Profile saved! Welcome to Utsav 🎉",
                            Toast.LENGTH_SHORT).show();
                    goToDashboard();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this,
                            "Couldn't save profile. Try again.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    private void goToDashboard() {
        Intent intent = new Intent(this, ManagerDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String text(android.widget.TextView tv) {
        return tv.getText() != null ? tv.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}