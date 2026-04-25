package com.example.utsav;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Statistic_Insights extends AppCompatActivity {

    // Top bar
    private ImageButton btnMenu;

    // Stat cards
    private TextView tvProfileViews, tvProfileViewsChange;
    private TextView tvTotalRequests, tvRequestsChange;
    private TextView tvEngagement, tvEngagementChange;
    private TextView tvProfileReach, tvReachChange;

    // Service breakdown rows
    private TextView tvCorporatePercent;
    private TextView tvPrivatePercent;
    private TextView tvConsultPercent;

    // Bottom nav
    private ImageButton navHome, navRequests, navStats, navChat, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_insights);

        initViews();
        populateData();
        setupBottomNav();
    }

    // -------------------------------------------------------
    //  INIT
    // -------------------------------------------------------
    private void initViews() {
        btnMenu = findViewById(R.id.btnMenu);

        tvProfileViews       = findViewById(R.id.tvProfileViews);
        tvProfileViewsChange = findViewById(R.id.tvProfileViewsChange);
        tvTotalRequests      = findViewById(R.id.tvTotalRequests);
        tvRequestsChange     = findViewById(R.id.tvRequestsChange);
        tvEngagement         = findViewById(R.id.tvEngagement);
        tvEngagementChange   = findViewById(R.id.tvEngagementChange);
        tvProfileReach       = findViewById(R.id.tvProfileReach);
        tvReachChange        = findViewById(R.id.tvReachChange);

        tvCorporatePercent   = findViewById(R.id.tvCorporatePercent);
        tvPrivatePercent     = findViewById(R.id.tvPrivatePercent);
        tvConsultPercent     = findViewById(R.id.tvConsultPercent);

        navHome     = findViewById(R.id.navHome);
        navRequests = findViewById(R.id.navRequests);
        navStats    = findViewById(R.id.navStats);
        navChat     = findViewById(R.id.navChat);
        navProfile  = findViewById(R.id.navProfile);

        btnMenu.setOnClickListener(v -> { /* TODO: open drawer */ });
    }

    // -------------------------------------------------------
    //  POPULATE  (replace with Firebase fetch in Phase 3)
    // -------------------------------------------------------
    private void populateData() {
        tvProfileViews.setText("1,284");
        tvProfileViewsChange.setText("+18%");

        tvTotalRequests.setText("45");
        tvRequestsChange.setText("+5%");

        tvEngagement.setText("4.8%");
        tvEngagementChange.setText("+0.8%");

        tvProfileReach.setText("24,512");
        tvReachChange.setText("+35.4%");

        tvCorporatePercent.setText("45%");
        tvPrivatePercent.setText("30%");
        tvConsultPercent.setText("25%");
    }

    // -------------------------------------------------------
    //  BOTTOM NAV
    // -------------------------------------------------------
    private void setupBottomNav() {
        // Stats tab active — tinted purple in XML
        navHome.setOnClickListener(v     -> navigateDashboard("home"));
        navRequests.setOnClickListener(v -> navigateDashboard("requests"));
        navStats.setOnClickListener(v    -> { /* already here */ });
        navChat.setOnClickListener(v     -> navigateDashboard("chat"));
        navProfile.setOnClickListener(v  -> navigateDashboard("profile"));
    }

    private void navigateDashboard(String tab) {
        Intent i = new Intent(this, ManagerDashboard.class);
        i.putExtra("openTab", tab);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}