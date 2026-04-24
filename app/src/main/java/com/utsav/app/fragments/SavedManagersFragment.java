package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.MainActivity;
import com.utsav.app.R;
import com.utsav.app.adapters.ManagerAdapter;
import com.utsav.app.models.Manager;

import java.util.ArrayList;
import java.util.List;

public class SavedManagersFragment extends Fragment {

    private RecyclerView rvSaved;
    private TextView tvEmpty;
    private ManagerAdapter adapter;
    private final List<Manager> savedList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_managers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Hamburger ────────────────────────────────────────────────────────
        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openSidebar();
            }
        });

        tvEmpty  = view.findViewById(R.id.tvEmpty);
        rvSaved  = view.findViewById(R.id.rvSaved);
        rvSaved.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ManagerAdapter(savedList);
        rvSaved.setAdapter(adapter);

        loadSavedManagers();
    }

    /**
     * Reads the "savedManagers" sub-collection under the current user's document.
     *
     * Firestore path:  users/{uid}/savedManagers/{managerId}
     * Each document is a full Manager object written by ManagerAdapter's bookmark tap.
     */
    private void loadSavedManagers() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("savedManagers")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null || !isAdded()) return;

                    savedList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        Manager m = doc.toObject(Manager.class);
                        if (m != null) {
                            m.setId(doc.getId());   // restore Firestore doc ID
                            savedList.add(m);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(savedList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvSaved.setVisibility(savedList.isEmpty()  ? View.GONE   : View.VISIBLE);
                });
    }
}