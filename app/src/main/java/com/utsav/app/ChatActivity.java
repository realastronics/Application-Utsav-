package com.utsav.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
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
    private ImageButton      btnSend, btnBack;
    private TextView         tvChatName, tvChatInitial;

    private MessageAdapter          adapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    private FirebaseFirestore db;
    private String            currentUid;   // whoever is logged in right now
    private String            chatId;
    private String            otherPersonName;

    private ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db         = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ── Extras ──────────────────────────────────────────────────────────
        chatId          = getIntent().getStringExtra(Constants.EXTRA_CHAT_ID);
        otherPersonName = getIntent().getStringExtra(Constants.EXTRA_MANAGER_NAME);

        if (chatId == null || chatId.isEmpty()) {
            Toast.makeText(this, "Invalid chat.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ── Views ────────────────────────────────────────────────────────────
        btnBack       = findViewById(R.id.btn_back);
        tvChatName    = findViewById(R.id.tv_chat_name);
        tvChatInitial = findViewById(R.id.tv_chat_initial);
        rvMessages    = findViewById(R.id.rv_messages);
        etMessage     = findViewById(R.id.et_message);
        btnSend       = findViewById(R.id.btn_send);

        tvChatName.setText(otherPersonName != null ? otherPersonName : "Chat");
        if (otherPersonName != null && !otherPersonName.isEmpty()) {
            tvChatInitial.setText(String.valueOf(otherPersonName.charAt(0)).toUpperCase());
        }

        btnBack.setOnClickListener(v -> finish());

        // ── RecyclerView ─────────────────────────────────────────────────────
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);          // newest messages at bottom
        rvMessages.setLayoutManager(lm);

        // Pass currentUid — adapter uses it to decide left vs right bubble
        adapter = new MessageAdapter(messages, currentUid);
        rvMessages.setAdapter(adapter);

        // ── Send ─────────────────────────────────────────────────────────────
        btnSend.setOnClickListener(v -> sendMessage());

        // ── Listen ───────────────────────────────────────────────────────────
        listenForMessages();
    }

    // ── Real-time listener ────────────────────────────────────────────────────
    private void listenForMessages() {
        messageListener = db
                .collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    messages.clear();
                    for (var doc : snapshots.getDocuments()) {
                        String senderId = doc.getString("senderId");
                        String text     = doc.getString("text");
                        Long   ts       = doc.getLong("timestamp");

                        if (text == null || text.isEmpty()) continue;

                        ChatMessage msg = new ChatMessage();
                        msg.setMessageId(doc.getId());
                        msg.setSenderId(senderId);
                        msg.setText(text);
                        msg.setTimestamp(ts != null ? ts : 0L);

                        // ── KEY FIX: sentByUser is determined at READ time
                        // by comparing senderId to whoever is currently logged in.
                        // This means the same message doc shows on the RIGHT
                        // for the sender and on the LEFT for the receiver.
                        msg.setSentByUser(currentUid.equals(senderId));

                        messages.add(msg);
                    }

                    adapter.notifyDataSetChanged();
                    // Scroll to latest message
                    if (!messages.isEmpty()) {
                        rvMessages.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    // ── Send message ─────────────────────────────────────────────────────────
    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        etMessage.setText("");

        long now = System.currentTimeMillis();

        // Write to messages subcollection
        Map<String, Object> msgData = new HashMap<>();
        msgData.put("senderId",  currentUid);
        msgData.put("text",      text);
        msgData.put("timestamp", now);

        db.collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .collection("messages")
                .add(msgData)
                .addOnFailureListener(ex ->
                        Toast.makeText(this,
                                "Failed to send. Check connection.",
                                Toast.LENGTH_SHORT).show());

        // Update parent chat doc for preview (lastMessage + lastTimestamp)
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage",   text);
        chatUpdate.put("lastTimestamp", now);

        db.collection(Constants.COLLECTION_CHATS)
                .document(chatId)
                .update(chatUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) messageListener.remove();
    }
}