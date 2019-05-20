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

public class MentorGroupsAcitivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MentorGroupAdapter mAdapter;
    private List<MentorGroupClass> itemList = new ArrayList<>();


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
        setContentView(R.layout.activity_mentor_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recyclerView);

        mAdapter = new MentorGroupAdapter(itemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        readDataFromFirebase();

    }


    public void readDataFromFirebase() {
        FirebaseUtilClass.getDatabaseReference().child("MentorshipGroups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String str=dsp.getKey();
                    String[] composite=str.split("_");
                    final String purpose=composite[0];
                    final String area=composite[1];
                    FirebaseUtilClass.getDatabaseReference().child("MentorshipGroups").child(str).child("meetup").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String added;
                            if(dataSnapshot.exists()){
                                added="event added";
                            }else {
                                added="no event added";
                            }
                            itemList.add(new MentorGroupClass(purpose,area,added));
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                mAdapter.notifyDataSetChanged();
                //  refreshlayout.setRefreshing(false);

                //   Toast.makeText(getContext(), "Changed something", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MentorGroupsAcitivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // refreshlayout.setRefreshing(false);
            }
        });
    }
}
