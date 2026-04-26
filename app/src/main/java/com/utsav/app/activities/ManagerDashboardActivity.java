package com.utsav.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private TextView tvGreeting;
    private TextView tvRevenue;
    private TextView tvActiveBookings;
    private TextView tvPendingCount;
    private RecyclerView rvRequests;
    private RecyclerView rvSchedule;
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
        loadManagerName();
        loadStats();
        setupRequestsList();
        setupScheduleList();
    }

    // ── Bind ──────────────────────────────────────────────────────────────────
    private void bindViews() {
        drawerLayout    = findViewById(R.id.drawerLayout);
        tvGreeting      = findViewById(R.id.tvGreeting);
        tvRevenue       = findViewById(R.id.tvRevenue);
        tvActiveBookings = findViewById(R.id.tvActiveBookings);
        tvPendingCount  = findViewById(R.id.tvPendingCount);
        rvRequests      = findViewById(R.id.rvRequests);
        rvSchedule      = findViewById(R.id.rvSchedule);
        emptyRequests   = findViewById(R.id.emptyRequests);
        emptySchedule   = findViewById(R.id.emptySchedule);

        // Hamburger
        findViewById(R.id.btnMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, ManagerNotificationsActivity.class)));
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
            else if (id == R.id.sidebar_notifications) {
                startActivity(new Intent(this, ManagerNotificationsActivity.class));
            }
            // More sidebar items (Profile, etc) to be wired once those
            // activities are built by Farhan / Mehak.
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    // ── Greeting ──────────────────────────────────────────────────────────────
    private void loadManagerName() {
        db.collection(Constants.COLLECTION_USERS).document(managerUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        if (name != null && !name.isEmpty()) {
                            tvGreeting.setText("Hi " + name.split(" ")[0] + "!");
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
                    tvPendingCount.setText(pendingCount + " pending");
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
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new EventRequestAdapter(requestList, this::onAccept, this::onDecline);
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
        updateEventStatus(req.getId(), Constants.STATUS_ACCEPTED);
    }

    private void onDecline(EventRequest req) {
        updateEventStatus(req.getId(), Constants.STATUS_REJECTED);
    }

    private void updateEventStatus(String eventId, String newStatus) {
        db.collection(Constants.COLLECTION_EVENTS)
                .document(eventId)
                .update("status", newStatus)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                newStatus.equals(Constants.STATUS_ACCEPTED)
                                        ? "Request accepted ✓"
                                        : "Request declined",
                                Toast.LENGTH_SHORT).show())
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

                    scheduleList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        ScheduleItem item = doc.toObject(ScheduleItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            scheduleList.add(item);
                        }
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

    /** Returns the name stored in tvGreeting, stripped of "Hi " prefix. */
    private String getCurrentManagerName() {
        String raw = tvGreeting.getText().toString(); // "Hi Farhan!"
        return raw.startsWith("Hi ") ? raw.substring(3, raw.length() - 1) : raw;
    }

    // ── Back / drawer ─────────────────────────────────────────────────────────
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
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