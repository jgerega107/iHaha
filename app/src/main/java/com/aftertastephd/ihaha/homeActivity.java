package com.aftertastephd.ihaha;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.ProfileFlow.FriendsFlow.addFriendActivity;
import com.aftertastephd.ihaha.ProfileFlow.profileActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;


public class homeActivity extends AppCompatActivity {
    private static final String TAG = "homeActivity";
    private DocumentReference currentUserReference;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
        currentUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Sucessfully retrieved User!");
                    refreshFcmToken();
                } else {
                    Log.d(TAG, "User does not exist, creating in Firestore.");
                    writeUser(currentUserFirebase.getDisplayName(), currentUserFirebase.getEmail(), "", currentUserFirebase.getUid(), currentUserFirebase.getPhotoUrl());
                }
            }
        });

        mViewPager = findViewById(R.id.homeViewPager);
        homeFragmentPagerAdapter adapter = new homeFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.homeTabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    private void writeUser(final String username, final String email, final String bio, final String uid, final Uri pfpUri) {
        //Get FCM token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    if(pfpUri != null){
                        User user = new User(username, email, bio, uid, pfpUri.toString(), task.getResult().getToken());
                        currentUserReference.set(user);
                    }
                    else{
                        User user = new User(username, email, bio, uid, task.getResult().getToken());
                        currentUserReference.set(user);
                    }

                }
                else{
                    User user = new User(username, email, bio, uid, pfpUri.toString(), FirebaseInstanceId.getInstance().getToken());
                    currentUserReference.set(user);
                }
            }
        });
    }

    private void refreshFcmToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Map<String, Object> fcmUpdates = new HashMap<>();
                fcmUpdates.put("fcmToken", instanceIdResult.getToken());
                currentUserReference.update(fcmUpdates);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(this, profileActivity.class));
                return true;

            case R.id.action_search:
                startActivity(new Intent(this, addFriendActivity.class));
                return true;

            case R.id.action_signout:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(homeActivity.this, mainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                            }
                        });
                return true;

            case R.id.action_about:
                startActivity(new Intent(this, aboutActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }
}
