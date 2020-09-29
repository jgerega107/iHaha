package com.aftertastephd.ihaha.ProfileFlow;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.DataSources.Request;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class friendRequestViewHolder extends RecyclerView.ViewHolder {
    private View v;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    public friendRequestViewHolder(View v) {
        super(v);
        this.v = v;
    }

    public void setViews(Request request) {
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final RelativeLayout holder = v.findViewById(R.id.viewholder_friendrequestactivity);
        final ImageView pfp = v.findViewById(R.id.profile_picture_friendrequestactivity);
        final TextView username = v.findViewById(R.id.user_name_friendrequestactivity);
        final ImageButton accept = v.findViewById(R.id.accept_friendrequestactivity);
        final ImageButton reject = v.findViewById(R.id.reject_friendrequestactivity);

        if(!request.isReturned){
            holder.setVisibility(View.VISIBLE);
            firestore.collection("users").document(request.getAuthor()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final User u = documentSnapshot.toObject(User.class);
                    Glide.with(v).load(u.getPfpUriAsString()).centerCrop().into(pfp);
                    username.setText(u.getUsername());

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference currentRequest = firestore.collection("users").document(user.getUid()).collection("requests").document(u.getUid());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("isReturned", true);
                            currentRequest.update(updates);
                            firestore.collection("users").document(user.getUid()).collection("conversations").document(u.getUid()).set(new Conversation(u.getUid()));
                            firestore.collection("users").document(u.getUid()).collection("conversations").document(user.getUid()).set(new Conversation(user.getUid()));
                            holder.setVisibility(View.GONE);
                        }
                    });
                    reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference currentRequest = firestore.collection("users").document(user.getUid()).collection("requests").document(u.getUid());
                            currentRequest.delete();
                            holder.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }
}