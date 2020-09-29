package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.aftertastephd.ihaha.homeActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class conversationProfileViewer extends Fragment {

    private ImageView pfp;
    private TextView username;
    private TextView bio;
    private Button removeFriend;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;

    private String uid;


    public conversationProfileViewer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_conversation_profile_viewer, container, false);
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        uid = getArguments().getString("CONVERSATION_UID", "");
        pfp = v.findViewById(R.id.pfp_conversationprofileviewer);
        username = v.findViewById(R.id.username_conversationprofileviewer);
        bio = v.findViewById(R.id.bio_conversationprofileviewer);
        removeFriend = v.findViewById(R.id.remove_conversationprofileviewer);

        firestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                Glide.with(v).load(u.getPfpUriAsString()).centerCrop().into(pfp);
                username.setText(u.getUsername());
                bio.setText(u.getBio());
            }
        });

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference currentConversation = firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(uid);
                DocumentReference selectedConversation = firestore.collection("users").document(uid).collection("conversations").document(currentUserFirebase.getUid());
                currentConversation.delete();
                selectedConversation.delete();
                startActivity(new Intent(getActivity(), homeActivity.class));
                getActivity().finish();
            }
        });

        return v;
    }

}
