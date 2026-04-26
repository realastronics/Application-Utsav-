package com.utsav.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.utsav.app.ChatActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.ChatListAdapter;
import com.utsav.app.models.ChatPreview;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChatsListFragment extends Fragment {

    private RecyclerView rvChats;
    private ChatListAdapter adapter;
    private List<ChatPreview> chatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof com.utsav.app.MainActivity) {
                ((com.utsav.app.MainActivity) getActivity()).openSidebar();
            }
        });

        rvChats = view.findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(chatList, preview -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(Constants.EXTRA_CHAT_ID, preview.getChatId());
            intent.putExtra(Constants.EXTRA_MANAGER_NAME, preview.getManagerName());
            startActivity(intent);
        });
        rvChats.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_CHATS)
                .whereEqualTo("hostUid", uid)
                .orderBy("lastTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    chatList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        ChatPreview preview = new ChatPreview();
                        preview.setChatId(doc.getId());
                        preview.setManagerName(doc.getString("managerName"));
                        preview.setLastMessage(doc.getString("lastMessage"));
                        preview.setTimestamp(doc.getLong("lastTimestamp") != null
                                ? doc.getLong("lastTimestamp") : 0L);
                        chatList.add(preview);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}