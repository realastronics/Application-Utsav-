package com.utsav.app.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.utsav.app.MainActivity;
import com.utsav.app.R;
import com.utsav.app.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateEventFragment extends Fragment {

    // ── Views ────────────────────────────────────────────────────────────────
    private TextInputEditText etTitle, etDescription, etDate, etTime, etLocation, etAudience;
    private AutoCompleteTextView actvBudget;
    private MaterialButton btnCorporate, btnWedding, btnBirthday, btnConcert;
    private CheckBox cbDJ, cbLiveBand, cbCatering, cbDecoration, cbPhotography, cbVideography;
    private RadioGroup rgVisibility;
    private RadioButton rbPublic, rbInviteOnly;
    private MaterialButton btnPostEvent;
    private ProgressBar progressBar;

    // ── State ────────────────────────────────────────────────────────────────
    private String selectedEventType = "";   // one of Corporate / Wedding / Birthday / Concert
    private final List<MaterialButton> typeButtons = new ArrayList<>();

    // ── Budget options ───────────────────────────────────────────────────────
    private static final String[] BUDGET_OPTIONS = {
            "Under ₹50,000",
            "₹50,000 – ₹1,00,000",
            "₹1,00,000 – ₹3,00,000",
            "₹3,00,000 – ₹5,00,000",
            "₹5,00,000 – ₹10,00,000",
            "Above ₹10,00,000"
    };

    // ────────────────────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupHamburger(view);
        setupEventTypeToggle();
        setupDatePicker();
        setupTimePicker();
        setupBudgetDropdown();
        setupSubmit();
    }

    // ── Bind ─────────────────────────────────────────────────────────────────
    private void bindViews(View view) {
        etTitle       = view.findViewById(R.id.et_event_title);
        etDescription = view.findViewById(R.id.et_description);
        etDate        = view.findViewById(R.id.et_event_date);
        etTime        = view.findViewById(R.id.et_event_time);
        etLocation    = view.findViewById(R.id.et_location);
        etAudience    = view.findViewById(R.id.et_audience);
        actvBudget    = view.findViewById(R.id.actvBudget);

        btnCorporate  = view.findViewById(R.id.btnTypeCorporate);
        btnWedding    = view.findViewById(R.id.btnTypeWedding);
        btnBirthday   = view.findViewById(R.id.btnTypeBirthday);
        btnConcert    = view.findViewById(R.id.btnTypeConcert);

        cbDJ          = view.findViewById(R.id.cbDJ);
        cbLiveBand    = view.findViewById(R.id.cbLiveBand);
        cbCatering    = view.findViewById(R.id.cbCatering);
        cbDecoration  = view.findViewById(R.id.cbDecoration);
        cbPhotography = view.findViewById(R.id.cbPhotography);
        cbVideography = view.findViewById(R.id.cbVideography);

        rgVisibility  = view.findViewById(R.id.rgVisibility);
        rbPublic      = view.findViewById(R.id.rbPublic);
        rbInviteOnly  = view.findViewById(R.id.rbInviteOnly);

        btnPostEvent  = view.findViewById(R.id.btnPostEvent);
        progressBar   = view.findViewById(R.id.progressBar);

        typeButtons.addAll(Arrays.asList(btnCorporate, btnWedding, btnBirthday, btnConcert));
    }

    // ── Hamburger ─────────────────────────────────────────────────────────────
    private void setupHamburger(View view) {
        view.findViewById(R.id.btnMenu).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openSidebar();
            }
        });
    }

    // ── Event Type Toggle ─────────────────────────────────────────────────────
    private void setupEventTypeToggle() {
        btnCorporate.setOnClickListener(v -> selectEventType("Corporate", btnCorporate));
        btnWedding.setOnClickListener(v  -> selectEventType("Wedding",   btnWedding));
        btnBirthday.setOnClickListener(v -> selectEventType("Birthday",  btnBirthday));
        btnConcert.setOnClickListener(v  -> selectEventType("Concert",   btnConcert));
    }

    private void selectEventType(String type, MaterialButton selected) {
        selectedEventType = type;
        for (MaterialButton btn : typeButtons) {
            boolean isActive = btn == selected;
            btn.setBackgroundColor(isActive
                    ? getResources().getColor(android.R.color.transparent, null) : 0x00000000);
            if (isActive) {
                btn.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF9381FF));
                btn.setTextColor(0xFFFFFFFF);
            } else {
                btn.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0x00000000));
                btn.setTextColor(0xFF9381FF);
                btn.setStrokeColor(
                        android.content.res.ColorStateList.valueOf(0xFF9381FF));
            }
        }
    }

    // ── Date Picker ───────────────────────────────────────────────────────────
    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(
                    requireContext(),
                    R.style.UtsavDatePickerDialog,
                    (dp, year, month, day) ->
                            etDate.setText(String.format(Locale.getDefault(),
                                    "%02d/%02d/%04d", month + 1, day, year)),
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Also trigger on end-icon tap
        view_til_click(R.id.til_event_date, etDate);
    }

    // ── Time Picker ───────────────────────────────────────────────────────────
    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(
                    requireContext(),
                    R.style.UtsavDatePickerDialog,
                    (tp, hour, minute) -> {
                        String ampm = hour >= 12 ? "PM" : "AM";
                        int h12 = hour % 12 == 0 ? 12 : hour % 12;
                        etTime.setText(String.format(Locale.getDefault(),
                                "%02d:%02d %s", h12, minute, ampm));
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
            ).show();
        });
    }

    // Helper — makes the TextInputLayout end-icon also open the picker
    private void view_til_click(int tilId, TextInputEditText target) {
        // The end-icon forwards its click to the EditText automatically
        // when the EditText is non-focusable, so nothing extra needed here.
    }

    // ── Budget Dropdown ───────────────────────────────────────────────────────
    private void setupBudgetDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                BUDGET_OPTIONS
        );
        actvBudget.setAdapter(adapter);
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    private void setupSubmit() {
        btnPostEvent.setOnClickListener(v -> {
            if (!validate()) return;
            postEvent();
        });
    }

    private boolean validate() {
        String title = text(etTitle);
        if (title.isEmpty()) {
            etTitle.setError("Event title is required");
            etTitle.requestFocus();
            return false;
        }
        if (selectedEventType.isEmpty()) {
            Toast.makeText(getContext(), "Please select an event type", Toast.LENGTH_SHORT).show();
            return false;
        }
        String desc = text(etDescription);
        if (desc.isEmpty()) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return false;
        }
        if (desc.length() < 50) {
            etDescription.setError("Minimum 50 characters required");
            etDescription.requestFocus();
            return false;
        }
        if (text(etDate).isEmpty()) {
            Toast.makeText(getContext(), "Please select an event date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (text(etTime).isEmpty()) {
            Toast.makeText(getContext(), "Please select an event time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (text(etLocation).isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
            return false;
        }
        if (text(etAudience).isEmpty()) {
            etAudience.setError("Expected audience is required");
            etAudience.requestFocus();
            return false;
        }
        if (text(actvBudget).isEmpty()) {
            Toast.makeText(getContext(), "Please select a budget range", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void postEvent() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        String hostUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Collect services
        List<String> services = new ArrayList<>();
        if (cbDJ.isChecked())          services.add("DJ");
        if (cbLiveBand.isChecked())    services.add("Live Band");
        if (cbCatering.isChecked())    services.add("Catering");
        if (cbDecoration.isChecked())  services.add("Decoration");
        if (cbPhotography.isChecked()) services.add("Photography");
        if (cbVideography.isChecked()) services.add("Videography");

        String visibility = rbPublic.isChecked() ? "public" : "invite_only";

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("hostUid",      hostUid);
        eventData.put("title",        text(etTitle));
        eventData.put("type",         selectedEventType);
        eventData.put("description",  text(etDescription));
        eventData.put("date",         text(etDate));
        eventData.put("time",         text(etTime));
        eventData.put("location",     text(etLocation));
        eventData.put("guestCount",   parseAudience());
        eventData.put("budgetRange",  text(actvBudget));
        eventData.put("services",     services);
        eventData.put("visibility",   visibility);
        eventData.put("status",       Constants.STATUS_PENDING);
        eventData.put("createdAt",    System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_EVENTS)
                .add(eventData)
                .addOnSuccessListener(ref -> {
                    setLoading(false);
                    Toast.makeText(getContext(),
                            "Event posted successfully! 🎉",
                            Toast.LENGTH_LONG).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(getContext(),
                            "Failed to post event. Try again.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String text(android.widget.TextView tv) {
        return tv.getText() != null ? tv.getText().toString().trim() : "";
    }

    private int parseAudience() {
        try {
            return Integer.parseInt(text(etAudience));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPostEvent.setEnabled(!loading);
    }

    private void clearForm() {
        etTitle.setText("");
        etDescription.setText("");
        etDate.setText("");
        etTime.setText("");
        etLocation.setText("");
        etAudience.setText("");
        actvBudget.setText("", false);
        selectedEventType = "";
        for (MaterialButton btn : typeButtons) {
            btn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0x00000000));
            btn.setTextColor(0xFF9381FF);
        }
        cbDJ.setChecked(false);
        cbLiveBand.setChecked(false);
        cbCatering.setChecked(false);
        cbDecoration.setChecked(false);
        cbPhotography.setChecked(false);
        cbVideography.setChecked(false);
        rbPublic.setChecked(true);
    }
}