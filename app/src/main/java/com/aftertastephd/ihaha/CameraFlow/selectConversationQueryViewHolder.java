package com.aftertastephd.ihaha.CameraFlow;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class selectConversationQueryViewHolder extends RecyclerView.ViewHolder{
    private View view;
    private static List<String> uids = new ArrayList<>();

    public selectConversationQueryViewHolder(View itemView){
        super(itemView);
        view = itemView;
    }

    public void setViews(final String uid, String latestMessage){
        final TextView usernameView = view.findViewById(R.id.username_selectconversationoverview);
        final ImageView pfpView = view.findViewById(R.id.profile_picture_selectconversationoverview);
        TextView lastMessageView = view.findViewById(R.id.latestmessage_selectconversationoverview);
        CheckBox selectedUser = view.findViewById(R.id.selectusercheckbox_selectconversationoverview);
        selectedUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    uids.add(uid);
                }
                else if(!isChecked){
                    uids.remove(uid);
                }
            }
        });
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(uid);
        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                usernameView.setText(user.getUsername());
                if(user.getPfpUriAsString() != null){
                    Glide.with(view).load(user.getPfpUriAsString()).centerCrop().into(pfpView);
                }
            }
        });

        lastMessageView.setText(latestMessage);
    }

    public static List<String> getUidList(){
        return uids;
    }
    public static void wipeUidList(){
        uids = new ArrayList<>();
    }

}