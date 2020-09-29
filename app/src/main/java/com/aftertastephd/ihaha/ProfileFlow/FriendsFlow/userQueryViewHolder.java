package com.aftertastephd.ihaha.ProfileFlow.FriendsFlow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class userQueryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View view;
    private String uid;
    private Context context;

    public userQueryViewHolder(View view){
        super(view);
        this.view = view;
        context = view.getContext();
    }

    public void setUser(String username, String pfpUri, String uid){
        this.uid = uid;
        TextView usernameView = view.findViewById(R.id.user_name_query);
        usernameView.setText(username);
        ImageView pfp = view.findViewById(R.id.profile_picture_query);
        Glide.with(view).load(pfpUri).centerCrop().into(pfp);
        if(!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v){
        Intent loadUser = new Intent(context, profileViewerActivity.class);
        loadUser.putExtra("SELECTED_PROFILE_UID", uid);
        context.startActivity(loadUser);
    }
}