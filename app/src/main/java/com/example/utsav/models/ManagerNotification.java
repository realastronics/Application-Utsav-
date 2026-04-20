package com.example.utsav.models;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.utsav.R;

public class ManagerPerspectiveNotificationActivity extends AppCompatActivity {

    private ImageButton btnBack, btnMarkAllRead;
    private TextView tabAll, tabActivity, tabSystem;
    private View tabIndicator;
    private CardView cardEventConfirmed, cardMessage1, cardSubscription, cardMessage2;
    private ImageButton navHome, navRequests, navStats, navChat, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_perspective_notification);

        initViews();
        setupTabListeners();
        setupCardListeners();
        setupBottomNav();
    }

    private void initViews() {
        btnBack        = findViewById(R.id.btnBack);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        tabAll         = findViewById(R.id.tabAll);
        tabActivity    = findViewById(R.id.tabActivity);
        tabSystem      = findViewById(R.id.tabSystem);
        tabIndicator   = findViewById(R.id.tabIndicator);

        cardEventConfirmed = findViewById(R.id.cardEventConfirmed);
        cardMessage1       = findViewById(R.id.cardMessageReceived1);
        cardSubscription   = findViewById(R.id.cardSubscription);
        cardMessage2       = findViewById(R.id.cardMessageReceived2);

        navHome     = findViewById(R.id.navHome);
        navRequests = findViewById(R.id.navRequests);
        navStats    = findViewById(R.id.navStats);
        navChat     = findViewById(R.id.navChat);
        navProfile  = findViewById(R.id.navProfile);

        btnBack.setOnClickListener(v -> finish());
        btnMarkAllRead.setOnClickListener(v ->
                Toast.makeText(this, "All notifications marked as read",
                        Toast.LENGTH_SHORT).show());
    }

    private void setupTabListeners() {
        tabAll.setOnClickListener(v      -> selectTab(tabAll));
        tabActivity.setOnClickListener(v -> selectTab(tabActivity));
        tabSystem.setOnClickListener(v   -> selectTab(tabSystem));
    }

    private void selectTab(TextView selected) {
        int grey   = getColor(R.color.utsav_text_secondary);
        int purple = getColor(R.color.utsav_purple);

        tabAll.setTextColor(grey);
        tabActivity.setTextColor(grey);
        tabSystem.setTextColor(grey);
        tabAll.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabActivity.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabSystem.setTypeface(null, android.graphics.Typeface.NORMAL);

        selected.setTextColor(purple);
        selected.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void setupCardListeners() {
        cardEventConfirmed.setOnClickListener(v -> {
            // TODO: open event detail screen
        });
        cardMessage1.setOnClickListener(v -> {
            startActivity(new Intent(this, ManagerChatActivity.class));
        });
        cardSubscription.setOnClickListener(v -> {
            startActivity(new Intent(this, PromotionVisibilityActivity.class));
        });
        cardMessage2.setOnClickListener(v -> {
            startActivity(new Intent(this, ManagerChatActivity.class));
        });
    }

    private void setupBottomNav() {
        // Stats tab is active in this screen per design
        navHome.setOnClickListener(v     -> navigateDashboard("home"));
        navRequests.setOnClickListener(v -> navigateDashboard("requests"));
        navStats.setOnClickListener(v    -> { /* already here */ });
        navChat.setOnClickListener(v     -> navigateDashboard("chat"));
        navProfile.setOnClickListener(v  -> navigateDashboard("profile"));
    }

    private void navigateDashboard(String tab) {
        Intent i = new Intent(this, ManagerDashboardActivity.class);
        i.putExtra("openTab", tab);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}