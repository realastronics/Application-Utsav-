package com.utsav.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.ChatActivity;
import com.utsav.app.R;
import com.utsav.app.models.Manager;
import com.utsav.app.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerAdapter extends
        RecyclerView.Adapter<ManagerAdapter.ViewHolder> {

    private final List<Manager> managers;

    public ManagerAdapter(List<Manager> managers) {
        this.managers = managers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manager_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manager m = managers.get(position);

        holder.tvName.setText(m.getName() != null ? m.getName() : "—");
        holder.tvType.setText(m.getEventType() + " Manager");
        holder.tvDesc.setText(m.getDescription());
        holder.tvRating.setText(String.valueOf(m.getRating()));
        holder.tvPhone.setText(m.getPhone() != null ? m.getPhone() : "—");

        // Opens or creates a Firestore chat document, then launches ChatActivity
        holder.btnChat.setOnClickListener(v -> openOrCreateChat(v.getContext(), m));

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Opening " + m.getName() + "'s profile",
                        Toast.LENGTH_SHORT).show());
    }

    /**
     * Checks Firestore for an existing chat between the current host and this manager.
     * Creates one if none exists, then navigates to ChatActivity.
     *
     * Document schema (collection: "chats"):
     *   hostUid       : String
     *   managerId     : String
     *   managerName   : String
     *   lastMessage   : String
     *   lastTimestamp : long
     */
    private void openOrCreateChat(Context context, Manager manager) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String hostUid     = auth.getCurrentUser().getUid();
        String managerId   = manager.getId();
        String managerName = manager.getName();

        // Guard: DataProvider/DummyData managers have no Firestore ID set.
        // Until managers are seeded from Firestore, show a clear message.
        if (managerId == null || managerId.isEmpty()) {
            Toast.makeText(context,
                    "This manager is not yet linked to a Firestore account.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.COLLECTION_CHATS)
                .whereEqualTo("hostUid",   hostUid)
                .whereEqualTo("managerId", managerId)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        // Chat already exists — open it directly
                        String chatId = snapshot.getDocuments().get(0).getId();
                        navigateToChat(context, chatId, managerName);
                    } else {
                        // No chat yet — create document, then open
                        Map<String, Object> chatData = new HashMap<>();
                        chatData.put("hostUid",       hostUid);
                        chatData.put("managerId",     managerId);
                        chatData.put("managerName",   managerName);
                        chatData.put("lastMessage",   "");
                        chatData.put("lastTimestamp", System.currentTimeMillis());

                        db.collection(Constants.COLLECTION_CHATS)
                                .add(chatData)
                                .addOnSuccessListener(docRef ->
                                        navigateToChat(context, docRef.getId(), managerName))
                                .addOnFailureListener(e ->
                                        Toast.makeText(context,
                                                "Could not start chat. Try again.",
                                                Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context,
                                "Network error. Check your connection.",
                                Toast.LENGTH_SHORT).show());
    }

    private void navigateToChat(Context context, String chatId, String managerName) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID,      chatId);
        intent.putExtra(Constants.EXTRA_MANAGER_NAME, managerName);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() { return managers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc, tvRating, tvPhone;
        View btnChat;

        ViewHolder(View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvManagerName);
            tvType   = v.findViewById(R.id.tvManagerType);
            tvDesc   = v.findViewById(R.id.tvManagerDesc);
            tvRating = v.findViewById(R.id.tvRating);
            tvPhone  = v.findViewById(R.id.tvPhone);
            btnChat  = v.findViewById(R.id.btnChat);
        }
    }
}