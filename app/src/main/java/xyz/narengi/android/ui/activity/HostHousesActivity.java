package xyz.narengi.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import xyz.narengi.android.R;
import xyz.narengi.android.ui.adapter.HostHousesContentRecyclerAdapter;

public class HostHousesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_houses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.host_houses_toolbar);
        setSupportActionBar(toolbar);

        setupContentRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.host_houses_addHouseFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddHouse();
            }
        });
    }



    private void setupContentRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.host_houses_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        HostHousesContentRecyclerAdapter contentRecyclerAdapter = new HostHousesContentRecyclerAdapter(this, null);
        mRecyclerView.setAdapter(contentRecyclerAdapter);
    }

    private void openAddHouse() {
        Intent intent = new Intent(this, AddHouseActivity.class);
        startActivity(intent);
    }
}
