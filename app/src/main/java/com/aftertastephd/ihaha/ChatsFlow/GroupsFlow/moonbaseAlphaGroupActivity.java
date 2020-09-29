package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class moonbaseAlphaGroupActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonbase_alpha_group);

        mViewPager = findViewById(R.id.viewpager_moonbasealphagroupactivity);
        String inviteCode = getIntent().getExtras().getString("GROUP_INVITECODE", "");
        moonbaseAlphaGroupFragmentPagerAdapter adapter = new moonbaseAlphaGroupFragmentPagerAdapter(getSupportFragmentManager(), inviteCode);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("groups").document(inviteCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group g = documentSnapshot.toObject(Group.class);
                getSupportActionBar().setTitle(g.getTitle());
            }
        });
    }
}
