package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class groupMemberListViewAdapter extends RecyclerView.Adapter<groupMemberListViewHolder> {
    private List<String> users;
    private Context context;
    private FirebaseFirestore firestore;

    public groupMemberListViewAdapter(List<String> users, Context context) {
        this.users = users;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public groupMemberListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(R.layout.groupmembers_query_layout, parent, false);
        return new groupMemberListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final groupMemberListViewHolder holder, int position) {
        firestore.collection("users").document(users.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                holder.setView(u);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
