package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.utsav.app.MainActivity;
import com.utsav.app.R;
import com.utsav.app.models.EventRequest;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserEventsFragment extends Fragment {

    private RecyclerView rvMyEvents;
    private View layoutEmpty;
    private List<EventRequest> eventList = new ArrayList<>();
    private UserEventAdapter adapter;

    private FirebaseFirestore db;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        rvMyEvents = view.findViewById(R.id.rvMyEvents);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openSidebar();
            }
        });

        view.findViewById(R.id.btnCreateNow).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                // Navigate to Create tab
                ((com.google.android.material.bottomnavigation.BottomNavigationView)getActivity().findViewById(R.id.bottomNav))
                        .setSelectedItemId(R.id.nav_create);
            }
        });

        setupRecyclerView();
        loadMyEvents();
    }

    private void setupRecyclerView() {
        rvMyEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserEventAdapter(eventList);
        rvMyEvents.setAdapter(adapter);
    }

    private void loadMyEvents() {
        if (currentUid == null) return;

        db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("hostUid", currentUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    eventList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        EventRequest req = doc.toObject(EventRequest.class);
                        if (req != null) {
                            req.setId(doc.getId());
                            eventList.add(req);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    layoutEmpty.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    // ── Internal Adapter ──────────────────────────────────────────────────────

    private static class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.VH> {
        private final List<EventRequest> items;

        UserEventAdapter(List<EventRequest> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_event, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            EventRequest req = items.get(position);
            holder.tvTitle.setText(req.getTitle());
            holder.tvDate.setText(req.getDate());
            holder.tvType.setText(req.getType());
            holder.tvBudget.setText(req.getBudgetRange());
            
            String status = req.getStatus() != null ? req.getStatus() : Constants.STATUS_PENDING;
            holder.tvStatus.setText(status.toUpperCase());

            if (status.equals(Constants.STATUS_ACCEPTED)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_accepted);
            } else if (status.equals(Constants.STATUS_REJECTED)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            } else {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvStatus, tvType, tvBudget;
            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                tvDate = v.findViewById(R.id.tvDate);
                tvStatus = v.findViewById(R.id.tvStatus);
                tvType = v.findViewById(R.id.tvType);
                tvBudget = v.findViewById(R.id.tvBudget);
            }
        }
    }
}
