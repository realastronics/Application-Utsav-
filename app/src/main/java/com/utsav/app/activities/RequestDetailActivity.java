package com.utsav.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.utsav.app.R;
import com.utsav.app.models.EventRequest;
import com.utsav.app.utils.Constants;

public class RequestDetailActivity extends AppCompatActivity {

    private String eventId;
    private FirebaseFirestore db;
    private EventRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        eventId = getIntent().getStringExtra("eventId");
        db = FirebaseFirestore.getInstance();

        if (eventId == null) {
            Toast.makeText(this, "Error: Request ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        loadRequestDetails();
    }

    private void bindViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnAccept).setOnClickListener(v -> updateStatus(Constants.STATUS_ACCEPTED));
        findViewById(R.id.btnDecline).setOnClickListener(v -> updateStatus(Constants.STATUS_REJECTED));
        findViewById(R.id.btnSendQuote).setOnClickListener(v -> {
            Toast.makeText(this, "Quote feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRequestDetails() {
        db.collection(Constants.COLLECTION_EVENTS).document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        request = doc.toObject(EventRequest.class);
                        if (request != null) {
                            request.setId(doc.getId());
                            displayData();
                        }
                    } else {
                        Toast.makeText(this, "Request no longer exists", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load details", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayData() {
        ((TextView) findViewById(R.id.tvTitle)).setText(request.getTitle());
        ((TextView) findViewById(R.id.tvCategory)).setText(request.getType());
        ((TextView) findViewById(R.id.tvStatus)).setText(request.getStatus());
        ((TextView) findViewById(R.id.tvDate)).setText(request.getDate());
        ((TextView) findViewById(R.id.tvLocation)).setText(request.getLocation() != null ? request.getLocation() : "Not specified");
        ((TextView) findViewById(R.id.tvGuests)).setText(request.getGuests() != null ? request.getGuests() : "N/A");
        ((TextView) findViewById(R.id.tvBudget)).setText(request.getBudgetRange());
        ((TextView) findViewById(R.id.tvDescription)).setText(request.getDescription() != null ? request.getDescription() : "No description provided.");
        
        ((TextView) findViewById(R.id.tvEventType)).setText(request.getType());
        ((TextView) findViewById(R.id.tvDuration)).setText(request.getDuration() != null ? request.getDuration() : "N/A");
        ((TextView) findViewById(R.id.tvStyle)).setText(request.getStyle() != null ? request.getStyle() : "N/A");
        ((TextView) findViewById(R.id.tvSpecialRequests)).setText(request.getSpecialRequests() != null ? request.getSpecialRequests() : "None");

        // Client info
        ((TextView) findViewById(R.id.tvClientName)).setText(request.getHostName() != null ? request.getHostName() : "Client");
        ((TextView) findViewById(R.id.tvClientEmail)).setText(request.getClientEmail() != null ? request.getClientEmail() : "N/A");
        ((TextView) findViewById(R.id.tvClientPhone)).setText(request.getClientPhone() != null ? request.getClientPhone() : "N/A");
        
        // Status color
        TextView tvStatus = findViewById(R.id.tvStatus);
        if (Constants.STATUS_PENDING.equals(request.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(R.color.purple_primary));
        } else if (Constants.STATUS_ACCEPTED.equals(request.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateStatus(String status) {
        db.collection(Constants.COLLECTION_EVENTS).document(eventId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();
                    
                    if (request != null) {
                        String title = status.equals(Constants.STATUS_ACCEPTED) ? "Request Accepted! 🎉" : "Request Declined";
                        String body = "The event '" + request.getTitle() + "' has been " + status;
                        
                        // 1. Notify Host
                        com.utsav.app.activities.NotificationsActivity.pushNotification(
                            request.getHostUid(), "activity", "booking", title, body);
                            
                        // 2. Notify Manager (Self-record)
                        String managerUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                        if (managerUid != null) {
                            com.utsav.app.activities.NotificationsActivity.pushNotification(
                                managerUid, "activity", "booking", "Action Confirmed", "You " + status + " the request: " + request.getTitle());
                        }
                    }
                    
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}
