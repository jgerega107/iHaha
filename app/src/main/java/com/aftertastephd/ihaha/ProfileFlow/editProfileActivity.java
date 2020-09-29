package com.aftertastephd.ihaha.ProfileFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class editProfileActivity extends AppCompatActivity {

    private static final String TAG = "editProfileActivity";

    private ImageView pfp;
    private Button cancelChanges;
    private Button confirmChanges;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private EditText profileNameEditText;
    private EditText bioEditText;
    private ProgressBar uploadProgress;
    private LinearLayout changesLayout;
    private StorageReference storageReference;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 120;
    private DocumentReference currentUserReference;
    private String username;
    private String bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        pfp = findViewById(R.id.pfp_profileeditoractivity);
        cancelChanges = findViewById(R.id.cancel_profileeditoractivity);
        confirmChanges = findViewById(R.id.save_profileeditoractivity);
        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
        profileNameEditText = findViewById(R.id.username_profileeditoractivity);
        bioEditText = findViewById(R.id.bio_profileeditoractivity);
        uploadProgress = findViewById(R.id.progress_profileeditoractivity);
        changesLayout = findViewById(R.id.changeslayout_profileeditoractivity);
        firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Glide.with(getApplicationContext()).load(user.getPfpUriAsString()).centerCrop().into(pfp);
                profileNameEditText.setText(user.getUsername());
                bioEditText.setText(user.getBio());
            }
        });

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancelled changes.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> profileUpdates = new HashMap<>();
                profileUpdates.put("username", username);
                profileUpdates.put("bio", bio);
                currentUserReference.update(profileUpdates);
                uploadImage();
            }
        });

        profileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bio = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Glide.with(getApplicationContext()).load(filePath).centerCrop().into(pfp);
        }
    }

    public void uploadImage(){
        if(filePath != null){
            final StorageReference currentUserStorageReference = storageReference.child("users/" + currentUserFirebase.getUid() + "/pfp");
            changesLayout.setVisibility(View.GONE);
            uploadProgress.setVisibility(View.VISIBLE);
            currentUserStorageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    changesLayout.setVisibility(View.VISIBLE);
                    uploadProgress.setVisibility(View.GONE);
                    final DocumentReference currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());
                    currentUserStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Map<String, Object> pfpUpdate = new HashMap<>();
                            pfpUpdate.put("pfpUriAsString", url);
                            currentUserReference.update(pfpUpdate);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(editProfileActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Toast.makeText(getApplicationContext(), "Successfully submitted changes.", Toast.LENGTH_SHORT).show();
    }

}
