package com.utsav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName    = view.findViewById(R.id.tv_profile_name);
        TextView tvContact = view.findViewById(R.id.tv_contact);
        TextView tvAddress = view.findViewById(R.id.tv_home_address);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvName.setText(doc.getString("name"));

                        String contact = doc.getString("contactNumber");
                        tvContact.setText("• Contact: " +
                                (contact != null ? contact : "Not set"));

                        String address = doc.getString("address");
                        tvAddress.setText("• Home Address: " +
                                (address != null ? address : "Not set"));
                    }
                });
    }
}