package com.utsav.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.fragments.EventsFragment;
import com.utsav.app.fragments.HomeFragment;
import com.utsav.app.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout     drawerLayout;
    private NavigationView   navView;

    // ---------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView      = findViewById(R.id.nav_view);

        setupBottomNav();
        setupSidebar();
        populateSidebarHeader();

        // Default screen
        loadFragment(new HomeFragment());
    }

    // ---------------------------------------------------------------
    // Bottom navigation
    // ---------------------------------------------------------------

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());

            } else if (id == R.id.nav_navigate) {
                loadFragment(new EventsFragment());

            } else if (id == R.id.nav_create) {
                // TODO: wire to CreateEventFragment / Activity when built
                Toast.makeText(this, "Create event — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_chat) {
                // TODO: wire to ChatListFragment when built
                Toast.makeText(this, "Chats — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }

            return true;
        });
    }

    // ---------------------------------------------------------------
    // Sidebar — matches Figma: Options header with the items listed
    // ---------------------------------------------------------------

    private void setupSidebar() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.sidebar_profile) {
                loadFragment(new ProfileFragment());

            } else if (id == R.id.sidebar_events) {
                // TODO: MyEventsFragment
                Toast.makeText(this, "My Events — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_saved) {
                // TODO: SavedManagersFragment
                Toast.makeText(this, "Saved Managers — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_notifications) {
                Toast.makeText(this, "Notifications — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_support) {
                Toast.makeText(this, "Contact Support — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_create_event) {
                Toast.makeText(this, "Event Create — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_theme) {
                Toast.makeText(this, "Theme toggle — coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                return true;   // skip closing drawer — activity is finishing
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    // ---------------------------------------------------------------
    // Sidebar header  — real name + email from Firestore
    // ---------------------------------------------------------------

    private void populateSidebarHeader() {
        View headerView = navView.getHeaderView(0);
        TextView tvName  = headerView.findViewById(R.id.nav_user_name);
        TextView tvEmail = headerView.findViewById(R.id.nav_user_email);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvName.setText(doc.getString("name"));
                        tvEmail.setText(doc.getString("email"));
                    }
                });
    }

    // ---------------------------------------------------------------
    // Public API — called by HomeFragment hamburger tap
    // ---------------------------------------------------------------

    public void openSidebar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}