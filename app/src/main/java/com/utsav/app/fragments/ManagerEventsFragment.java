package com.utsav.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.utsav.app.ChatActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.ScheduleAdapter;
import com.utsav.app.models.ScheduleItem;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ManagerEventsFragment extends Fragment {

    private RecyclerView rvEvents;
    private ScheduleAdapter adapter;
    private final List<ScheduleItem> eventList = new ArrayList<>();
    private FirebaseFirestore db;
    private String managerUid;
    private ListenerRegistration listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        managerUid = FirebaseAuth.getInstance().getUid();

        rvEvents = view.findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ScheduleAdapter(eventList, this::onChatWithHost);
        rvEvents.setAdapter(adapter);

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof com.utsav.app.activities.ManagerDashboardActivity) {
                ((com.utsav.app.activities.ManagerDashboardActivity) getActivity()).openSidebar();
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        if (managerUid == null) return;

        listener = db.collection(Constants.COLLECTION_EVENTS)
                .whereEqualTo("managerId", managerUid)
                .whereEqualTo("status", Constants.STATUS_ACCEPTED)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    eventList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        ScheduleItem item = doc.toObject(ScheduleItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            eventList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    
                    View emptyState = getView() != null ? getView().findViewById(R.id.layoutEmpty) : null;
                    if (emptyState != null) {
                        emptyState.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                        rvEvents.setVisibility(eventList.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private void onChatWithHost(ScheduleItem item) {
        if (item.getHostUid() == null || item.getHostUid().isEmpty()) {
            Toast.makeText(getContext(), "Host info not available", Toast.LENGTH_SHORT).show();
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
                        // Create chat if doesn't exist
                        db.collection("users").document(managerUid).get().addOnSuccessListener(managerDoc -> {
                            String managerName = managerDoc.getString("name");
                            if (managerName == null) managerName = "Manager";
                            
                            String finalManagerName = managerName;
                            db.collection("users").document(item.getHostUid()).get().addOnSuccessListener(hostDoc -> {
                                String hName = hostDoc.getString("name");
                                final String hostName = (hName != null) ? hName : "Host";

                                java.util.Map<String, Object> chat = new java.util.HashMap<>();
                                chat.put("hostUid",       item.getHostUid());
                                chat.put("managerId",     managerUid);
                                chat.put("managerName",   finalManagerName);
                                chat.put("hostName",      hostName);
                                chat.put("lastMessage",   "");
                                chat.put("lastTimestamp", System.currentTimeMillis());

                                db.collection(Constants.COLLECTION_CHATS).add(chat)
                                        .addOnSuccessListener(ref -> navigateToChat(ref.getId(), hostName));
                            });
                        });
                    }
                });
    }

    private void navigateToChat(String chatId, String title) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID, chatId);
        intent.putExtra(Constants.EXTRA_MANAGER_NAME, title);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
