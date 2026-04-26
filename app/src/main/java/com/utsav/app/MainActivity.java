package com.utsav.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.fragments.ChatsListFragment;
import com.utsav.app.fragments.CreateEventFragment;
import com.utsav.app.fragments.EventsFragment;
import com.utsav.app.fragments.HomeFragment;
import com.utsav.app.fragments.ProfileFragment;
import com.utsav.app.fragments.SavedManagersFragment;
import com.utsav.app.fragments.UserEventsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView      = findViewById(R.id.nav_view);

        setupBottomNav();
        setupSidebar();
        populateSidebarHeader();

        // Check for navigation extras
        handleIntentExtras(getIntent());
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntentExtras(intent);
    }

    private void handleIntentExtras(android.content.Intent intent) {
        if (intent == null) {
            loadFragment(new HomeFragment());
            return;
        }

        String target = intent.getStringExtra("targetFragment");
        String managerId = intent.getStringExtra("managerId");

        if ("create".equals(target)) {
            CreateEventFragment fragment = new CreateEventFragment();
            if (managerId != null) {
                Bundle args = new Bundle();
                args.putString("managerId", managerId);
                fragment.setArguments(args);
            }
            loadFragment(fragment);
            // Sync bottom nav UI
            ((BottomNavigationView)findViewById(R.id.bottomNav)).setSelectedItemId(R.id.nav_create);
        } else {
            loadFragment(new HomeFragment());
        }
    }

    // ── Bottom navigation ─────────────────────────────────────────────────────

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());

            } else if (id == R.id.nav_navigate) {
                // "Managers" tab → Events/browse screen
                loadFragment(new EventsFragment());

            } else if (id == R.id.nav_create) {
                loadFragment(new CreateEventFragment());


            } else if (id == R.id.nav_chat) {
                // ── FIX: Chat tab was never wired up ──
                loadFragment(new ChatsListFragment());

            } else if (id == R.id.nav_chat) {
                loadFragment(new SavedManagersFragment());
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }

            return true;
        });
    }

    // ── Sidebar (Navigation Drawer) ───────────────────────────────────────────

    private void setupSidebar() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.sidebar_profile) {
                loadFragment(new ProfileFragment());

            } else if (id == R.id.sidebar_events) {
                loadFragment(new UserEventsFragment());

            } else if (id == R.id.sidebar_create_event) {
                loadFragment(new CreateEventFragment());


            } else if (id == R.id.sidebar_saved) {
                loadFragment(new SavedManagersFragment());
                drawerLayout.closeDrawer(GravityCompat.END); // already called below, but safe

            } else if (id == R.id.sidebar_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new android.content.Intent(this, AuthActivity.class));
                finish();
            }
            // sidebar_theme, sidebar_notifications, sidebar_support,
            // sidebar_create_event are placeholders — add fragments when ready.

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    // ── Sidebar header: show real user name + email ───────────────────────────

    private void populateSidebarHeader() {
        View headerView  = navView.getHeaderView(0);
        TextView tvName  = headerView.findViewById(R.id.nav_user_name);
        TextView tvEmail = headerView.findViewById(R.id.nav_user_email);

        // Guard: getCurrentUser() can be null briefly during sign-out
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name  = doc.getString("name");
                        String email = doc.getString("email");
                        tvName.setText(name  != null && !name.isEmpty()  ? name  : "Welcome");
                        tvEmail.setText(email != null && !email.isEmpty() ? email : "");
                    }
                });
    }

    // ── Called from HomeFragment / ChatsListFragment hamburger icon ───────────

    public void openSidebar() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    // ── Fragment host ─────────────────────────────────────────────────────────

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