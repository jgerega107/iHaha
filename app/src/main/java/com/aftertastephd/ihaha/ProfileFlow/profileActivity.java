package com.aftertastephd.ihaha.ProfileFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.ProfileFlow.FriendsFlow.addFriendActivity;
import com.aftertastephd.ihaha.R;
import com.aftertastephd.ihaha.mainActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profileActivity extends AppCompatActivity {

    private static final String TAG = "profileActivity";

    private Button searchButton;
    private TextView emailTextView;
    private ImageView profilePicture;
    private TextView bioTextView;
    private FirebaseFirestore firestore;
    private DocumentReference currentUserReference;
    private User currentUser;
    private FirebaseUser currentUserFirebase;
    private TextView profileNameTextView;
    private Button editProfileButton;
    private Button friendRequestsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileNameTextView = findViewById(R.id.username_profileactivity);
        emailTextView = findViewById(R.id.email_profileactivity);
        profilePicture = findViewById(R.id.pfp_profileactivity);
        bioTextView = findViewById(R.id.bio_profileactivity);
        firestore = FirebaseFirestore.getInstance();
        editProfileButton = findViewById(R.id.edit_profileactivity);
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        friendRequestsButton = findViewById(R.id.friendrequests_profileactivity);

        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
        currentUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    currentUser = documentSnapshot.toObject(User.class);
                    updateViews(currentUser.getUsername(), currentUser.getEmail(), currentUser.getBio(), Uri.parse(currentUser.getPfpUriAsString()));
                }
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profileActivity.this, editProfileActivity.class));
            }
        });
        friendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profileActivity.this, friendRequestViewerActivity.class));
            }
        });
    }

    private void updateViews(String username, String email, String bio, Uri pfpUri) {
        profileNameTextView.setText(username);
        emailTextView.setText(email);
        bioTextView.setText(bio);
        Glide.with(getApplicationContext()).load(pfpUri).centerCrop().into(profilePicture);
    }

    private void refreshProfile(){
        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
        currentUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    currentUser = documentSnapshot.toObject(User.class);
                    updateViews(currentUser.getUsername(), currentUser.getEmail(), currentUser.getBio(), Uri.parse(currentUser.getPfpUriAsString()));
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshProfile();
    }
    @Override
    public void onStart(){
        super.onStart();
        refreshProfile();
    }
}
