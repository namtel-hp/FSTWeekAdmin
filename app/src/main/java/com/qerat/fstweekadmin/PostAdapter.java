package com.qerat.fstweekadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostClass> itemList;
    private Context context;

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView emailTextView, timeTextView, msgTextView;
        private Button approveButton, declineButton;
        private PostClass item;
        private CardView parent;

        public PostViewHolder(View view) {
            super(view);

            emailTextView = view.findViewById(R.id.emailTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            msgTextView = view.findViewById(R.id.msgTextView);
            approveButton = view.findViewById(R.id.approveMessageButton);
            declineButton = view.findViewById(R.id.declineMessageButton);


            parent = view.findViewById(R.id.parent);
        }

        public PostClass getItem() {
            return item;
        }

        public void setItem(PostClass item) {
            this.item = item;
        }
    }


    public PostAdapter(List<PostClass> moviesList, Context context) {
        this.itemList = moviesList;
        this.context = context;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_post, parent, false);

        return new PostViewHolder(itemView);
    }

    private int convertDpToPx(float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final PostClass item = itemList.get(position);
        holder.emailTextView.setText(item.getEmail());
        holder.timeTextView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", item.getMsgTime()));
        holder.msgTextView.setText(item.getMsg());


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        params.setMargins(convertDpToPx(8), 0, convertDpToPx(60), convertDpToPx(7));
        holder.parent.setLayoutParams(params);

        holder.approveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.approveButton.setEnabled(false);
                holder.declineButton.setEnabled(false);
                FirebaseUtilClass.getDatabaseReference().child("PanelDisc").child("pending").child(item.getPushId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUtilClass.getDatabaseReference().child("PanelDisc").child("accepted").child(item.getPushId()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                holder.approveButton.setText("Approved");
                                holder.approveButton.setEnabled(false);
                                holder.declineButton.setEnabled(false);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.approveButton.setEnabled(true);
                        holder.declineButton.setEnabled(true);
                    }
                });
            }
        });
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtilClass.getDatabaseReference().child("PanelDisc").child("pending").child(item.getPushId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        holder.declineButton.setText("Declined");
                        holder.approveButton.setEnabled(false);
                        holder.declineButton.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.approveButton.setEnabled(true);
                        holder.declineButton.setEnabled(true);
                    }
                });
            }
        });
        holder.setItem(item);

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
