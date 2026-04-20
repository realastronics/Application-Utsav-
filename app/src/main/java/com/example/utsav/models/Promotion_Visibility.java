package com.example.utsav.models;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourteam.utsav.R;

public class PromotionVisibilityActivity extends AppCompatActivity {

    // Top bar
    private ImageButton btnMenu;

    // Back
    private ImageButton btnBack;

    // Boost card
    private TextView tvPrice;
    private Button btnSubscribe;

    // Visibility card
    private ProgressBar progressReach;
    private TextView tvPotential;

    // Bottom nav
    private ImageButton navHome, navRequests, navStats, navChat, navProfile;

    // --- Pricing (change here to update everywhere) ---
    private static final int MONTHLY_PRICE_INR = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_visibility);

        initViews();
        populateData();
        setupListeners();
        setupBottomNav();
    }

    // -------------------------------------------------------
    //  INIT
    // -------------------------------------------------------
    private void initViews() {
        btnMenu       = findViewById(R.id.btnMenu);
        btnBack       = findViewById(R.id.btnBack);
        tvPrice       = findViewById(R.id.tvPrice);
        btnSubscribe  = findViewById(R.id.btnSubscribe);
        progressReach = findViewById(R.id.progressReach);
        tvPotential   = findViewById(R.id.tvPotential);

        navHome     = findViewById(R.id.navHome);
        navRequests = findViewById(R.id.navRequests);
        navStats    = findViewById(R.id.navStats);
        navChat     = findViewById(R.id.navChat);
        navProfile  = findViewById(R.id.navProfile);
    }

    // -------------------------------------------------------
    //  POPULATE
    // -------------------------------------------------------
    private void populateData() {
        // Price
        tvPrice.setText("INR " + MONTHLY_PRICE_INR + " /month");

        // Reach progress — free tier is roughly 22% of PRO reach
        progressReach.setProgress(22);

        // Potential uplift label
        tvPotential.setText("+350% potential");
    }

    // -------------------------------------------------------
    //  LISTENERS
    // -------------------------------------------------------
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            // TODO: open side drawer / menu
        });

        btnSubscribe.setOnClickListener(v -> handleSubscribe());
    }

    private void handleSubscribe() {
        // Phase 2: UI stub — just show confirmation toast.
        // Phase 3: integrate payment gateway / Firestore subscription flag.
        Toast.makeText(this,
                "Redirecting to payment... INR " + MONTHLY_PRICE_INR + "/month",
                Toast.LENGTH_SHORT).show();

        // TODO (Phase 3):
        // Intent paymentIntent = new Intent(this, PaymentActivity.class);
        // paymentIntent.putExtra("plan", "PRO");
        // paymentIntent.putExtra("price", MONTHLY_PRICE_INR);
        // startActivity(paymentIntent);
    }

    // -------------------------------------------------------
    //  BOTTOM NAVIGATION
    // -------------------------------------------------------
    private void setupBottomNav() {
        // Stats (this screen's tab) is already tinted purple in XML

        navHome.setOnClickListener(v -> navigateDashboard("home"));
        navRequests.setOnClickListener(v -> navigateDashboard("requests"));
        navStats.setOnClickListener(v -> { /* already here */ });
        navChat.setOnClickListener(v -> navigateDashboard("chat"));
        navProfile.setOnClickListener(v -> navigateDashboard("profile"));
    }

    private void navigateDashboard(String tab) {
        Intent intent = new Intent(this, ManagerDashboardActivity.class);
        intent.putExtra("openTab", tab);
        // Bring existing dashboard to front rather than creating a new one
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
