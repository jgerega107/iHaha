package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.aftertastephd.ihaha.homeActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class groupInformationFragment extends Fragment {
    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;
    private DocumentReference currentGroup;
    private DocumentReference currentUserReference;

    public groupInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_group_information, container, false);
        final ImageView groupPicture = v.findViewById(R.id.grouppicture_groupinformationfragment);
        final TextView groupTitle = v.findViewById(R.id.grouptitle_groupinformationfragment);
        final TextView groupDescription = v.findViewById(R.id.groupdescription_groupinformationfragment);
        final TextView inviteCodeView = v.findViewById(R.id.invitecode_groupinformationfragment);
        Button leaveGroup = v.findViewById(R.id.leave_groupinformationfragment);
        ImageButton copyClipboard = v.findViewById(R.id.copytoclipboard_groupinformationfragment);
        firestore = FirebaseFirestore.getInstance();


        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        currentUserReference = firestore.collection("users").document(currentUserFirebase.getUid());

        final String inviteCode = getArguments().getString("GROUP_INVITECODE", "");
        currentGroup = firestore.collection("groups").document(inviteCode);

        currentGroup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group g = documentSnapshot.toObject(Group.class);
                Glide.with(v).load(g.getGroupPfpUrlAsString()).centerCrop().into(groupPicture);
                groupTitle.setText(g.getTitle());
                groupDescription.setText(g.getDescription());
                inviteCodeView.setText(g.getInviteCode());
            }
        });

        copyClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Invite Code", inviteCode);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGroup.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        startActivity(new Intent(getContext(), homeActivity.class));
                        final Group g = documentSnapshot.toObject(Group.class);
                        if(g.getUids().size() == 1){
                            currentGroup.delete();
                        }
                        else{
                            g.removeUser(currentUserFirebase.getUid());
                            currentGroup.set(g);
                        }
                        currentUserReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User u = documentSnapshot.toObject(User.class);
                                u.removeGroup(g.getInviteCode());
                                currentUserReference.set(u);
                            }
                        });
                        getActivity().finish();
                    }
                });
            }
        });

        return v;
    }
}
