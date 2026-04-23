package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utsav.app.R;
import com.utsav.app.adapters.CategoryAdapter;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rvCategories);

        setupCategories();

        view.findViewById(R.id.btnExplore)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Exploring managers...",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void setupCategories() {
        List<String> cats = Arrays.asList(
                "Birthday", "Weddings", "Cooperate"
        );
        LinearLayoutManager lm = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(lm);
        rvCategories.setAdapter(new CategoryAdapter(cats,
                category -> Toast.makeText(getContext(),
                        category, Toast.LENGTH_SHORT).show()));
    }
}