package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Photo;
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


public class conversationQueryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View view;
    public String uid;
    private Context context;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private ImageButton newUnreadMessageImageButton;
    private ImageButton newUnseenImageImageButton;

    public conversationQueryViewHolder(View itemView){
        super(itemView);
        view = itemView;
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setViews(final String uid, final String latestMessage, boolean unread, boolean unseenPhoto){
        final TextView usernameView = view.findViewById(R.id.username_messageoverview);
        final ImageView pfpView = view.findViewById(R.id.profile_picture_messageoverview);
        TextView lastMessageView = view.findViewById(R.id.latestmessage_messageoverview);
        newUnreadMessageImageButton = view.findViewById(R.id.unreadmessage_messageoverview);
        newUnseenImageImageButton = view.findViewById(R.id.unseenimage_messageoverview);

        if(unread){
            newUnreadMessageImageButton.setVisibility(View.VISIBLE);
            newUnreadMessageImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newUnseenImageImageButton.setVisibility(View.GONE);
                    newUnreadMessageImageButton.setVisibility(View.GONE);
                    Intent loadConversation = new Intent(context, conversationActivity.class);
                    loadConversation.putExtra("CONVERSATION_UID", uid);
                    context.startActivity(loadConversation);
                }
            });
        }
        if(unseenPhoto){
            newUnseenImageImageButton.setVisibility(View.VISIBLE);
            firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(uid).collection("photos").document("photo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        final Photo photo = documentSnapshot.toObject(Photo.class);
                        newUnseenImageImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newUnseenImageImageButton.setVisibility(View.GONE);
                                newUnreadMessageImageButton.setVisibility(View.GONE);
                                Intent i = new Intent(view.getContext(), photoViewerActivity.class);
                                i.putExtra("PHOTO_URL", photo.getPhotoUrl());
                                i.putExtra("PHOTO_AUTHORUID", uid);
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("unseenPhoto", false);
                                firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(uid).update(updates);
                                view.getContext().startActivity(i);
                            }
                        });
                    }
                }
            });
        }
        this.uid = uid;
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(uid);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                usernameView.setText(user.getUsername());
                if(user.getPfpUriAsString() != null){
                    Glide.with(view).load(user.getPfpUriAsString()).centerCrop().into(pfpView);
                }
            }
        });

        lastMessageView.setText(latestMessage);
        view.setOnClickListener(this);
        context = view.getContext();
    }

    @Override
    public void onClick(View view){
        newUnseenImageImageButton.setVisibility(View.GONE);
        newUnreadMessageImageButton.setVisibility(View.GONE);
        Intent loadConversation = new Intent(context, conversationActivity.class);
        loadConversation.putExtra("CONVERSATION_UID", uid);
        context.startActivity(loadConversation);
    }

}
