package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aftertastephd.ihaha.ChatsFlow.MessagesFlow.conversationActivity;
import com.aftertastephd.ihaha.ChatsFlow.MessagesFlow.messageQueryViewHolder;
import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.Message;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class groupActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mViewPager = findViewById(R.id.viewpager_groupactivity);
        String inviteCode = getIntent().getExtras().getString("GROUP_INVITECODE", "");
        groupActivityFragmentPagerAdapter adapter = new groupActivityFragmentPagerAdapter(getSupportFragmentManager(), inviteCode);
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
