package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utsav.app.R;
import com.utsav.app.models.ScheduleItem;

import java.util.List;

public class ScheduleAdapter extends
        RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface OnChatClick { void onChat(ScheduleItem item); }

    private final List<ScheduleItem> items;
    private final OnChatClick listener;

    public ScheduleAdapter(List<ScheduleItem> items, OnChatClick listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = items.get(position);
        holder.tvTitle.setText(item.getDisplayTitle());
        holder.tvLocation.setText(item.getLocation() != null ? item.getLocation() : "TBD");
        
        // Date Badge logic: Convert MM/dd/yyyy to something like "18 May"
        String rawDate = item.getDate();
        if (rawDate != null && rawDate.contains("/")) {
            try {
                String[] parts = rawDate.split("/");
                int monthIdx = Integer.parseInt(parts[0]) - 1;
                String day = parts[1];
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                holder.tvDateBadge.setText(day + " " + months[monthIdx]);
            } catch (Exception e) {
                holder.tvDateBadge.setText(rawDate);
            }
        } else {
            holder.tvDateBadge.setText(rawDate != null ? rawDate : "—");
        }

        holder.btnChat.setOnClickListener(v -> listener.onChat(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvTitle, tvLocation, tvDateBadge, tvTime;
        ImageView btnChat;

        ViewHolder(View v) {
            super(v);
            tvTitle     = v.findViewById(R.id.tvScheduleTitle);
            tvLocation  = v.findViewById(R.id.tvScheduleLocation);
            tvDateBadge = v.findViewById(R.id.tvScheduleDateBadge);
            tvTime      = v.findViewById(R.id.tvScheduleTime);
            btnChat     = v.findViewById(R.id.btnScheduleChat);
        }
    }
}