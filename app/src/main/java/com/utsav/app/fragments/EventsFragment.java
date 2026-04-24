package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.utsav.app.MainActivity;
import com.utsav.app.R;

public class EventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnViewMain)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Opening Wedding managers...",
                                Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openSidebar();
            }
        });

        view.findViewById(R.id.btnCat1)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Corporate Events",
                                Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnCat2)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Birthday Events",
                                Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnCat3)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Wedding Events",
                                Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnCat4)
                .setOnClickListener(v ->
                        Toast.makeText(getContext(),
                                "Funeral Events",
                                Toast.LENGTH_SHORT).show());
    }
}