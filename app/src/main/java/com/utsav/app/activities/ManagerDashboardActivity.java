package com.utsav.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.utsav.app.AuthActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.EventRequestAdapter;
import com.utsav.app.adapters.ScheduleAdapter;
import com.utsav.app.models.EventRequest;
import com.utsav.app.models.ScheduleItem;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {

    // ── Views ─────────────────────────────────────────────────────────────────
    private DrawerLayout drawerLayout;
    private TextView tvGreeting, tvManagerNameDisplay;
    private TextView tvRevenue;
    private TextView tvActiveBookings;
    private TextView tvPendingCount;
    private RecyclerView rvRequests;
    private RecyclerView rvSchedule;
    private View dashboardContent, fragmentContainer;
    private View emptyRequests;
    private View emptySchedule;

    // ── Adapters ──────────────────────────────────────────────────────────────
    private EventRequestAdapter requestAdapter;
    private ScheduleAdapter scheduleAdapter;
    private final List<EventRequest> requestList = new ArrayList<>();
    private final List<ScheduleItem> scheduleList = new ArrayList<>();

    // ── Firebase ──────────────────────────────────────────────────────────────
    private FirebaseFirestore db;
    private String managerUid;
    private final List<ListenerRegistration> listeners = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_manager_dashboard);

        db         = FirebaseFirestore.getInstance();
        managerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bindViews();
        setupSidebar();
        loadManagerInfo();
        loadStats();
        setupRequestsList();
        setupScheduleList();
        setupBottomNav();
    }

    // ── Bind ──────────────────────────────────────────────────────────────────
    private void bindViews() {
        drawerLayout    = findViewById(R.id.drawerLayout);
        tvGreeting      = findViewById(R.id.tvGreeting);
        tvManagerNameDisplay = findViewById(R.id.tvManagerNameDisplay);
        tvRevenue       = findViewById(R.id.tvRevenue);
        tvActiveBookings = findViewById(R.id.tvActiveBookings);
        tvPendingCount  = findViewById(R.id.tvPendingCount);
        rvRequests      = findViewById(R.id.rvRequests);
        rvSchedule      = findViewById(R.id.rvSchedule);
        emptyRequests   = findViewById(R.id.emptyRequests);
        emptySchedule   = findViewById(R.id.emptySchedule);
        dashboardContent = findViewById(R.id.dashboard_content);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Hamburger
        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));
    }

    // -- Bottom navbar
    private void setupBottomNav() {
        View dashboardContent = findViewById(R.id.dashboard_content);

        findViewById(R.id.mnav_dashboard).setOnClickListener(v -> {
            clearFragments();
            dashboardContent.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            updateNavUI(R.id.mnav_dashboard);
        });

        findViewById(R.id.mnav_requests).setOnClickListener(v -> {
            showFragment(new com.utsav.app.fragments.RequestFragment());
            updateNavUI(R.id.mnav_requests);
        });

        findViewById(R.id.mnav_insights).setOnClickListener(v -> {
            showFragment(new com.utsav.app.fragments.StatisticInsightsFragment());
            updateNavUI(R.id.mnav_insights);
        });

        findViewById(R.id.mnav_chat).setOnClickListener(v -> {
            showFragment(new com.utsav.app.fragments.ManagerChatsFragment());
            updateNavUI(R.id.mnav_chat);
        });

        findViewById(R.id.mnav_profile).setOnClickListener(v ->
                startActivity(new Intent(this, ManagerSelfProfileActivity.class)));
    }

    private void showFragment(androidx.fragment.app.Fragment fragment) {
        dashboardContent.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void clearFragments() {
        getSupportFragmentManager().getFragments().forEach(f ->
                getSupportFragmentManager().beginTransaction().remove(f).commit());
    }

    private void updateNavUI(int activeId) {
        int inactiveColor = 0xFFBBBBBB;
        int activeColor   = 0xFF9381FF;

        // Reset all
        ((ImageView)findViewById(R.id.mnav_dashboard_icon)).setColorFilter(inactiveColor);
        ((ImageView)findViewById(R.id.mnav_requests_icon)).setColorFilter(inactiveColor);
        ((ImageView)findViewById(R.id.mnav_chat_icon)).setColorFilter(inactiveColor);
        ((ImageView)findViewById(R.id.mnav_profile_icon)).setColorFilter(inactiveColor);
        ((ImageView)findViewById(R.id.mnav_insights_icon)).setColorFilter(inactiveColor);

        // Set active
        if (activeId == R.id.mnav_dashboard) {
            ((ImageView)findViewById(R.id.mnav_dashboard_icon)).setColorFilter(activeColor);
        } else if (activeId == R.id.mnav_requests) {
            ((ImageView)findViewById(R.id.mnav_requests_icon)).setColorFilter(activeColor);
        } else if (activeId == R.id.mnav_insights) {
            ((ImageView)findViewById(R.id.mnav_insights_icon)).setColorFilter(activeColor);
        } else if (activeId == R.id.mnav_chat) {
            ((ImageView)findViewById(R.id.mnav_chat_icon)).setColorFilter(activeColor);
        }
    }


    // ── Sidebar ───────────────────────────────────────────────────────────────
    private void setupSidebar() {
        NavigationView navView = findViewById(R.id.navView);

        // Populate sidebar header
        View header     = navView.getHeaderView(0);
        TextView tvName  = header.findViewById(R.id.nav_user_name);
        TextView tvEmail = header.findViewById(R.id.nav_user_email);

        db.collection(Constants.COLLECTION_USERS).document(managerUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String n = doc.getString("name");
                        String e = doc.getString("email");
                        tvName.setText(n  != null ? n : "Manager");
                        tvEmail.setText(e != null ? e : "");
                    }
                });

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sidebar_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else if (id == R.id.manager_sidebar_notifications) {
                startActivity(new Intent(this, com.utsav.app.activities.NotificationsActivity.class));
            }
            else if (id == R.id.manager_sidebar_requests) {
                showFragment(new com.utsav.app.fragments.RequestFragment());
                updateNavUI(R.id.mnav_requests);
            }
            else if (id == R.id.manager_sidebar_insights) {
                findViewById(R.id.dashboard_content).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new com.utsav.app.fragments.StatisticInsightsFragment())
                        .commit();
            }
            else if (id == R.id.manager_sidebar_events) {
                findViewById(R.id.dashboard_content).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new com.utsav.app.fragments.ManagerEventsFragment())
                        .commit();
            }
            else if (id == R.id.manager_sidebar_support) {
                startActivity(new Intent(this, SupportActivity.class));
            }
            // More sidebar items (Profile, etc) to be wired once those
            // activities are built by Farhan / Mehak.
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    public void openSidebar() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    // ── Manager Info ──────────────────────────────────────────────────────────
    private void loadManagerInfo() {
        // Set time-based greeting
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greet = "Good Night,";
        if (hour >= 5 && hour < 12)  greet = "Good Morning,";
        else if (hour >= 12 && hour < 17) greet = "Good Afternoon,";
        else if (hour >= 17 && hour < 21) greet = "Good Evening,";
        tvGreeting.setText(greet);

        db.collection(Constants.COLLECTION_USERS).document(managerUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        if (name != null && !name.isEmpty()) {
                            tvManagerNameDisplay.setText(name);
                        }
                    }
                });
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    /**
     * Reads all events where managerId == managerUid.
     * - activeBookings  → status == "accepted"
     * - revenue         → sum of budgetRange lower-bound for accepted events
     *                     (actual payment logic can replace this later)
     * - pendingCount    → status == "pending"
     */
    private void loadStats() {
        ListenerRegistration reg = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    int activeCount  = 0;
                    int pendingCount = 0;
                    long totalRevenue = 0;

                    for (var doc : snapshots.getDocuments()) {
                        String status = doc.getString("status");
                        if (Constants.STATUS_ACCEPTED.equals(status)
                                || Constants.STATUS_COMPLETED.equals(status)) {
                            activeCount++;
                            // Parse lower-bound from budgetRange for a rough revenue figure
                            totalRevenue += parseBudgetLower(doc.getString("budgetRange"));
                        }
                        if (Constants.STATUS_PENDING.equals(status)) {
                            pendingCount++;
                        }
                    }

                    tvRevenue.setText("INR " + totalRevenue);
                    tvActiveBookings.setText(String.valueOf(activeCount));
                    if (tvPendingCount != null) {
                        tvPendingCount.setText(pendingCount + " pending");
                    }
                });
        listeners.add(reg);
    }

    /** Extracts the lower bound integer from strings like "₹50,000 – ₹1,00,000". */
    private long parseBudgetLower(String budgetRange) {
        if (budgetRange == null || budgetRange.isEmpty()) return 0;
        try {
            // Strip everything except digits up to the first separator
            String part = budgetRange.split("[–\\-]")[0];
            part = part.replaceAll("[^0-9]", "");
            return Long.parseLong(part);
        } catch (Exception ignored) {
            return 0;
        }
    }

    // ── Pending Requests ──────────────────────────────────────────────────────
    private void setupRequestsList() {
        rvRequests.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        requestAdapter = new EventRequestAdapter(requestList, this::onAccept, this::onDecline, this::onItemClick);
        rvRequests.setAdapter(requestAdapter);

        ListenerRegistration reg = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .whereEqualTo("status", Constants.STATUS_PENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    requestList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        EventRequest req = doc.toObject(EventRequest.class);
                        if (req != null) {
                            req.setId(doc.getId());
                            requestList.add(req);
                        }
                    }
                    requestAdapter.notifyDataSetChanged();
                    emptyRequests.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvRequests.setVisibility(requestList.isEmpty() ? View.GONE : View.VISIBLE);
                });
        listeners.add(reg);
    }

    private void onAccept(EventRequest req) {
        updateEventStatus(req, Constants.STATUS_ACCEPTED);
    }

    private void onDecline(EventRequest req) {
        updateEventStatus(req, Constants.STATUS_REJECTED);
    }

    private void onItemClick(EventRequest req) {
        // Navigate to detail view
        Intent intent = new Intent(this, com.utsav.app.activities.RequestDetailActivity.class);
        intent.putExtra("eventId", req.getId());
        startActivity(intent);
    }

    private void updateEventStatus(EventRequest req, String newStatus) {
        db.collection(Constants.COLLECTION_EVENTS)
                .document(req.getId())
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            newStatus.equals(Constants.STATUS_ACCEPTED)
                                    ? "Request accepted ✓"
                                    : "Request declined",
                            Toast.LENGTH_SHORT).show();

                    String title = newStatus.equals(Constants.STATUS_ACCEPTED) ? "Request Accepted! 🎉" : "Request Declined";
                    String body = "The event '" + req.getTitle() + "' has been " + newStatus;

                    // 1. Notify host
                    com.utsav.app.activities.NotificationsActivity.pushNotification(
                            req.getHostUid(), "activity", "booking", title, body);

                    // 2. Notify manager (self)
                    String managerUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                    if (managerUid != null) {
                        com.utsav.app.activities.NotificationsActivity.pushNotification(
                                managerUid, "activity", "booking", "Action Confirmed", "You " + newStatus + " the request: " + req.getTitle());
                    }
                })
                .addOnFailureListener(ex ->
                        Toast.makeText(this, "Update failed. Try again.",
                                Toast.LENGTH_SHORT).show());
    }

    // ── Schedule ──────────────────────────────────────────────────────────────
    /**
     * Schedule = accepted events, sorted by date ascending.
     * Each item shows: title, date, location, and a Chat button.
     */
    private void setupScheduleList() {
        rvSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(scheduleList, this::onChatWithHost);
        rvSchedule.setAdapter(scheduleAdapter);

        ListenerRegistration reg = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .whereEqualTo("status", Constants.STATUS_ACCEPTED)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    List<ScheduleItem> allAccepted = new ArrayList<>();
                    for (var doc : snapshots.getDocuments()) {
                        ScheduleItem item = doc.toObject(ScheduleItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            allAccepted.add(item);
                        }
                    }

                    // Filter and Sort in Java
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault());
                    long now = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // Allow today

                    List<ScheduleItem> upcoming = new ArrayList<>();
                    for (ScheduleItem item : allAccepted) {
                        try {
                            java.util.Date d = sdf.parse(item.getDate());
                            if (d != null && d.getTime() >= now) {
                                upcoming.add(item);
                            }
                        } catch (Exception ignored) {}
                    }

                    // Sort by date ascending
                    java.util.Collections.sort(upcoming, (a, b) -> {
                        try {
                            java.util.Date da = sdf.parse(a.getDate());
                            java.util.Date db = sdf.parse(b.getDate());
                            return da.compareTo(db);
                        } catch (Exception ex) { return 0; }
                    });

                    scheduleList.clear();
                    // Take only 3
                    for (int i = 0; i < Math.min(3, upcoming.size()); i++) {
                        scheduleList.add(upcoming.get(i));
                    }

                    scheduleAdapter.notifyDataSetChanged();
                    emptySchedule.setVisibility(scheduleList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvSchedule.setVisibility(scheduleList.isEmpty() ? View.GONE : View.VISIBLE);
                });
        listeners.add(reg);
    }

    /** Opens (or creates) a chat between this manager and the event host. */
    private void onChatWithHost(ScheduleItem item) {
        if (item.getHostUid() == null || item.getHostUid().isEmpty()) {
            Toast.makeText(this, "Host info not available", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection(Constants.COLLECTION_CHATS)
                .whereEqualTo("hostUid",   item.getHostUid())
                .whereEqualTo("managerId", managerUid)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    String chatId;
                    if (!snap.isEmpty()) {
                        chatId = snap.getDocuments().get(0).getId();
                        navigateToChat(chatId, item.getTitle());
                    } else {
                        java.util.Map<String, Object> chat = new java.util.HashMap<>();
                        chat.put("hostUid",       item.getHostUid());
                        chat.put("managerId",     managerUid);
                        chat.put("managerName",   getCurrentManagerName());
                        chat.put("lastMessage",   "");
                        chat.put("lastTimestamp", System.currentTimeMillis());

                        db.collection(Constants.COLLECTION_CHATS).add(chat)
                                .addOnSuccessListener(ref ->
                                        navigateToChat(ref.getId(), item.getTitle()))
                                .addOnFailureListener(ex ->
                                        Toast.makeText(this,
                                                "Could not start chat. Try again.",
                                                Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void navigateToChat(String chatId, String title) {
        Intent intent = new Intent(this, com.utsav.app.ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID,      chatId);
        intent.putExtra(Constants.EXTRA_MANAGER_NAME, title);   // reuse key for display
        startActivity(intent);
    }

    /** Returns the name stored in Firestore or a fallback. */
    private String getCurrentManagerName() {
        return "Manager"; // Fallback, could be fetched from a variable
    }

    // ── Back / drawer ─────────────────────────────────────────────────────────
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (findViewById(R.id.dashboard_content).getVisibility() == View.GONE) {
            // Return to dashboard if a fragment is showing
            clearFragments();
            findViewById(R.id.dashboard_content).setVisibility(View.VISIBLE);
            updateNavUI(R.id.mnav_dashboard);
        } else {
            super.onBackPressed();
        }
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ListenerRegistration reg : listeners) reg.remove();
    }
}