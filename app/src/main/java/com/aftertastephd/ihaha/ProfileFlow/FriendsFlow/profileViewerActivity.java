package com.aftertastephd.ihaha.ProfileFlow.FriendsFlow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.DataSources.Notification;
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

public class profileViewerActivity extends AppCompatActivity {

    private ImageView pfpImageView;
    private TextView usernameTextView;
    private TextView bio;
    private Button addFriendButton;
    private CardView friendAlreadyAddedButton;
    private FirebaseFirestore firestore;
    private DocumentReference currentUserReference;
    private DocumentReference selectedUserReference;
    private User selectedUser;
    private FirebaseUser currentUserFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        final String uid = getIntent().getExtras().getString("SELECTED_PROFILE_UID");

        pfpImageView = findViewById(R.id.pfp_profilevieweractivity);
        usernameTextView = findViewById(R.id.username_profilevieweractivity);
        addFriendButton = findViewById(R.id.sendrequest_profilevieweractivity);
        bio = findViewById(R.id.bio_profilevieweractivity);
        friendAlreadyAddedButton = findViewById(R.id.friendalreadyadded_profilevieweractivity);
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        selectedUserReference = firestore.collection("users").document(uid);
        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
        selectedUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                selectedUser = documentSnapshot.toObject(User.class);
                usernameTextView.setText(selectedUser.getUsername());
                bio.setText(selectedUser.getBio());
                Glide.with(getApplicationContext()).load(selectedUser.getPfpUriAsString()).centerCrop().into(pfpImageView);
            }
        });

        selectedUserReference.collection("requests").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    friendAlreadyAddedButton.setVisibility(View.VISIBLE);
                    addFriendButton.setVisibility(View.GONE);
                }
                else{
                    addFriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectedUserReference.collection("requests").document(currentUserFirebase.getUid()).set(new Request(currentUserFirebase.getUid()));
                            addFriendButton.setVisibility(View.GONE);
                            friendAlreadyAddedButton.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        });

    }
}
