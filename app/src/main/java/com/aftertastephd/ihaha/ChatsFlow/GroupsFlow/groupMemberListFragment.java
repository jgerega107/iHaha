package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aftertastephd.ihaha.ChatsFlow.MessagesFlow.messageQueryViewHolder;
import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class groupMemberListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;
    private groupMemberListViewAdapter adapter;

    private static final String TAG = "groupMemberListFragment";

    public groupMemberListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_member_list, container, false);
        recyclerView = v.findViewById(R.id.recycler_groupmemberlistfragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final String inviteCode = getArguments().getString("GROUP_INVITECODE", "");
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();

        firestore.collection("groups").document(inviteCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group g = documentSnapshot.toObject(Group.class);
                adapter = new groupMemberListViewAdapter(g.getUids(), getContext());
                recyclerView.setAdapter(adapter);
            }
        });
        return v;
    }

}
