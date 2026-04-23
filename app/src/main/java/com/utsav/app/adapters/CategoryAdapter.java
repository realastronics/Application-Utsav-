package com.utsav.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utsav.app.R;
import java.util.List;

public class CategoryAdapter extends
        RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClick {
        void onClick(String category);
    }

    private final List<String> categories;
    private final OnCategoryClick listener;
    private int selectedPos = 0;

    public CategoryAdapter(List<String> categories,
                           OnCategoryClick listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        holder.tvCategory.setText(categories.get(position));

        if (position == selectedPos) {
            holder.tvCategory.setBackgroundResource(
                    R.drawable.bg_chip_active);
            holder.tvCategory.setTextColor(Color.WHITE);
        } else {
            holder.tvCategory.setBackgroundResource(
                    R.drawable.bg_chip_inactive);
            holder.tvCategory.setTextColor(
                    Color.parseColor("#9381FF"));
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPos;
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPos);
            listener.onClick(categories.get(
                    holder.getAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        ViewHolder(View v) {
            super(v);
            tvCategory = v.findViewById(R.id.tvCategory);
        }
    }
}