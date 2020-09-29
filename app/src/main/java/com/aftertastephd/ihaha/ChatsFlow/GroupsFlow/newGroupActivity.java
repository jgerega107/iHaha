package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class newGroupActivity extends AppCompatActivity {

    private ImageButton groupPictureButton;
    private EditText groupNameEditText;
    private EditText groupDesriptionEditText;
    private Button createGroupButton;
    private CheckBox moonbaseAlphaModeCheckbox;
    private Uri filePath;
    private StorageReference storageReference;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;

    private String groupName;
    private String groupDescription;
    private String inviteCode;
    private boolean moonbaseAlphaMode;

    private static final int PICK_IMAGE_REQUEST = 109;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        storageReference = FirebaseStorage.getInstance().getReference();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        groupPictureButton = findViewById(R.id.grouppicture_newgroupactivity);
        groupNameEditText = findViewById(R.id.editname_newgroupactivity);
        groupDesriptionEditText = findViewById(R.id.editdescription_newgroupactivity);
        createGroupButton = findViewById(R.id.creategroupbutton_groupsoverviewfragment);
        moonbaseAlphaModeCheckbox = findViewById(R.id.moonbasealphamodecheckbox_newgroupactivity);
        groupPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath == null){
                    Toast.makeText(newGroupActivity.this, "Missing group picture.", Toast.LENGTH_SHORT).show();
                }
                else if(groupNameEditText.getText().toString().equals("")){
                    Toast.makeText(newGroupActivity.this, "Missing group name.", Toast.LENGTH_SHORT).show();
                }
                else if(groupDesriptionEditText.getText().toString().equals("")){
                    Toast.makeText(newGroupActivity.this, "Missing group description.", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadImageAndCreateGroup();
                }
            }
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        groupDesriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        moonbaseAlphaModeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                moonbaseAlphaMode = b;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                groupPictureButton.setImageBitmap(bitmap);
                groupPictureButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void uploadImageAndCreateGroup(){
        final Group newGroup = new Group(groupName, groupDescription, currentUserFirebase.getUid(), moonbaseAlphaMode);
        inviteCode = newGroup.getInviteCode();
        firestore.collection("groups").document(newGroup.getInviteCode()).set(newGroup);
        if(filePath != null){
            final StorageReference currentGroupStorageReference = storageReference.child("groups").child(newGroup.getInviteCode()).child("grouppfp.jpg");
            currentGroupStorageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    currentGroupStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> groupUpdates = new HashMap<>();
                            groupUpdates.put("groupPfpUrlAsString", uri.toString());
                            firestore.collection("groups").document(newGroup.getInviteCode()).update(groupUpdates);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(newGroupActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                u.addGroup(inviteCode);
                firestore.collection("users").document(currentUserFirebase.getUid()).set(u);
            }
        });
        Intent i = new Intent(this, previewInviteCodeActivity.class);
        i.putExtra("GROUPPICTURE", filePath);
        i.putExtra("INVITECODE", inviteCode);
        startActivity(i);
        finish();
    }
}
