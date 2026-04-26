package com.utsav.app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.utsav.app.adapters.MessageAdapter;
import com.utsav.app.models.ChatMessage;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView     rvMessages;
    private EditText         etMessage;
    private ImageButton      btnSend;
    private MessageAdapter   adapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private FirebaseFirestore db;
    private String currentUid;
    private String chatId;
    private String managerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId      = getIntent().getStringExtra(Constants.EXTRA_CHAT_ID);
        managerName = getIntent().getStringExtra(Constants.EXTRA_MANAGER_NAME);
        currentUid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db          = FirebaseFirestore.getInstance();

        // Toolbar name
        TextView tvName = findViewById(R.id.tv_chat_name);
        tvName.setText(managerName);

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // RecyclerView
        rvMessages = findViewById(R.id.rv_messages);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);   // newest message at bottom
        rvMessages.setLayoutManager(lm);
        adapter = new MessageAdapter(messages, currentUid);
        rvMessages.setAdapter(adapter);

        etMessage = findViewById(R.id.et_message);
        btnSend   = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v -> sendMessage());

        listenForMessages();
    }

    private void listenForMessages() {
        db.collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .collection("messages")
                .orderBy(Constants.FIELD_TIMESTAMP, Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    messages.clear();
                    for (var doc : snapshots.getDocuments()) {
                        ChatMessage msg = doc.toObject(ChatMessage.class);
                        if (msg != null) {
                            // sentByUser = true if this device's user sent it
                            msg.setSentByUser(currentUid.equals(msg.getSenderId()));
                            messages.add(msg);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    // Scroll to bottom
                    if (!messages.isEmpty()) {
                        rvMessages.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        etMessage.setText("");

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("senderId",   currentUid);
        msgData.put("text",       text);
        msgData.put("timestamp",  System.currentTimeMillis());
        msgData.put("sentByUser", true);

        // Write message to sub-collection
        db.collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .collection("messages")
                .add(msgData)
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this,
                                "Failed to send", Toast.LENGTH_SHORT).show());

        // Update last message on parent chat document
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage",   text);
        chatUpdate.put("lastTimestamp", System.currentTimeMillis());

        db.collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .update(chatUpdate);
    }
}