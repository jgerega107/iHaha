package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class groupsOverviewAdapter extends RecyclerView.Adapter<groupQueryViewHolder> {
    private List<String> groups;
    private Context context;
    private FirebaseFirestore firestore;

    public groupsOverviewAdapter(List<String> groups, Context context){
        this.groups = groups;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public groupQueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(R.layout.group_query_layout, parent, false);
        return new groupQueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final groupQueryViewHolder holder, int position){
        firestore.collection("groups").document(groups.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Group g = documentSnapshot.toObject(Group.class);
                    holder.setView(g);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return groups.size();
    }
}
