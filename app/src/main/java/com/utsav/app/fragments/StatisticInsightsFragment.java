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

    private TextView tvTotalRevenue, tvActiveBookings, tvPendingCount,
            tvSuccessRate, tvTotalRequests, tvRejectedCount;

    private FirebaseFirestore db;
    private String managerUid;
    private ListenerRegistration listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db         = FirebaseFirestore.getInstance();
        managerUid = FirebaseAuth.getInstance().getUid();

        // Bind — IDs now match the new XML exactly
        tvTotalRevenue   = view.findViewById(R.id.tvTotalRevenue);
        tvActiveBookings = view.findViewById(R.id.tvActiveBookings);
        tvPendingCount   = view.findViewById(R.id.tvPendingCount);
        tvSuccessRate    = view.findViewById(R.id.tvSuccessRate);
        tvTotalRequests  = view.findViewById(R.id.tvTotalRequests);
        tvRejectedCount  = view.findViewById(R.id.tvRejectedCount);

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof
                    com.utsav.app.activities.ManagerDashboardActivity) {
                ((com.utsav.app.activities.ManagerDashboardActivity)
                        getActivity()).openSidebar();
            }
        });

        loadStats();
    }

    private void loadStats() {
        if (managerUid == null) return;

        listener = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null || !isAdded()) return;

                    long totalRevenue  = 0;
                    int  activeCount   = 0;
                    int  pendingCount  = 0;
                    int  rejectedCount = 0;
                    int  totalRequests = snapshots.size();

                    for (var doc : snapshots.getDocuments()) {
                        String status      = doc.getString("status");
                        String budgetRange = doc.getString("budgetRange");

                        if (Constants.STATUS_ACCEPTED.equals(status)
                                || Constants.STATUS_COMPLETED.equals(status)) {
                            activeCount++;
                            totalRevenue += parseBudget(budgetRange);
                        } else if (Constants.STATUS_PENDING.equals(status)) {
                            pendingCount++;
                        } else if (Constants.STATUS_REJECTED.equals(status)) {
                            rejectedCount++;
                        }
                    }

                    tvTotalRevenue.setText("INR " + totalRevenue);
                    tvActiveBookings.setText(String.valueOf(activeCount));
                    tvPendingCount.setText(String.valueOf(pendingCount));
                    tvTotalRequests.setText(String.valueOf(totalRequests));
                    tvRejectedCount.setText(String.valueOf(rejectedCount));

                    if (totalRequests > 0) {
                        int rate = (activeCount * 100) / totalRequests;
                        tvSuccessRate.setText(rate + "%");
                    } else {
                        tvSuccessRate.setText("—");
                    }
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
    public void onDestroyView() {
        super.onDestroyView();
        if (listener != null) listener.remove();
    }
}