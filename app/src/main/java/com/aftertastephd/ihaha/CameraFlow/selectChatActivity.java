package com.aftertastephd.ihaha.CameraFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftertastephd.ihaha.DataSources.Conversation;
import com.aftertastephd.ihaha.DataSources.Notification;
import com.aftertastephd.ihaha.DataSources.Photo;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.aftertastephd.ihaha.homeActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class selectChatActivity extends AppCompatActivity {

    private RecyclerView conversationRecycler;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;
    private CollectionReference conversations;
    private Query friendsQuery;
    private ImageView imagePreview;
    private ImageButton sendImages;
    private TextView fileName;
    private File takenImage;
    private FirestoreRecyclerAdapter<Conversation, selectConversationQueryViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference photoReference;
    private Uri filePath;
    private static final String TAG = "selectChatActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);

        getSupportActionBar().setTitle("Select a chat to send to");

        conversationRecycler = findViewById(R.id.conversationrecycler_selectchatactivity);
        imagePreview = findViewById(R.id.imagepreview_selectchatactivity);
        sendImages = findViewById(R.id.sendimages_selectchatactivity);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        takenImage = (File) getIntent().getExtras().get("PICTURE");
        filePath = Uri.fromFile(takenImage);
        Bitmap image = BitmapFactory.decodeFile(takenImage.getAbsolutePath());
        imagePreview.setImageBitmap(image);
        fileName = findViewById(R.id.filename_selectchatactivity);
        fileName.setText(System.currentTimeMillis() + ".jpg");
        imagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        conversations = firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations");
        friendsQuery = conversations.orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Conversation> options = new FirestoreRecyclerOptions.Builder<Conversation>()
                .setQuery(friendsQuery, Conversation.class)
                .build();

        sendImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
                startActivity(new Intent(selectChatActivity.this, homeActivity.class));
                finish();
            }
        });

        adapter = new FirestoreRecyclerAdapter<Conversation, selectConversationQueryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final selectConversationQueryViewHolder selectConversationQueryViewHolder, int i, @NonNull Conversation conversation) {
                firestore.collection("users").document(currentUserFirebase.getUid()).collection("conversations").document(conversation.getUserUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Conversation queriedConversation = documentSnapshot.toObject(Conversation.class);
                        selectConversationQueryViewHolder.setViews(queriedConversation.getUserUid(), queriedConversation.getLastMessage());
                    }
                });
            }

            @NonNull
            @Override
            public selectConversationQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectconversation_query_layout, parent, false);
                return new selectConversationQueryViewHolder(view);
            }
        };
        conversationRecycler.setAdapter(adapter);
    }

    public void uploadPhoto() {
        photoReference = storageReference.child("users").child(currentUserFirebase.getUid()).child("photos").child("photo.jpg");
        photoReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final List<String> uids = selectConversationQueryViewHolder.getUidList();
                photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoUrl = uri.toString();
                        for (int i = 0; i < uids.size(); i++) {
                            Photo photo = new Photo(uids.get(i), photoUrl);
                            firestore.collection("users").document(uids.get(i)).collection("conversations").document(currentUserFirebase.getUid()).collection("photos").document("photo").set(photo);
                            Map<String, Object> conversationUpdates = new HashMap<>();
                            conversationUpdates.put("unseenPhoto", true);
                            firestore.collection("users").document(uids.get(i)).collection("conversations").document(currentUserFirebase.getUid()).update(conversationUpdates);
                        }
                    }
                });
                for(int k = 0; k < uids.size(); k++){
                    //notification
                    final int index = k;
                    firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final User me = documentSnapshot.toObject(User.class);
                            Notification notification = new Notification(me.getUsername(), "New camera message!", uids.get(index));
                            firestore.collection("users").document(uids.get(index)).collection("notifications").document().set(notification);
                        }
                    });
                }
            }
        });
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
}
