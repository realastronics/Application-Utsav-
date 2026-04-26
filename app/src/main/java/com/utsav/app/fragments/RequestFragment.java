package com.utsav.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.utsav.app.R;
import com.utsav.app.activities.ManagerDashboardActivity;
import com.utsav.app.adapters.EventRequestAdapter;
import com.utsav.app.models.EventRequest;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestFragment extends Fragment {

    private RecyclerView rvRequests;
    private EventRequestAdapter adapter;
    private final List<EventRequest> fullList = new ArrayList<>();
    private final List<EventRequest> filteredList = new ArrayList<>();
    
    private FirebaseFirestore db;
    private String managerUid;
    private ListenerRegistration listener;
    
    private String currentFilter = "All";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        managerUid = FirebaseAuth.getInstance().getUid();

        rvRequests = view.findViewById(R.id.rvRequests);
        EditText etSearch = view.findViewById(R.id.etSearch);

        setupRecyclerView();
        setupSearch(etSearch);
        setupChips(view);
        loadRequests();

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof ManagerDashboardActivity) {
                ((ManagerDashboardActivity) getActivity()).openSidebar();
            }
        });
    }

    private void setupRecyclerView() {
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventRequestAdapter(filteredList, this::onAccept, this::onDecline, this::onItemClick);
        rvRequests.setAdapter(adapter);
    }

    private void onItemClick(EventRequest req) {
        // Navigate to detail view
        Intent intent = new Intent(getContext(), com.utsav.app.activities.RequestDetailActivity.class);
        intent.putExtra("eventId", req.getId());
        startActivity(intent);
    }

    private void setupSearch(EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase(Locale.ROOT);
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupChips(View view) {
        view.findViewById(R.id.chipAll).setOnClickListener(v -> { currentFilter = "All"; applyFilters(); });
        view.findViewById(R.id.chipPending).setOnClickListener(v -> { currentFilter = Constants.STATUS_PENDING; applyFilters(); });
        view.findViewById(R.id.chipAccepted).setOnClickListener(v -> { currentFilter = Constants.STATUS_ACCEPTED; applyFilters(); });
        view.findViewById(R.id.chipCompleted).setOnClickListener(v -> { currentFilter = Constants.STATUS_COMPLETED; applyFilters(); });
    }

    private void loadRequests() {
        if (managerUid == null) return;

        listener = db.collection(Constants.COLLECTION_EVENTS)
                .where(com.google.firebase.firestore.Filter.or(
                        com.google.firebase.firestore.Filter.equalTo("managerId", managerUid),
                        com.google.firebase.firestore.Filter.equalTo("managerId", "public")
                ))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    fullList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        EventRequest req = doc.toObject(EventRequest.class);
                        if (req != null) {
                            req.setId(doc.getId());
                            fullList.add(req);
                        }
                    }
                    applyFilters();
                });
    }

    private void applyFilters() {
        filteredList.clear();
        for (EventRequest req : fullList) {
            boolean matchesSearch = req.getTitle().toLowerCase(Locale.ROOT).contains(searchQuery) ||
                                   (req.getType() != null && req.getType().toLowerCase(Locale.ROOT).contains(searchQuery));
            
            boolean matchesFilter = currentFilter.equals("All") || currentFilter.equals(req.getStatus());

            if (matchesSearch && matchesFilter) {
                filteredList.add(req);
            }
        }
        adapter.notifyDataSetChanged();
        
        View emptyState = getView() != null ? getView().findViewById(R.id.layoutEmpty) : null;
        if (emptyState != null) {
            emptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
            rvRequests.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void onAccept(EventRequest req) {
        updateStatus(req, Constants.STATUS_ACCEPTED);
    }

    private void onDecline(EventRequest req) {
        updateStatus(req, Constants.STATUS_REJECTED);
    }

    private void updateStatus(EventRequest req, String status) {
        db.collection(Constants.COLLECTION_EVENTS).document(req.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Status updated", Toast.LENGTH_SHORT).show();
                    
                    String title = status.equals(Constants.STATUS_ACCEPTED) ? "Request Accepted! 🎉" : "Request Declined";
                    String body = "The event '" + req.getTitle() + "' has been " + status;
                    
                    // 1. Notify Host
                    com.utsav.app.activities.NotificationsActivity.pushNotification(
                        req.getHostUid(), "activity", "booking", title, body);
                        
                    // 2. Notify Manager (Self)
                    if (managerUid != null) {
                        com.utsav.app.activities.NotificationsActivity.pushNotification(
                            managerUid, "activity", "booking", "Action Confirmed", "You " + status + " the request: " + req.getTitle());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
