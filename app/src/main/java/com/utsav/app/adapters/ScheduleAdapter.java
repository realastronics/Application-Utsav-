package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
        holder.tvLocationDate.setText(item.getLocationDateLabel());
        holder.btnChat.setOnClickListener(v -> listener.onChat(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView       tvTitle, tvLocationDate;
        MaterialButton btnChat;

        ViewHolder(View v) {
            super(v);
            tvTitle        = v.findViewById(R.id.tvScheduleTitle);
            tvLocationDate = v.findViewById(R.id.tvScheduleLocation);
            btnChat        = v.findViewById(R.id.btnScheduleChat);
        }
    }
}