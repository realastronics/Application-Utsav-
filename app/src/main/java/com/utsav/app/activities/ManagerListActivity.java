package com.utsav.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.AuthActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.ManagerAdapter;
import com.utsav.app.fragments.ProfileFragment;
import com.utsav.app.models.Manager;
import com.utsav.app.utils.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class ManagerListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);

        drawerLayout = findViewById(R.id.drawerLayout);

        // ── Hamburger → open sidebar ──────────────────────────────────────────
        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));

        // ── Sidebar item clicks ───────────────────────────────────────────────
        NavigationView navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.sidebar_profile) {
                // Go back to MainActivity and show profile
                finish();

            } else if (id == R.id.sidebar_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            // Other sidebar items: close drawer and let MainActivity handle them
            // when the user navigates back.

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // ── Populate sidebar header with real user data ───────────────────────
        populateSidebarHeader(navView);

        // ── Manager list ──────────────────────────────────────────────────────
        RecyclerView rv = findViewById(R.id.rvManagers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        loadManagers(rv);
    }

    // In ManagerListActivity.java, replace the rv.setAdapter line and add this method:

    private void loadManagers(RecyclerView rv) {
        List<Manager> managerList = new ArrayList<>();
        ManagerAdapter managerAdapter = new ManagerAdapter(managerList);
        rv.setAdapter(managerAdapter);

        FirebaseFirestore.getInstance()
                .collection("managers")
                .whereEqualTo("isAvailable", true)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    managerList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        Manager m = doc.toObject(Manager.class);
                        if (m != null) {
                            m.setId(doc.getId());  // real Firebase UID
                            managerList.add(m);
                        }
                    }
                    managerAdapter.notifyDataSetChanged();
                });
    }

    private void populateSidebarHeader(NavigationView navView) {
        android.view.View header = navView.getHeaderView(0);
        TextView tvName  = header.findViewById(R.id.nav_user_name);
        TextView tvEmail = header.findViewById(R.id.nav_user_email);

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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}