package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.ProfileFlow.FriendsFlow.profileViewerActivity;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class groupMemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View view;
    private FirebaseFirestore firestore;
    private User user;

    public groupMemberListViewHolder(View itemView){
        super(itemView);
        view = itemView;
    }

    public void setView(User u){
        user = u;
        ImageView profilePicture = view.findViewById(R.id.profile_picture_groupmembersfragment);
        TextView profileUsername = view.findViewById(R.id.user_name_groupmembersfragment);
        Glide.with(view).load(user.getPfpUriAsString()).centerCrop().into(profilePicture);
        profileUsername.setText(user.getUsername());
        if(!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v){
        Intent i = new Intent(v.getContext(), profileViewerActivity.class);
        i.putExtra("SELECTED_PROFILE_UID", user.getUid());
        v.getContext().startActivity(i);
    }
}
