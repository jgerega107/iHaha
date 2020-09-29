package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.Message;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class moonbaseAlphaGroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton sendMessageButton;
    private EditText enterTextEditText;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private LinearLayout sendMessageDrawer;
    private ImageButton allowT2SButton;
    private ImageButton disallowT2SButton;
    private CollectionReference selectedGroupMessages;
    private DocumentReference selectedGroup;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String groupInviteCode;
    private boolean allowT2S;
    private FirestoreRecyclerAdapter<Message, moonbaseAlphaMessageQueryViewHolder> adapter;

    private static final String TAG = "mbaGroupFragment";

    public moonbaseAlphaGroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moonbase_alpha_group, container, false);
        recyclerView = v.findViewById(R.id.recycler_moonbasealphagroupfragment);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        sendMessageDrawer = v.findViewById(R.id.sendmessagedrawer_moonbasealphagroupfragment);
        sendMessageButton = v.findViewById(R.id.sendmessage_moonbasealphagroupfragment);
        enterTextEditText = v.findViewById(R.id.entermessage_moonbasealphagroupfragment);

        allowT2SButton = v.findViewById(R.id.allowt2s_moonbasealphagroupfragment);
        disallowT2SButton = v.findViewById(R.id.disallowt2s_moonbasealphagroupfragment);

        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        groupInviteCode = getArguments().getString("GROUP_INVITECODE", "");

        selectedGroupMessages = firestore.collection("groups").document(groupInviteCode).collection("messages");
        selectedGroup = firestore.collection("groups").document(groupInviteCode);


        Query groupMessageQuery = selectedGroupMessages.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(groupMessageQuery, Message.class)
                .build();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        adapter = new FirestoreRecyclerAdapter<Message, moonbaseAlphaMessageQueryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final moonbaseAlphaMessageQueryViewHolder moonbaseAlphaMessageQueryViewHolder, int i, @NonNull final Message message) {
                DocumentReference conversationUserReference = firestore.collection("users").document(message.getAuthorUid());
                conversationUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User conversationUser = documentSnapshot.toObject(User.class);
                        boolean isMe = currentUserFirebase.getUid().equals(message.getAuthorUid());
                        moonbaseAlphaMessageQueryViewHolder.setViews(conversationUser.getUsername(), conversationUser.getPfpUriAsString(), message.getMessage(), message.getTimestamp(), allowT2S);
                    }
                });
            }

            @NonNull
            @Override
            public moonbaseAlphaMessageQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.t2smessage_query_layout, parent, false);
                return new moonbaseAlphaMessageQueryViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        allowT2SButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowT2S = true;
                allowT2SButton.setVisibility(View.GONE);
                disallowT2SButton.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Autoplay on", Toast.LENGTH_SHORT).show();
            }
        });
        disallowT2SButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowT2S = false;
                disallowT2SButton.setVisibility(View.GONE);
                allowT2SButton.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Autoplay off", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public void sendMessage(){
        if (!enterTextEditText.getText().toString().equals("")) {
            Message message = new Message(enterTextEditText.getText().toString(), currentUserFirebase.getUid(), false);
            selectedGroupMessages.document(currentUserFirebase.getUid()).set(message);
            Map<String, Object> groupUpdates = new HashMap<>();
            groupUpdates.put("lastMessageTimestamp", System.currentTimeMillis());
            groupUpdates.put("lastMessage", "? ? ?");
            selectedGroup.update(groupUpdates);
            selectedGroup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        Group g = documentSnapshot.toObject(Group.class);
                        int sizeOfGroup = g.getUids().size();
                        Map<String, Object> unreadUpdates = new HashMap<>();
                        List<Boolean> unreads = new ArrayList<>();
                        for(int i = 0; i < sizeOfGroup; i++){
                            unreads.add(true);
                        }
                        unreadUpdates.put("unreads", unreads);
                        selectedGroup.update(unreadUpdates);
                    }
                }
            });
            enterTextEditText.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
        selectedGroup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Group g = documentSnapshot.toObject(Group.class);
                    if(g.getUids().contains(currentUserFirebase.getUid())){
                        int indexOfMe = g.getUids().indexOf(currentUserFirebase.getUid());
                        List<Boolean> unreads = g.getUnreads();
                        unreads.set(indexOfMe, false);
                        Map<String, Object> hasBeenReadUpdates = new HashMap<>();
                        hasBeenReadUpdates.put("unreads", unreads);
                        selectedGroup.update(hasBeenReadUpdates);
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
        selectedGroup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Group g = documentSnapshot.toObject(Group.class);
                    if(g.getUids().contains(currentUserFirebase.getUid())){
                        int indexOfMe = g.getUids().indexOf(currentUserFirebase.getUid());
                        List<Boolean> unreads = g.getUnreads();
                        unreads.set(indexOfMe, false);
                        Map<String, Object> hasBeenReadUpdates = new HashMap<>();
                        hasBeenReadUpdates.put("unreads", unreads);
                        selectedGroup.update(hasBeenReadUpdates);
                    }
                }
            }
        });
    }
}
    

