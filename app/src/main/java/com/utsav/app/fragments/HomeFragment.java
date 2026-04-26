package com.utsav.app.fragments;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.MainActivity;
import com.utsav.app.R;
import com.utsav.app.activities.ManagerListActivity;
import com.utsav.app.adapters.CategoryAdapter;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    // ---------------------------------------------------------------
    // Views
    // ---------------------------------------------------------------
    private TextView        tvGreeting;
    private RecyclerView    rvCategories;

    // ---------------------------------------------------------------
    // Firebase
    // ---------------------------------------------------------------
    private FirebaseFirestore db;
    private FirebaseAuth      mAuth;

    // ---------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase
        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Bind views
        tvGreeting   = view.findViewById(R.id.tvGreeting);
        rvCategories = view.findViewById(R.id.rvCategories);

        // Notifications
        view.findViewById(R.id.btnNotifications)
                .setOnClickListener(v ->
                        startActivity(new Intent(requireContext(),
                                com.utsav.app.activities.NotificationsActivity.class)));

        // Wire up hamburger → open sidebar in parent MainActivity
        view.findViewById(R.id.btnMenu)
                .setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openSidebar();
                    }
                });

        // "Explore →" button → ManagerListActivity
        view.findViewById(R.id.btnExplore)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(requireContext(),
                                        ManagerListActivity.class)));

        // Manager cards (static for now — replaced by Firestore later)
        view.findViewById(R.id.cardManager1)
                .setOnClickListener(v -> openManagerList("Wedding"));

        view.findViewById(R.id.cardManager2)
                .setOnClickListener(v -> openManagerList("Corporate"));

        // Load data
        loadGreeting();
        setupCategories();
    }

    // ---------------------------------------------------------------
    // Greeting  — pulls user name from Firestore
    // ---------------------------------------------------------------

    private void loadGreeting() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && tvGreeting != null) {
                        String name = doc.getString("name");
                        if (name != null && !name.isEmpty()) {
                            // Show only first name to keep it friendly
                            String firstName = name.split(" ")[0];
                            tvGreeting.setText("Hi " + firstName + "!");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Non-critical — leave default text in XML
                });
    }

    // ---------------------------------------------------------------
    // Category chip rail
    // ---------------------------------------------------------------

    private void setupCategories() {
        List<String> cats = Arrays.asList(
                "All", "Birthday", "Weddings", "Corporate", "Concerts"
        );

        LinearLayoutManager lm = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvCategories.setLayoutManager(lm);
        rvCategories.setAdapter(new CategoryAdapter(cats, this::openManagerList));
    }

    // ---------------------------------------------------------------
    // Navigation helper
    // ---------------------------------------------------------------

    /**
     * Opens ManagerListActivity pre-filtered by category.
     * Pass null / "All" to show every manager.
     */
    private void openManagerList(String category) {
        Intent intent = new Intent(requireContext(), ManagerListActivity.class);
        if (category != null && !category.equals("All")) {
            intent.putExtra("category", category);
        }
        startActivity(intent);
    }
}