package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utsav.app.R;
import com.utsav.app.models.ChatMessage;

import java.util.List;

public class MessageAdapter extends
        RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int VIEW_SENT     = 1;
    private static final int VIEW_RECEIVED = 2;

    private final List<ChatMessage> messages;
    private final String currentUid;

    public MessageAdapter(List<ChatMessage> messages, String currentUid) {
        this.messages   = messages;
        this.currentUid = currentUid;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSentByUser()
                ? VIEW_SENT : VIEW_RECEIVED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == VIEW_SENT
                ? R.layout.item_message_sent
                : R.layout.item_message_received;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMessage.setText(messages.get(position).getText());
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ViewHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tv_message_text);
        }
    }
}
