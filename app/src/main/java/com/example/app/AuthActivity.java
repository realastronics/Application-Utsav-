package com.utsav.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class AuthActivity extends AppCompatActivity {

    private TextView tabLogin, tabRegister;
    private TextInputLayout tilName;
    private Button btnAuth;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLogin  = findViewById(R.id.tab_login);
        tabRegister = findViewById(R.id.tab_register);
        tilName   = findViewById(R.id.til_name);
        btnAuth   = findViewById(R.id.btn_auth);

        tabLogin.setOnClickListener(v -> switchMode(true));
        tabRegister.setOnClickListener(v -> switchMode(false));

        btnAuth.setOnClickListener(v -> {
            if (isLoginMode) {
                // Returning user — go straight to SelectActorActivity
                // Later: validate credentials against Firebase here
                goToSelectActor();
            } else {
                // New user — go to SelectActorActivity to pick role
                goToSelectActor();
            }
        });
    }

    private void switchMode(boolean loginMode) {
        isLoginMode = loginMode;

        if (loginMode) {
            tabLogin.setTextColor(getColor(R.color.purple));
            tabLogin.setTextSize(15);
            tabRegister.setTextColor(0xFFAAAAAA);
            tilName.setVisibility(View.GONE);
            btnAuth.setText("Login");
        } else {
            tabRegister.setTextColor(getColor(R.color.purple));
            tabLogin.setTextColor(0xFFAAAAAA);
            tilName.setVisibility(View.VISIBLE);
            btnAuth.setText("Create Account");
        }
    }

    private void goToSelectActor() {
        Intent intent = new Intent(AuthActivity.this, SelectActorActivity.class);
        startActivity(intent);
        // Do not call finish() here — user should be able to press back to auth
    }
}