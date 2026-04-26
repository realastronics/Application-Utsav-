package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utsav.app.R;
import com.utsav.app.models.NotificationItem;

import java.util.List;

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotifClick { void onClick(NotificationItem item); }

    private final List<NotificationItem> items;
    private final OnNotifClick listener;

    public NotificationAdapter(List<NotificationItem> items, OnNotifClick listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        NotificationItem item = items.get(position);

        // Icon — pick drawable based on iconType
        switch (item.getIconType() != null ? item.getIconType() : "") {
            case "booking":      h.ivIcon.setImageResource(R.drawable.ic_event);         break;
            case "message":      h.ivIcon.setImageResource(R.drawable.ic_chat);          break;
            case "subscription": h.ivIcon.setImageResource(R.drawable.ic_notifications); break;
            default:             h.ivIcon.setImageResource(R.drawable.ic_notifications); break;
        }

        h.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "");
        h.tvBody.setText(item.getBody()   != null ? item.getBody()  : "");
        h.tvTime.setText(item.getRelativeTime());

        // Unread dot
        h.dotUnread.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

        // Dim read items slightly
        h.itemView.setAlpha(item.isRead() ? 0.65f : 1f);

        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView  tvTitle, tvBody, tvTime;
        View      dotUnread;

        ViewHolder(View v) {
            super(v);
            ivIcon    = v.findViewById(R.id.ivNotifIcon);
            tvTitle   = v.findViewById(R.id.tvNotifTitle);
            tvBody    = v.findViewById(R.id.tvNotifBody);
            tvTime    = v.findViewById(R.id.tvNotifTime);
            dotUnread = v.findViewById(R.id.dotUnread);
        }
    }
}