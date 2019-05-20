package com.qerat.fstweekadmin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends AppCompatActivity {
    private RecyclerView purchaseRecyclerView;
    private List<EventClass> itemList = new ArrayList<>();
    private EventsAdapter mAdapter;
    private SwipeRefreshLayout refreshlayout;
    private Spinner daySpinner;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_events);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshlayout = findViewById(R.id.refreshingLayout);
        refreshlayout.setRefreshing(true);
        daySpinner = findViewById(R.id.daySpinner);
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readDataFromFirebase();
            }
        });

        initializeRecyclerView();

        readDataFromFirebase();


        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readDataFromFirebase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initializeRecyclerView() {
        purchaseRecyclerView = findViewById(R.id.eventRecycler);

        mAdapter = new EventsAdapter(itemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        purchaseRecyclerView.setLayoutManager(mLayoutManager);
        purchaseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        purchaseRecyclerView.setAdapter(mAdapter);
    }

    public void readDataFromFirebase() {
        FirebaseUtilClass.getDatabaseReference().child("Talks").child(daySpinner.getSelectedItem().toString()).orderByChild("eventTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    itemList.add(dsp.getValue(EventClass.class)); //add result into array list
                }
                mAdapter.notifyDataSetChanged();
                refreshlayout.setRefreshing(false);

                //   Toast.makeText(getContext(), "Changed something", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EventsFragment.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                refreshlayout.setRefreshing(false);
            }
        });
    }
}
