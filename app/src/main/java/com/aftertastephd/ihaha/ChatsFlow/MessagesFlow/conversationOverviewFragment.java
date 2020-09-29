package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class conversationOverviewFragment extends Fragment {

    private RecyclerView messageOverviewRecycler;
    private FirebaseFirestore firestore;
    private CollectionReference conversations;
    private Query friendsQuery;
    private FirebaseUser currentUserFirebase;
    private FirestoreRecyclerAdapter<Conversation, conversationQueryViewHolder> adapter;
    private Conversation queriedConversation;
    private static final String TAG = "conversationOvFragment";

    public conversationOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messagesoverview, container, false);
        messageOverviewRecycler = v.findViewById(R.id.conversationRecycler);
        messageOverviewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        conversations = firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations");
        friendsQuery = conversations.orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Conversation> options = new FirestoreRecyclerOptions.Builder<Conversation>()
                .setQuery(friendsQuery, Conversation.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Conversation, conversationQueryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final conversationQueryViewHolder conversationQueryViewHolder, int i, @NonNull Conversation conversation) {
                firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(conversation.getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            queriedConversation = documentSnapshot.toObject(Conversation.class);
                            conversationQueryViewHolder.setViews(queriedConversation.getUserUid(), queriedConversation.getLastMessage(), queriedConversation.isUnread(), queriedConversation.isUnseenPhoto());
                        }
                    }
                });
            }

            @NonNull
            @Override
            public conversationQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_query_layout, parent, false);
                return new conversationQueryViewHolder(view);
            }
        };
        messageOverviewRecycler.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}
