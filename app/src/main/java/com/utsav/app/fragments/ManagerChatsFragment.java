package com.utsav.app.fragments;

import android.content.Intent;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import com.utsav.app.ChatActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.ChatListAdapter;
import com.utsav.app.models.ChatPreview;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ManagerChatsFragment extends Fragment {

    private RecyclerView          rvChats;
    private View                  layoutEmpty;
    private ChatListAdapter       adapter;
    private final List<ChatPreview> chatList = new ArrayList<>();
    private ListenerRegistration  listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChats      = view.findViewById(R.id.rvChats);
        layoutEmpty  = view.findViewById(R.id.layoutEmpty);

        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(chatList, preview -> {
            // When manager taps a chat, open ChatActivity.
            // otherPersonName = hostName stored in the chat doc.
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(Constants.EXTRA_CHAT_ID,      preview.getChatId());
            // Reuse EXTRA_MANAGER_NAME key but pass the HOST's name
            // so the toolbar shows who the manager is chatting with.
            intent.putExtra(Constants.EXTRA_MANAGER_NAME, preview.getHostName());
            startActivity(intent);
        });
        rvChats.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ── KEY: query by managerId == current user's real Firebase UID ──────
        listener = FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_CHATS)
                .whereEqualTo("managerId", uid)
                .orderBy("lastTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null || !isAdded()) return;

                    chatList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        ChatPreview preview = new ChatPreview();
                        preview.setChatId(doc.getId());
                        // For the manager, the "other person" is the host
                        preview.setManagerName(doc.getString("hostName"));
                        preview.setHostName(doc.getString("hostName"));
                        preview.setLastMessage(doc.getString("lastMessage"));
                        preview.setTimestamp(doc.getLong("lastTimestamp") != null
                                ? doc.getLong("lastTimestamp") : 0L);
                        chatList.add(preview);
                    }

                    adapter.notifyDataSetChanged();

                    if (layoutEmpty != null) {
                        layoutEmpty.setVisibility(
                                chatList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listener != null) listener.remove();
    }
}