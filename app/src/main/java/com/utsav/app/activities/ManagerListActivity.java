package com.utsav.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utsav.app.R;
import com.utsav.app.adapters.ManagerAdapter;
import com.utsav.app.utils.DataProvider;

public class ManagerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);

        RecyclerView rv = findViewById(R.id.rvManagers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ManagerAdapter(
                DataProvider.getManagers()));
    }
}