package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.utsav.app.R;
import com.utsav.app.models.EventRequest;

import java.util.List;

public class EventRequestAdapter extends
        RecyclerView.Adapter<EventRequestAdapter.ViewHolder> {

    public interface OnAccept  { void onAccept(EventRequest req); }
    public interface OnDecline { void onDecline(EventRequest req); }

    private final List<EventRequest> items;
    private final OnAccept  onAccept;
    private final OnDecline onDecline;

    public EventRequestAdapter(List<EventRequest> items,
                               OnAccept onAccept,
                               OnDecline onDecline) {
        this.items     = items;
        this.onAccept  = onAccept;
        this.onDecline = onDecline;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventRequest req = items.get(position);

        // Avatar initial
        holder.tvInitial.setText(req.getInitial());

        // Host name — show hostName if populated, fall back to UID prefix
        String displayName = req.getHostName();
        if (displayName == null || displayName.isEmpty()) {
            String uid = req.getHostUid();
            displayName = uid != null ? uid.substring(0, Math.min(uid.length(), 8)) + "…" : "Host";
        }
        holder.tvHostName.setText(displayName);

        // Budget
        holder.tvBudget.setText(req.getBudgetDisplay());

        // Event type + date
        holder.tvEventType.setText(req.getType() != null ? req.getType() : "—");
        holder.tvEventDate.setText(req.getDate() != null ? req.getDate() : "—");

        // Buttons
        holder.btnAccept.setOnClickListener(v -> onAccept.onAccept(req));
        holder.btnDecline.setOnClickListener(v -> onDecline.onDecline(req));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView       tvInitial, tvHostName, tvBudget, tvEventType, tvEventDate;
        MaterialButton btnAccept, btnDecline;

        ViewHolder(View v) {
            super(v);
            tvInitial   = v.findViewById(R.id.tvHostInitial);
            tvHostName  = v.findViewById(R.id.tvHostName);
            tvBudget    = v.findViewById(R.id.tvBudget);
            tvEventType = v.findViewById(R.id.tvEventType);
            tvEventDate = v.findViewById(R.id.tvEventDate);
            btnAccept   = v.findViewById(R.id.btnAccept);
            btnDecline  = v.findViewById(R.id.btnDecline);
        }
    }
}