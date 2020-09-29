package com.aftertastephd.ihaha.ProfileFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aftertastephd.ihaha.DataSources.Request;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class friendRequestViewerActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Request, friendRequestViewHolder> adapter;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request_viewer);
        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.friendrequestrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Query q = firestore.collection("users").document(user.getUid()).collection("requests");
        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(q, Request.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Request, friendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull friendRequestViewHolder friendRequestViewHolder, int i, @NonNull Request request) {
                friendRequestViewHolder.setViews(request);
            }

            @NonNull
            @Override
            public friendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_query_layout, parent, false);
                return new friendRequestViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}