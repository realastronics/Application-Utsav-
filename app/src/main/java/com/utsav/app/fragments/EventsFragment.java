package com.utsav.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.utsav.app.MainActivity;
import com.utsav.app.R;
import com.utsav.app.activities.ManagerListActivity;

public class EventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Hamburger → open sidebar (now that btnMenu exists in the XML) ──
        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openSidebar();
            }
        });

        // ── Featured banner "View" → Wedding managers ──
        view.findViewById(R.id.btnViewMain)
                .setOnClickListener(v -> openManagerList("Wedding"));

        // ── Category grid ──
        view.findViewById(R.id.btnCat1)
                .setOnClickListener(v -> openManagerList("Corporate"));

        view.findViewById(R.id.btnCat2)
                .setOnClickListener(v -> openManagerList("Birthday"));

        view.findViewById(R.id.btnCat3)
                .setOnClickListener(v -> openManagerList("Wedding"));

        view.findViewById(R.id.btnCat4)
                .setOnClickListener(v -> openManagerList("Funeral"));
    }

    private void openManagerList(String category) {
        Intent intent = new Intent(requireContext(), ManagerListActivity.class);
        if (category != null && !category.equals("All")) {
            intent.putExtra("category", category);
        }
        startActivity(intent);
    }
}