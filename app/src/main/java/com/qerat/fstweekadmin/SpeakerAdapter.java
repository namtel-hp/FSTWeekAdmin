package com.qerat.fstweekadmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class SpeakerAdapter extends RecyclerView.Adapter<SpeakerAdapter.SpeakerViewHolder> {
    private List<SpeakerClass> itemList;
    private Context context;

    public class SpeakerViewHolder extends RecyclerView.ViewHolder {
        private TextView speakerNameTextView, speakerDetailsTextView, dayOfTalkTextView, keynoteTextView;
        private ImageView speakerImageView,noImageView;
        private SpeakerClass item;
        private LinearLayout parent;

        public SpeakerViewHolder(View view) {
            super(view);

            noImageView=view.findViewById(R.id.noImageImageView);
            speakerNameTextView = view.findViewById(R.id.speakerNameTextView);
            speakerDetailsTextView = view.findViewById(R.id.speakerDescTextView);
            dayOfTalkTextView = view.findViewById(R.id.dayOfTalkTextView);
            keynoteTextView = view.findViewById(R.id.keyNoteTextView);
            speakerImageView = view.findViewById(R.id.speakerImage);


            parent = view.findViewById(R.id.parent);
        }

        public SpeakerClass getItem() {
            return item;
        }

        public void setItem(SpeakerClass item) {
            this.item = item;
        }
    }


    public SpeakerAdapter(List<SpeakerClass> moviesList, Context context) {
        this.itemList = moviesList;
        this.context = context;
    }

    @Override
    public SpeakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_speaker, parent, false);

        return new SpeakerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SpeakerViewHolder holder, int position) {
        final SpeakerClass item = itemList.get(position);
        holder.speakerNameTextView.setText(item.getSpeakerName());
        holder.speakerDetailsTextView.setText(item.getSpeakerDetails());
        holder.dayOfTalkTextView.setText(item.getDayOfTalk());
        holder.keynoteTextView.setText(item.getSpeakerKeynote());


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SpeakerAddActivity.class);


                i.putExtra("item",item);

                context.startActivity(i);
            }
        });
        holder.setItem(item);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Speakers").child(item.getPushId());


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.noImageView.setVisibility(View.GONE);
                holder.speakerImageView.setVisibility(View.VISIBLE);
                Uri downloadUrl = uri;

                Picasso.get().load(downloadUrl.toString()).into(holder.speakerImageView);
                holder.speakerImageView.invalidate();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // holder.machineImage.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
