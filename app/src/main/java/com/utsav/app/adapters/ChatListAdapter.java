package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utsav.app.R;
import com.utsav.app.models.ChatPreview;

import java.util.List;

public class ChatListAdapter extends
        RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public interface OnChatClick {
        void onClick(ChatPreview preview);
    }

    private final List<ChatPreview> chats;
    private final OnChatClick listener;

    public ChatListAdapter(List<ChatPreview> chats, OnChatClick listener) {
        this.chats    = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_preview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatPreview chat = chats.get(position);
        holder.tvName.setText(chat.getManagerName());
        holder.tvLastMessage.setText(chat.getLastMessage());
        holder.tvInitial.setText(
                chat.getManagerName() != null && !chat.getManagerName().isEmpty()
                        ? String.valueOf(chat.getManagerName().charAt(0)).toUpperCase()
                        : "?");
        holder.itemView.setOnClickListener(v -> listener.onClick(chat));
    }

    @Override
    public int getItemCount() { return chats.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvLastMessage;
        ViewHolder(View v) {
            super(v);
            tvInitial     = v.findViewById(R.id.tv_initial);
            tvName        = v.findViewById(R.id.tv_manager_name);
            tvLastMessage = v.findViewById(R.id.tv_last_message);
        }
    }
}