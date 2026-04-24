package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName    = view.findViewById(R.id.tv_profile_name);
        TextView tvContact = view.findViewById(R.id.tv_contact);
        TextView tvAddress = view.findViewById(R.id.tv_home_address);

        // ── FIX: guard against null user (e.g. signed-out edge case) ──
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(),
                    "Please log in to view your profile", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && isAdded()) {
                        String name = doc.getString("name");
                        tvName.setText(name != null ? name : "—");

                        String contact = doc.getString("contactNumber");
                        tvContact.setText("• Contact: " +
                                (contact != null ? contact : "Not set"));

                        String address = doc.getString("address");
                        tvAddress.setText("• Home Address: " +
                                (address != null ? address : "Not set"));
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(),
                                "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });

        // ── Edit Profile button ──
        View btnEdit = view.findViewById(R.id.btn_edit_profile);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v ->
                    Toast.makeText(getContext(),
                            "Edit profile coming soon", Toast.LENGTH_SHORT).show());
        }
    }
}