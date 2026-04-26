package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.utsav.app.R;
import com.utsav.app.utils.Constants;

public class StatisticInsightsFragment extends Fragment {

    private TextView tvTotalRevenue, tvMonthlyRevenue, tvSuccessRate, tvTotalBookings, tvPendingCount;
    private FirebaseFirestore db;
    private String managerUid;
    private ListenerRegistration listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        managerUid = FirebaseAuth.getInstance().getUid();

        tvTotalRevenue = view.findViewById(R.id.tvProfileViews); // Re-using for views/revenue
        tvMonthlyRevenue = view.findViewById(R.id.tvProfileReach);
        tvSuccessRate = view.findViewById(R.id.tvEngagementRate);
        tvTotalBookings = view.findViewById(R.id.tvTotalRequestCount);
        tvPendingCount = view.findViewById(R.id.tvTotalRequestCount); // Fallback

        loadStats();

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof com.utsav.app.activities.ManagerDashboardActivity) {
                ((com.utsav.app.activities.ManagerDashboardActivity) getActivity()).openSidebar();
            }
        });
    }

    private void loadStats() {
        if (managerUid == null) return;

        listener = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    long totalRevenue = 0;
                    int activeCount = 0;
                    int pendingCount = 0;
                    int totalRequests = snapshots.size();

                    for (var doc : snapshots.getDocuments()) {
                        String status = doc.getString("status");
                        String budgetRange = doc.getString("budgetRange");

                        if (Constants.STATUS_ACCEPTED.equals(status) || Constants.STATUS_COMPLETED.equals(status)) {
                            activeCount++;
                            totalRevenue += parseBudget(budgetRange);
                        } else if (Constants.STATUS_PENDING.equals(status)) {
                            pendingCount++;
                        }
                    }

                    tvTotalRevenue.setText("INR " + totalRevenue);
                    tvTotalBookings.setText(String.valueOf(activeCount));
                    tvPendingCount.setText(String.valueOf(pendingCount));
                    
                    if (totalRequests > 0) {
                        int rate = (activeCount * 100) / totalRequests;
                        tvSuccessRate.setText(rate + "%");
                    }
                    
                    // Simple logic for monthly (for now same as total if we don't filter by date)
                    tvMonthlyRevenue.setText("INR " + totalRevenue);
                });
    }

    private long parseBudget(String budgetRange) {
        if (budgetRange == null || budgetRange.isEmpty()) return 0;
        try {
            String part = budgetRange.split("[–\\-]")[0];
            part = part.replaceAll("[^0-9]", "");
            return Long.parseLong(part);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
