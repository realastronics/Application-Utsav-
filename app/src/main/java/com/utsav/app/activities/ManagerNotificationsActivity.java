package com.utsav.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import com.utsav.app.R;
import com.utsav.app.adapters.NotificationAdapter;
import com.utsav.app.models.NotificationItem;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows all notifications for the logged-in manager.
 *
 * Tabs: All | Activity | System
 *
 * Firestore path:  notifications/{notifId}
 *   Fields: recipientUid, type ("activity"|"system"), title, body,
 *           read (boolean), createdAt (long), iconType ("booking"|"message"|"subscription")
 *
 * We also expose a static helper  pushNotification()  so other parts of the
 * app (e.g. when a host accepts a quote) can write a notification document
 * without importing this whole Activity.
 */
public class ManagerNotificationsActivity extends AppCompatActivity {

    // ── Tab IDs ───────────────────────────────────────────────────────────────
    private static final int TAB_ALL      = 0;
    private static final int TAB_ACTIVITY = 1;
    private static final int TAB_SYSTEM   = 2;

    // ── Views ─────────────────────────────────────────────────────────────────
    private TextView tvTabAll, tvTabActivity, tvTabSystem;
    private RecyclerView rvNotifications;
    private View emptyState;

    // ── Data ──────────────────────────────────────────────────────────────────
    private NotificationAdapter adapter;
    private final List<NotificationItem> allNotifs      = new ArrayList<>();
    private final List<NotificationItem> displayList    = new ArrayList<>();
    private int currentTab = TAB_ALL;

    // ── Firebase ──────────────────────────────────────────────────────────────
    private FirebaseFirestore db;
    private String managerUid;
    private ListenerRegistration listener;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_notifications);

        db         = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        managerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bindViews();
        setupList();
        setupTabs();
        listenForNotifications();
    }

    // ── Bind ──────────────────────────────────────────────────────────────────
    private void bindViews() {
        // Back arrow
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Mark-all-read button (top right checkmark icon)
        findViewById(R.id.btnMarkAllRead).setOnClickListener(v -> markAllRead());

        tvTabAll      = findViewById(R.id.tvTabAll);
        tvTabActivity = findViewById(R.id.tvTabActivity);
        tvTabSystem   = findViewById(R.id.tvTabSystem);
        rvNotifications = findViewById(R.id.rvNotifications);
        emptyState      = findViewById(R.id.emptyState);
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────
    private void setupTabs() {
        tvTabAll.setOnClickListener(v      -> selectTab(TAB_ALL));
        tvTabActivity.setOnClickListener(v -> selectTab(TAB_ACTIVITY));
        tvTabSystem.setOnClickListener(v   -> selectTab(TAB_SYSTEM));
        selectTab(TAB_ALL);
    }

    private void selectTab(int tab) {
        currentTab = tab;

        // Reset all
        int inactive = 0xFF888888;
        tvTabAll.setTextColor(inactive);
        tvTabActivity.setTextColor(inactive);
        tvTabSystem.setTextColor(inactive);
        tvTabAll.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvTabActivity.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvTabSystem.setTypeface(null, android.graphics.Typeface.NORMAL);

        // Highlight selected
        TextView selected = tab == TAB_ALL ? tvTabAll
                : tab == TAB_ACTIVITY ? tvTabActivity
                : tvTabSystem;
        selected.setTextColor(0xFF9381FF);
        selected.setTypeface(null, android.graphics.Typeface.BOLD);

        applyFilter();
    }

    private void applyFilter() {
        displayList.clear();
        for (NotificationItem n : allNotifs) {
            if (currentTab == TAB_ALL)                              displayList.add(n);
            else if (currentTab == TAB_ACTIVITY && "activity".equals(n.getType())) displayList.add(n);
            else if (currentTab == TAB_SYSTEM   && "system".equals(n.getType()))   displayList.add(n);
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
        rvNotifications.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────
    private void setupList() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(displayList, this::onNotifClick);
        rvNotifications.setAdapter(adapter);
    }

    private void onNotifClick(NotificationItem item) {
        if (!item.isRead()) {
            db.collection(Constants.COLLECTION_NOTIFICATIONS)
                    .document(item.getId())
                    .update("read", true);
        }
        // Future: deep-link based on item.getDeepLink()
    }

    // ── Firestore listener ────────────────────────────────────────────────────
    private void listenForNotifications() {
        listener = db.collection(Constants.COLLECTION_NOTIFICATIONS)
                .whereEqualTo("recipientUid", managerUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    allNotifs.clear();
                    for (var doc : snapshots.getDocuments()) {
                        NotificationItem item = doc.toObject(NotificationItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            allNotifs.add(item);
                        }
                    }
                    applyFilter();
                });
    }

    // ── Mark all read ─────────────────────────────────────────────────────────
    private void markAllRead() {
        for (NotificationItem n : allNotifs) {
            if (!n.isRead()) {
                db.collection(Constants.COLLECTION_NOTIFICATIONS)
                        .document(n.getId())
                        .update("read", true);
            }
        }
    }

    // ── Static helper ─────────────────────────────────────────────────────────
    /**
     * Call from anywhere to push a notification document.
     *
     * @param recipientUid  manager or host UID
     * @param type          "activity" or "system"
     * @param iconType      "booking" | "message" | "subscription"
     * @param title         short headline
     * @param body          detail line
     */
    public static void pushNotification(String recipientUid,
                                        String type,
                                        String iconType,
                                        String title,
                                        String body) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("recipientUid", recipientUid);
        data.put("type",         type);
        data.put("iconType",     iconType);
        data.put("title",        title);
        data.put("body",         body);
        data.put("read",         false);
        data.put("createdAt",    System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_NOTIFICATIONS)
                .add(data);
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}