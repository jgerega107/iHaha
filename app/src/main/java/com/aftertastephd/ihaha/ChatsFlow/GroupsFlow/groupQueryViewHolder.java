package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class groupQueryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    //test
    private View view;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUserFirebase;
    private int indexOfMe;
    private Group g;
    public groupQueryViewHolder(View itemView){
        super(itemView);
        view = itemView;
        firestore = FirebaseFirestore.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
    }
    public void setView(Group g){
        this.g = g;
        indexOfMe = g.getUids().indexOf(currentUserFirebase.getUid());
        LinearLayout linearLayout = view.findViewById(R.id.viewholder_groupoverview);
        linearLayout.setVisibility(View.VISIBLE);
        ImageView groupPicture = view.findViewById(R.id.grouppicture_groupoverview);
        TextView groupName = view.findViewById(R.id.groupname_groupoverview);
        ImageButton newMessageNotificationView = view.findViewById(R.id.newmessage_groupoverview);
        TextView latestMessage = view.findViewById(R.id.latestmessage_groupoverview);
        if(g.getGroupPfpUrlAsString() != null){
            Glide.with(view).load(g.getGroupPfpUrlAsString()).centerCrop().into(groupPicture);
        }
        groupName.setText(g.getTitle());
        latestMessage.setText(g.getLastMessage());
        if(g.getUnreads().get(indexOfMe)){
            newMessageNotificationView.setVisibility(View.VISIBLE);
        }
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(g.isMoonbaseAlphaMode()){
            Intent i = new Intent(view.getContext(), moonbaseAlphaGroupActivity.class);
            i.putExtra("GROUP_INVITECODE", g.getInviteCode());
            view.getContext().startActivity(i);
        }
        else {
            Intent i = new Intent(view.getContext(), groupActivity.class);
            i.putExtra( "GROUP_INVITECODE", g.getInviteCode());
            view.getContext().startActivity(i);
        }
    }
}
