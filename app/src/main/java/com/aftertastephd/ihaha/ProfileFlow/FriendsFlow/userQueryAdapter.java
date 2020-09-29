package com.aftertastephd.ihaha.ProfileFlow.FriendsFlow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;

import java.util.List;

public class userQueryAdapter extends RecyclerView.Adapter<userQueryViewHolder> {
    private List<User> users;
    private Context context;
    public userQueryAdapter(List<User> users, Context context){
        this.users = users;
        this.context = context;
    }
    @Override
    public userQueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(R.layout.user_query_layout, parent, false);
        return new userQueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(userQueryViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUser(user.getUsername(), user.getPfpUriAsString(), user.getUid());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}