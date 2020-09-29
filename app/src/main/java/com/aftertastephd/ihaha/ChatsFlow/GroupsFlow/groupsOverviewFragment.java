package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.ChatsFlow.MessagesFlow.conversationQueryViewHolder;
import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Collection;

import javax.annotation.Nullable;

public class groupsOverviewFragment extends Fragment {

    private Button createGroup;
    private Button joinGroup;
    private RecyclerView recyclerView;
    private groupsOverviewAdapter adapter;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private CollectionReference groups;


    public groupsOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_groupsoverview, container, false);

        createGroup = v.findViewById(R.id.creategroupbutton_groupsoverviewfragment);
        joinGroup = v.findViewById(R.id.joingroupbutton_groupsoverviewfragment);
        recyclerView = v.findViewById(R.id.recycler_groupsoverviewfragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(currentUserFirebase.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    User u = documentSnapshot.toObject(User.class);
                    adapter = new groupsOverviewAdapter(u.getGroups(), getContext());
                    recyclerView.setAdapter(adapter);
                }
            }
        });
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), newGroupActivity.class));
            }
        });
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), joinGroupActivity.class));
            }
        });
        recyclerView.setAdapter(adapter);
        return v;
    }

}
