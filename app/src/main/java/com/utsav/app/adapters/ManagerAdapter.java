package com.utsav.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utsav.app.R;
import com.utsav.app.models.Manager;
import java.util.List;

public class ManagerAdapter extends
        RecyclerView.Adapter<ManagerAdapter.ViewHolder> {

    private final List<Manager> managers;

    public ManagerAdapter(List<Manager> managers) {
        this.managers = managers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manager_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        Manager m = managers.get(position);

        holder.tvName.setText(m.getName());
        holder.tvType.setText(m.getEventType() + " Manager");
        holder.tvDesc.setText(m.getDescription());
        holder.tvRating.setText(String.valueOf(m.getRating()));
        holder.tvPhone.setText(m.getPhone());

        holder.btnChat.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Opening chat with " + m.getName(),
                        Toast.LENGTH_SHORT).show());

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Opening " + m.getName() + "'s profile",
                        Toast.LENGTH_SHORT).show());
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