package com.qerat.fstweekadmin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MentorGroupAdapter extends RecyclerView.Adapter<MentorGroupAdapter.PostViewHolder> {
    private List<MentorGroupClass> itemList;
    private Context context;

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView purposeTextView, areaTextView, addedTextView;
 
        private MentorGroupClass item;
        private LinearLayout parent;

        public PostViewHolder(View view) {
            super(view);

            purposeTextView = view.findViewById(R.id.purposeTextView);
            areaTextView = view.findViewById(R.id.areaTextView);
            addedTextView = view.findViewById(R.id.addedTextView);



            parent = view.findViewById(R.id.parent);
        }

        public MentorGroupClass getItem() {
            return item;
        }

        public void setItem(MentorGroupClass item) {
            this.item = item;
        }
    }


    public MentorGroupAdapter(List<MentorGroupClass> moviesList, Context context) {
        this.itemList = moviesList;
        this.context = context;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_mentor_groups, parent, false);

        return new PostViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final MentorGroupClass item = itemList.get(position);
        holder.purposeTextView.setText(item.getPurpose());
        holder.areaTextView.setText(item.getArea());
        holder.addedTextView.setText(item.getAdded());



        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AddMeetUpActivity.class);
                i.putExtra("fstem","true");
                i.putExtra("purpose",item.getPurpose());
                i.putExtra("area",item.getArea());
                context.startActivity(i);
            }
        });

        holder.setItem(item);

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
