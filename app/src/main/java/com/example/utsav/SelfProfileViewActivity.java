package com.example.utsav;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utsav.models.Manager;
import com.example.utsav.utils.DataProvider;

public class SelfProfileViewActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageView ivProfilePhoto;
    private TextView tvManagerName, tvManagerTitle, tvLocation;
    private Button btnEditProfile, btnShare;
    private TextView chipCorporate, chipWeddings, chipTech, chipPrivate;
    private ImageView portfolioImg1, portfolioImg2, portfolioImg3;
    private ImageView portfolioImg4, portfolioImg5, portfolioImg6;
    private TextView tvViewAll;
    private ImageButton navHome, navRequests, navStats, navChat, navProfile;

    private Manager currentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfprofileactivity);

        initViews();
        loadData();
        populateUI();
        setupListeners();
        setupBottomNav();
    }

    private void initViews() {
        btnMenu        = findViewById(R.id.btnMenu);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvManagerName  = findViewById(R.id.tvManagerName);
        tvManagerTitle = findViewById(R.id.tvManagerTitle);
        tvLocation     = findViewById(R.id.tvLocation);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnShare       = findViewById(R.id.btnShare);
        chipCorporate  = findViewById(R.id.chipCorporate);
        chipWeddings   = findViewById(R.id.chipWeddings);
        chipTech       = findViewById(R.id.chipTech);
        chipPrivate    = findViewById(R.id.chipPrivate);
        portfolioImg1  = findViewById(R.id.portfolioImg1);
        portfolioImg2  = findViewById(R.id.portfolioImg2);
        portfolioImg3  = findViewById(R.id.portfolioImg3);
        portfolioImg4  = findViewById(R.id.portfolioImg4);
        portfolioImg5  = findViewById(R.id.portfolioImg5);
        portfolioImg6  = findViewById(R.id.portfolioImg6);
        tvViewAll      = findViewById(R.id.tvViewAll);
        navHome        = findViewById(R.id.navHome);
        navRequests    = findViewById(R.id.navRequests);
        navStats       = findViewById(R.id.navStats);
        navChat        = findViewById(R.id.navChat);
        navProfile     = findViewById(R.id.navProfile);
    }

    private void loadData() {

        // Load currently logged-in manager.
        // Phase 3: replace with Firebase Auth + Firestore fetch.

        String managerId = getIntent().getStringExtra("manager_id");

        if (managerId != null) {
            currentManager = DataProvider.getManagerById(managerId);
        }

        if (currentManager == null) {
            currentManager = DataProvider.getSampleManagers().get(0);
        }
    }

    private void populateUI() {
        if (currentManager == null) return;

        tvManagerName.setText(currentManager.getName());
        tvLocation.setText(currentManager.getLocation());
        tvManagerTitle.setText("Senior Event Manager");

        // Profile photo
        int resId = getResources().getIdentifier(
                currentManager.getProfileImageUrl(), "drawable", getPackageName());
        if (resId != 0) ivProfilePhoto.setImageResource(resId);

        // Chips from event types
        TextView[] chipViews = {chipCorporate, chipWeddings, chipTech, chipPrivate};
        java.util.List<String> types = currentManager.getEventTypes();
        if (types != null) {
            for (int i = 0; i < chipViews.length; i++) {
                if (i < types.size()) {
                    chipViews[i].setText(types.get(i));
                    chipViews[i].setVisibility(View.VISIBLE);
                } else {
                    chipViews[i].setVisibility(View.GONE);
                }
            }
        }

        // Portfolio images
        ImageView[] portfolioViews = {
                portfolioImg1, portfolioImg2, portfolioImg3,
                portfolioImg4, portfolioImg5, portfolioImg6
        };
        java.util.List<String> images = currentManager.getPortfolioImages();
        if (images != null) {
            for (int i = 0; i < portfolioViews.length; i++) {
                if (i < images.size()) {
                    int imgRes = getResources().getIdentifier(
                            images.get(i), "drawable", getPackageName());
                    if (imgRes != 0) portfolioViews[i].setImageResource(imgRes);
                }
            }
        }
    }

    private void setupListeners() {
        btnMenu.setOnClickListener(v -> { /* TODO: drawer */ });

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileSetupActivity.class);
            if (currentManager != null)
                intent.putExtra("manager_id", currentManager.getId());
            startActivity(intent);
        });

        btnShare.setOnClickListener(v -> {
            if (currentManager == null) return;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check out " + currentManager.getName() + " on Utsav!");
            startActivity(Intent.createChooser(shareIntent, "Share Profile"));
        });

        tvViewAll.setOnClickListener(v ->
                Toast.makeText(this, "Full portfolio coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNav() {
        // Profile tab active
        navHome.setOnClickListener(v     -> navigateDashboard("home"));
        navRequests.setOnClickListener(v -> navigateDashboard("requests"));
        navStats.setOnClickListener(v    -> navigateDashboard("stats"));
        navChat.setOnClickListener(v     -> navigateDashboard("chat"));
        navProfile.setOnClickListener(v  -> { /* already here */ });
    }

    private void navigateDashboard(String tab) {
        Intent i = new Intent(this, ManagerDashboard.class);
        i.putExtra("openTab", tab);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}