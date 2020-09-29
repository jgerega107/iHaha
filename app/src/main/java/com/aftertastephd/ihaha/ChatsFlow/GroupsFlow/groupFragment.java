package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toolbar;

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

import static android.app.Activity.RESULT_OK;

public class groupFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton sendMessageButton;
    private ImageButton selectImageButton;
    private EditText enterTextEditText;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private LinearLayout sendMessageDrawer;
    private LinearLayout sendImageDrawer;
    private ImageButton cancelImageButton;
    private ImageButton sendImageButton;
    private ImageView selectedImageView;
    private ProgressBar uploadImageProgress;
    private CollectionReference selectedGroupMessages;
    private DocumentReference selectedGroup;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImage;
    private static final int PICK_IMAGE_REQUEST = 103;
    private String groupInviteCode;
    private FirestoreRecyclerAdapter<Message, groupMessageQueryViewHolder> adapter;


    public groupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = v.findViewById(R.id.recycler_groupfragment);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        sendMessageDrawer = v.findViewById(R.id.sendmessagedrawer_groupfragment);
        sendImageDrawer = v.findViewById(R.id.sendimagedrawer_groupfragment);
        sendMessageButton = v.findViewById(R.id.sendmessage_groupfragment);
        selectImageButton = v.findViewById(R.id.selectimage_groupfragment);
        cancelImageButton = v.findViewById(R.id.cancelimage_groupfragment);
        sendImageButton = v.findViewById(R.id.sendimage_groupfragment);
        selectedImageView = v.findViewById(R.id.sendimagepreview_groupfragment);
        uploadImageProgress = v.findViewById(R.id.sendimagedrawer_progressbar_groupfragment);
        enterTextEditText = v.findViewById(R.id.entermessage_groupfragment);

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
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        adapter = new FirestoreRecyclerAdapter<Message, groupMessageQueryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final groupMessageQueryViewHolder groupMessageQueryViewHolder, int i, @NonNull final Message message) {
                DocumentReference conversationUserReference = firestore.collection("users").document(message.getAuthorUid());
                conversationUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User conversationUser = documentSnapshot.toObject(User.class);
                        boolean isMe = currentUserFirebase.getUid().equals(message.getAuthorUid());
                        groupMessageQueryViewHolder.setViews(conversationUser.getUsername(), conversationUser.getPfpUriAsString(), message.getMessage(), message.getTimestamp(), isMe, message.isPicture());
                    }
                });
            }

            @NonNull
            @Override
            public groupMessageQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_query_layout, parent, false);
                return new groupMessageQueryViewHolder(view);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void sendMessage(){
        if (!enterTextEditText.getText().toString().equals("")) {
            Message message = new Message(enterTextEditText.getText().toString(), currentUserFirebase.getUid(), false);
            selectedGroupMessages.document().set(message);
            Map<String, Object> groupUpdates = new HashMap<>();
            groupUpdates.put("lastMessageTimestamp", System.currentTimeMillis());
            groupUpdates.put("lastMessage", enterTextEditText.getText().toString());
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
            adapter.notifyDataSetChanged();
        }
    }

    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void sendImage(){
        sendImageButton.setVisibility(View.GONE);
        selectedImageView.setVisibility(View.GONE);
        cancelImageButton.setVisibility(View.GONE);
        uploadImageProgress.setVisibility(View.VISIBLE);
        final StorageReference currentUserStorageReference = storage.getReference().child("groups/" + groupInviteCode + "/" + currentUserFirebase.getUid() + "/" + System.currentTimeMillis());
        currentUserStorageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sendImageButton.setVisibility(View.VISIBLE);
                selectedImageView.setVisibility(View.VISIBLE);
                cancelImageButton.setVisibility(View.VISIBLE);
                uploadImageProgress.setVisibility(View.GONE);
                sendMessageDrawer.setVisibility(View.VISIBLE);
                sendImageDrawer.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                currentUserStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Message imageMessage = new Message(uri.toString(), currentUserFirebase.getUid(), true);
                        selectedGroupMessages.document().set(imageMessage);
                    }
                });
                Map<String, Object> groupUpdates = new HashMap<>();
                groupUpdates.put("lastMessageTimestamp", System.currentTimeMillis());
                groupUpdates.put("lastMessage", "Image");
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
                selectedGroup.update(groupUpdates);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Upload failed.", Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                sendMessageDrawer.setVisibility(View.GONE);
                sendImageDrawer.setVisibility(View.VISIBLE);
                selectedImageView.setImageBitmap(bitmap);
                selectedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                sendImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendImage();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
