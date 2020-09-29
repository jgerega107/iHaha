package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class conversationActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        mViewPager = findViewById(R.id.viewpager_conversationactivity);
        String uid = getIntent().getExtras().getString("CONVERSATION_UID", "");
        conversationActivityFragmentPagerAdapter adapter = new conversationActivityFragmentPagerAdapter(getSupportFragmentManager(), uid);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User conversationUser = documentSnapshot.toObject(User.class);
                getSupportActionBar().setTitle(conversationUser.getUsername());
            }
        });
    }


}
