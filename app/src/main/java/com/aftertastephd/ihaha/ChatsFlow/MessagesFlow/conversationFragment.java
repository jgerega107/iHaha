package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aftertastephd.ihaha.DataSources.Message;
import com.aftertastephd.ihaha.DataSources.Notification;
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
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class conversationFragment extends Fragment {

    private RecyclerView recycler;
    private ImageButton sendMessageButton;
    private ImageButton selectImageButton;
    private EditText enterTextEditText;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private CollectionReference currentUserMessagesReference;
    private CollectionReference selectedUserMessagesReference;
    private DocumentReference currentUserConversationReference;
    private DocumentReference selectedUserConversationReference;
    private FirestoreRecyclerAdapter<Message, messageQueryViewHolder> adapter;
    private String selectedUserUid;
    private static final int PICK_IMAGE_REQUEST = 140;
    private static final String TAG = "conversationActivity";
    private Uri selectedImage;
    private LinearLayout sendMessageDrawer;
    private LinearLayout sendImageDrawer;
    private RelativeLayout cannotMessageDrawer;
    private ImageButton cancelImageButton;
    private ImageButton sendImageButton;
    private ImageView selectedImageView;
    private ProgressBar uploadImageProgress;
    private FirebaseStorage storage;
    private StorageReference reference;


    public conversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_conversation, container, false);
        sendMessageDrawer = v.findViewById(R.id.sendmessagedrawer_conversationfragment);
        sendImageDrawer = v.findViewById(R.id.sendimagedrawer_conversationfragment);
        cannotMessageDrawer = v.findViewById(R.id.friendnotaddednotice_conversationfragment);

        recycler = v.findViewById(R.id.recycler_conversationfragment);
        sendMessageButton = v.findViewById(R.id.sendmessage_conversationfragment);
        selectImageButton = v.findViewById(R.id.selectimage_conversationfragment);

        cancelImageButton = v.findViewById(R.id.cancelimage_conversationfragment);
        sendImageButton = v.findViewById(R.id.sendimage_conversationfragment);
        selectedImageView = v.findViewById(R.id.sendimagepreview_conversationfragment);

        uploadImageProgress = v.findViewById(R.id.sendimagedrawer_progressbar_conversationfragment);

        enterTextEditText = v.findViewById(R.id.entermessage_conversationfragment);
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);
        selectedUserUid = getArguments().getString("CONVERSATION_UID", "");
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        currentUserMessagesReference = firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(selectedUserUid).collection("messages");
        selectedUserMessagesReference = firestore.collection("users").document(selectedUserUid).collection("conversations").document(currentUserFirebase.getUid()).collection("messages");
        currentUserConversationReference = firestore.collection("users").document(selectedUserUid).collection("conversations").document(currentUserFirebase.getUid());
        selectedUserConversationReference = firestore.collection("users").document(selectedUserUid).collection("conversations").document(currentUserFirebase.getUid());

        selectedUserConversationReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    sendMessageDrawer.setVisibility(View.GONE);
                    cannotMessageDrawer.setVisibility(View.VISIBLE);
                }
            }
        });

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

        Query query = currentUserMessagesReference.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Message, messageQueryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final messageQueryViewHolder messageQueryViewHolder, int i, @NonNull final Message message) {
                DocumentReference conversationUserReference = firestore.collection("users").document(message.getAuthorUid());
                conversationUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User conversationUser = documentSnapshot.toObject(User.class);
                        boolean isMe = currentUserFirebase.getUid().equals(message.getAuthorUid());
                        messageQueryViewHolder.setViews(conversationUser.getUsername(), conversationUser.getPfpUriAsString(), message.getMessage(), message.getTimestamp(), isMe, message.isPicture());
                    }
                });
            }

            @NonNull
            @Override
            public messageQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_query_layout, parent, false);
                return new messageQueryViewHolder(view);
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

        recycler.setAdapter(adapter);
        return v;
    }

    public void sendMessage(){
        if (!enterTextEditText.getText().toString().equals("")) {
            Message message = new Message(enterTextEditText.getText().toString(), currentUserFirebase.getUid(), false);
            currentUserMessagesReference.document().set(message);
            selectedUserMessagesReference.document().set(message);
            //notification
            final String notificationMessage = enterTextEditText.getText().toString();
            firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final User me = documentSnapshot.toObject(User.class);
                    firestore.collection("users").document(selectedUserUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User selectedUser = documentSnapshot.toObject(User.class);
                            Notification notification = new Notification(me.getUsername(), notificationMessage, selectedUserUid);
                            firestore.collection("users").document(selectedUserUid).collection("notifications").document().set(notification);
                        }
                    });
                }
            });
            Map<String, Object> conversationUpdates = new HashMap<>();
            conversationUpdates.put("lastMessageTimestamp", System.currentTimeMillis());
            conversationUpdates.put("lastMessage", enterTextEditText.getText().toString());
            conversationUpdates.put("unread", true);
            firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(selectedUserUid).update(conversationUpdates);
            firestore.collection("users").document(selectedUserUid).collection("conversations").document(currentUserFirebase.getUid()).update(conversationUpdates);
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
        final StorageReference currentUserStorageReference = storage.getReference().child("users/" + currentUserFirebase.getUid() + "/" + selectedUserUid + "/" + System.currentTimeMillis());
        final StorageReference selectedUserStorageReference = storage.getReference().child("users/" + selectedUserUid + "/" + currentUserFirebase.getUid() +  "/" + System.currentTimeMillis());
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
                        currentUserMessagesReference.document().set(imageMessage);
                    }
                });
                selectedUserStorageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        selectedUserStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Message imageMessage = new Message(uri.toString(), currentUserFirebase.getUid(), true);
                                selectedUserMessagesReference.document().set(imageMessage);
                            }
                        });
                    }
                });
                //notification
                firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final User me = documentSnapshot.toObject(User.class);
                        Notification notification = new Notification(me.getUsername(), "New Image!", selectedUserUid);
                        firestore.collection("users").document(selectedUserUid).collection("notifications").document().set(notification);
                    }
                });
                Map<String, Object> conversationUpdates = new HashMap<>();
                conversationUpdates.put("lastMessageTimestamp", System.currentTimeMillis());
                conversationUpdates.put("lastMessage", "Image");
                conversationUpdates.put("unread", true);
                firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(selectedUserUid).update(conversationUpdates);
                firestore.collection("users").document(selectedUserUid).collection("conversations").document(currentUserFirebase.getUid()).update(conversationUpdates);
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
        Map<String, Object> hasBeenReadUpdates = new HashMap<>();
        hasBeenReadUpdates.put("unread", false);
        firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(selectedUserUid).update(hasBeenReadUpdates);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
        Map<String, Object> hasBeenReadUpdates = new HashMap<>();
        hasBeenReadUpdates.put("unread", false);
        firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(selectedUserUid).update(hasBeenReadUpdates);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
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
