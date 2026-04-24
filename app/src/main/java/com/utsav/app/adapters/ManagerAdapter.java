package com.utsav.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        holder.tvName.setText(m.getName()  != null ? m.getName()  : "—");
        holder.tvType.setText(m.getEventType() + " Manager");
        holder.tvDesc.setText(m.getDescription());
        holder.tvRating.setText(String.valueOf(m.getRating()));
        holder.tvPhone.setText(m.getPhone() != null ? m.getPhone() : "—");

        // ── Bookmark icon — save / unsave ─────────────────────────────────────
        holder.imgSaved.setOnClickListener(v -> toggleSave(v.getContext(), m, holder));

        // ── Chat button — open or create Firestore chat ───────────────────────
        holder.btnChat.setOnClickListener(v -> openOrCreateChat(v.getContext(), m));

        // ── Card tap — profile (placeholder) ─────────────────────────────────
        holder.itemView.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Opening " + m.getName() + "'s profile",
                        Toast.LENGTH_SHORT).show());
    }

    // ── Save / unsave ─────────────────────────────────────────────────────────

    /**
     * Writes or deletes a document at:
     *   users/{hostUid}/savedManagers/{managerId}
     *
     * The document is a snapshot of the Manager object so
     * SavedManagersFragment can deserialize it directly.
     */
    private void toggleSave(Context context, Manager manager, ViewHolder holder) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (manager.getId() == null || manager.getId().isEmpty()) {
            Toast.makeText(context, "Manager not yet in database", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid       = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String managerId = manager.getId();

        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        var docRef = db.collection("users")
                .document(uid)
                .collection("savedManagers")
                .document(managerId);

        docRef.get().addOnSuccessListener(snap -> {
            if (snap.exists()) {
                // Already saved → unsave
                docRef.delete()
                        .addOnSuccessListener(unused -> {
                            holder.imgSaved.setImageResource(R.drawable.ic_saved);
                            Toast.makeText(context,
                                    manager.getName() + " removed from saved",
                                    Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Not saved yet → save
                Map<String, Object> data = new HashMap<>();
                data.put("id",          manager.getId());
                data.put("name",        manager.getName());
                data.put("email",       manager.getEmail());
                data.put("bio",         manager.getBio());
                data.put("location",    manager.getLocation());
                data.put("phone",       manager.getPhone());
                data.put("priceRange",  manager.getPriceRange());
                data.put("rating",      manager.getRating());
                data.put("reviewCount", manager.getReviewCount());
                data.put("eventTypes",  manager.getEventTypes());

                docRef.set(data)
                        .addOnSuccessListener(unused -> {
                            holder.imgSaved.setImageResource(R.drawable.ic_bookmark); // filled
                            Toast.makeText(context,
                                    manager.getName() + " saved!",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    private void openOrCreateChat(Context context, Manager manager) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(context, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (manager.getId() == null || manager.getId().isEmpty()) {
            Toast.makeText(context,
                    "This manager is not yet linked to a Firestore account.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String hostUid     = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String managerId   = manager.getId();
        String managerName = manager.getName();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.COLLECTION_CHATS)
                .whereEqualTo("hostUid",   hostUid)
                .whereEqualTo("managerId", managerId)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        navigateToChat(context,
                                snapshot.getDocuments().get(0).getId(), managerName);
                    } else {
                        Map<String, Object> chat = new HashMap<>();
                        chat.put("hostUid",       hostUid);
                        chat.put("managerId",     managerId);
                        chat.put("managerName",   managerName);
                        chat.put("lastMessage",   "");
                        chat.put("lastTimestamp", System.currentTimeMillis());

                        db.collection(Constants.COLLECTION_CHATS)
                                .add(chat)
                                .addOnSuccessListener(ref ->
                                        navigateToChat(context, ref.getId(), managerName))
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
        TextView  tvName, tvType, tvDesc, tvRating, tvPhone;
        ImageView imgSaved;
        View      btnChat;

        ViewHolder(View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvManagerName);
            tvType   = v.findViewById(R.id.tvManagerType);
            tvDesc   = v.findViewById(R.id.tvManagerDesc);
            tvRating = v.findViewById(R.id.tvRating);
            tvPhone  = v.findViewById(R.id.tvPhone);
            imgSaved = v.findViewById(R.id.imgSaved);   // bookmark icon
            btnChat  = v.findViewById(R.id.btnChat);
        }
    }
}