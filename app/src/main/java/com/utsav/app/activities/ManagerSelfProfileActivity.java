package com.utsav.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.AuthActivity;
import com.utsav.app.R;
import com.utsav.app.utils.Constants;

import java.util.List;

public class ManagerSelfProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private TextView tvName, tvTitle, tvLocation, tvBio;
    private TextView tvInstagram, tvLinkedin, tvWebsite;
    private LinearLayout llPortfolioGrid;
    private LinearLayout llSpecialisations;

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_self_profile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bindViews();
        setupSidebar();
        loadProfile();

        // Bottom nav wiring
        setupBottomNav();

        // Edit profile → ProfileSetupActivity (re-use it for editing)
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSetupActivity.class));
        });
    }

    private void bindViews() {
        drawerLayout     = findViewById(R.id.drawerLayout);
        tvName           = findViewById(R.id.tvManagerName);
        tvTitle          = findViewById(R.id.tvManagerTitle);
        tvLocation       = findViewById(R.id.tvManagerLocation);
        tvBio            = findViewById(R.id.tvManagerBio);
        tvInstagram      = findViewById(R.id.tvInstagram);
        tvLinkedin       = findViewById(R.id.tvLinkedin);
        tvWebsite        = findViewById(R.id.tvWebsite);
        llPortfolioGrid  = findViewById(R.id.llPortfolioGrid);
        llSpecialisations= findViewById(R.id.llSpecialisations);

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));
    }

    private void setupSidebar() {
        NavigationView nav = findViewById(R.id.navView);
        View header = nav.getHeaderView(0);
        TextView tvN = header.findViewById(R.id.nav_user_name);
        TextView tvE = header.findViewById(R.id.nav_user_email);

        db.collection(Constants.COLLECTION_USERS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvN.setText(doc.getString("name"));
                        tvE.setText(doc.getString("email"));
                    }
                });

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sidebar_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, AuthActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else if (id == R.id.manager_sidebar_notifications) {
                startActivity(new Intent(this, com.utsav.app.activities.NotificationsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    private void loadProfile() {
        db.collection(Constants.COLLECTION_USERS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String name = doc.getString("name");
                    String location = doc.getString("location");
                    String bio  = doc.getString("bio");

                    tvName.setText(name     != null ? name     : "Manager");
                    tvLocation.setText(location != null ? location : "Location not set");
                    tvBio.setText(bio       != null ? bio       : "No bio added yet.");

                    // Event types → chip row
                    List<String> types = (List<String>) doc.get("eventTypes");
                    if (types != null && !types.isEmpty()) {
                        tvTitle.setText(types.get(0) + " Manager");
                        buildSpecialisationChips(types);
                    }

                    // Socials
                    Object socialsObj = doc.get("socials");
                    if (socialsObj instanceof java.util.Map) {
                        java.util.Map<String, String> s =
                                (java.util.Map<String, String>) socialsObj;
                        bindSocial(tvInstagram, s.get("instagram"));
                        bindSocial(tvLinkedin,  s.get("linkedin"));
                        bindSocial(tvWebsite,   s.get("website"));
                    }

                    // Portfolio URLs
                    List<String> urls = (List<String>) doc.get("portfolioUrls");
                    buildPortfolioGrid(urls);
                });
    }

    private void bindSocial(TextView tv, String value) {
        if (value == null || value.isEmpty()) {
            tv.setText("—");
            return;
        }
        tv.setText(value);
        tv.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        value.startsWith("http") ? value : "https://" + value)));
            } catch (Exception ignored) {}
        });
    }

    private void buildSpecialisationChips(List<String> types) {
        llSpecialisations.removeAllViews();
        for (String t : types) {
            TextView chip = new TextView(this);
            chip.setText(t);
            chip.setTextSize(12f);
            chip.setTextColor(0xFF9381FF);
            chip.setBackgroundResource(R.drawable.bg_chip_inactive);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);
            chip.setPadding(32, 16, 32, 16);
            llSpecialisations.addView(chip);
        }
    }

    private void buildPortfolioGrid(List<String> urls) {
        // Portfolio placeholder tiles — swap with Glide/Picasso when ready
        llPortfolioGrid.removeAllViews();
        if (urls == null || urls.isEmpty()) return;

        // Show up to 6 tiles in a 3-col flow
        LinearLayout row = null;
        for (int i = 0; i < Math.min(urls.size(), 6); i++) {
            if (i % 3 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                rlp.bottomMargin = 8;
                row.setLayoutParams(rlp);
                llPortfolioGrid.addView(row);
            }
            View tile = buildPortfolioTile(urls.get(i));
            row.addView(tile);
        }
    }

    private View buildPortfolioTile(String url) {
        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                (int)(getResources().getDisplayMetrics().density * 100));
        lp.weight  = 1;
        lp.setMarginEnd(6);
        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setBackgroundColor(0xFFEEF0FF);
        iv.setImageResource(R.drawable.ic_event); // placeholder; swap with Glide

        // When Glide is added:
        // Glide.with(this).load(url).centerCrop().into(iv);
        iv.setOnClickListener(v -> {
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
            catch (Exception ignored) {}
        });
        return iv;
    }

    private void setupBottomNav() {
        // Wire each nav item manually since this uses a custom bottom nav layout
        findViewById(R.id.nav_dashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, ManagerDashboardActivity.class));
            finish();
        });
        // Self profile item is current screen — no action
        findViewById(R.id.nav_notifications).setOnClickListener(v ->
                startActivity(new Intent(this, com.utsav.app.activities.NotificationsActivity.class)));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else super.onBackPressed();
    }
}