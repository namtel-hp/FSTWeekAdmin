package com.qerat.fstweekadmin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SpeakerViewActivity extends AppCompatActivity {
    private RecyclerView purchaseRecyclerView;
    private SpeakerAdapter mAdapter;
    private List<SpeakerClass> itemList = new ArrayList<>();

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);

        purchaseRecyclerView = findViewById(R.id.recyclerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new SpeakerAdapter(itemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        purchaseRecyclerView.setLayoutManager(mLayoutManager);
        purchaseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        purchaseRecyclerView.setAdapter(mAdapter);

        readDataFromFirebase();
    }


    public void readDataFromFirebase() {
        FirebaseUtilClass.getDatabaseReference().child("Speakers").orderByChild("speakerName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    itemList.add(dsp.getValue(SpeakerClass.class)); //add result into array list
                }
                mAdapter.notifyDataSetChanged();
                //  refreshlayout.setRefreshing(false);

                //   Toast.makeText(getContext(), "Changed something", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SpeakerViewActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // refreshlayout.setRefreshing(false);
            }
        });
    }
}
