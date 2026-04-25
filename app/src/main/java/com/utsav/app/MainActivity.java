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

import com.utsav.app.activities.SupportActivity;
import com.utsav.app.fragments.EventsFragment;
import com.utsav.app.fragments.HomeFragment;
import com.utsav.app.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        setupBottomNav();
        setupSidebar();
        populateSidebarHeader();

        loadFragment(new HomeFragment());
    }

    // ---------------------------------------------------
    // Bottom Navigation
    // ---------------------------------------------------

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());

            } else if (id == R.id.nav_navigate) {
                loadFragment(new EventsFragment());

            } else if (id == R.id.nav_create) {
                Toast.makeText(this,
                        "Create Event - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_chat) {
                Toast.makeText(this,
                        "Chats - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }

            return true;
        });
    }

    // ---------------------------------------------------
    // Sidebar Navigation
    // ---------------------------------------------------

    private void setupSidebar() {

        navView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.sidebar_profile) {

                loadFragment(new ProfileFragment());

            } else if (id == R.id.sidebar_events) {

                Toast.makeText(this,
                        "My Events - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_saved) {

                Toast.makeText(this,
                        "Saved Managers - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_notifications) {

                Toast.makeText(this,
                        "Notifications - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_support) {

                Intent intent =
                        new Intent(MainActivity.this, SupportActivity.class);
                startActivity(intent);

            } else if (id == R.id.sidebar_create_event) {

                Toast.makeText(this,
                        "Create Event - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_theme) {

                Toast.makeText(this,
                        "Theme Toggle - coming soon",
                        Toast.LENGTH_SHORT).show();

            } else if (id == R.id.sidebar_logout) {

                FirebaseAuth.getInstance().signOut();

                startActivity(
                        new Intent(MainActivity.this, AuthActivity.class)
                );

                finish();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    // ---------------------------------------------------
    // Sidebar Header
    // ---------------------------------------------------

    private void populateSidebarHeader() {

        View headerView = navView.getHeaderView(0);

        TextView tvName =
                headerView.findViewById(R.id.nav_user_name);

        TextView tvEmail =
                headerView.findViewById(R.id.nav_user_email);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String uid =
                    FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid();

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            tvName.setText(
                                    documentSnapshot.getString("name")
                            );

                            tvEmail.setText(
                                    documentSnapshot.getString("email")
                            );
                        }
                    });
        }
    }

    // ---------------------------------------------------
    // Open Sidebar
    // ---------------------------------------------------

    public void openSidebar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    // ---------------------------------------------------
    // Load Fragment
    // ---------------------------------------------------

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // ---------------------------------------------------
    // Back Press
    // ---------------------------------------------------

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}