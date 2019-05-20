package com.qerat.fstweekadmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<EventClass> itemList;
    private Context context;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, speakerNameTextView, timeTextView, locationTextView, dateTextView;
        private ImageView noImageImageView, speakerImageView;
        private EventClass item;
        private LinearLayout parent;

        public EventViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            speakerNameTextView = view.findViewById(R.id.speakerNameTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            locationTextView = view.findViewById(R.id.locationTextView);
            noImageImageView = view.findViewById(R.id.noImageImageView);
            speakerImageView = view.findViewById(R.id.speakerImageView);
            dateTextView=view.findViewById(R.id.dateTextView);

            parent = view.findViewById(R.id.parent);
        }

        public EventClass getItem() {
            return item;
        }

        public void setItem(EventClass item) {
            this.item = item;
        }
    }


    public EventsAdapter(List<EventClass> moviesList, Context context) {
        this.itemList = moviesList;
        this.context = context;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_conference, parent, false);

        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final EventClass item = itemList.get(position);
        holder.titleTextView.setText(item.getEventTitle());
        holder.locationTextView.setText(item.getEventLocation());
        holder.timeTextView.setText(item.getEventTime());
        holder.speakerNameTextView.setText(item.getSpeakerName());
        holder.dateTextView.setText(item.getEventDate());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TalkEventAddActivity.class);


                i.putExtra("item",item);

                context.startActivity(i);
            }
        });
        holder.setItem(item);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Speakers").child(item.getSpeakerPushId());


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.noImageImageView.setVisibility(View.GONE);
                holder.speakerImageView.setVisibility(View.VISIBLE);
                Uri downloadUrl = uri;

                Picasso.get().load(downloadUrl.toString()).into(holder.speakerImageView);
                holder.speakerImageView.invalidate();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  holder.machineImage.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
