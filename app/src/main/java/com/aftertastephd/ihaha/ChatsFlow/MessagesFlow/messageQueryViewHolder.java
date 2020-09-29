package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import java.text.DateFormat;

public class messageQueryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View view;
    private boolean isShowingTimestamp;
    private boolean isCurrentUser;
    private boolean isPicture;
    private String author;
    private long timestamp;

    public messageQueryViewHolder(View itemView){
        super(itemView);
        view = itemView;
    }

    public void setViews(String author, String pfp, final String message, long timestamp, boolean isCurrentUser, boolean isPicture){
        this.isCurrentUser = isCurrentUser;
        this.isPicture = isPicture;
        isShowingTimestamp = false;
        this.author = author;
        this.timestamp = timestamp;

        LinearLayout sendmessage = view.findViewById(R.id.sendmessage_layout_conversationactivity);
        LinearLayout receivemessage = view.findViewById(R.id.receivemessage_layout_conversationactivity);
        LinearLayout sendimage = view.findViewById(R.id.sendimage_layout_conversationactivity);
        LinearLayout receiveimage = view.findViewById(R.id.receiveimage_layout_conversationactivity);

        if(isCurrentUser){
            if(!isPicture){
                sendmessage.setVisibility(View.VISIBLE);
                ImageView pfpView = view.findViewById(R.id.profile_picture_sendmessage_conversationactivity);
                TextView messageView = view.findViewById(R.id.message_sendmessage_conversationactivity);
                messageView.setText(message);
                TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendmessage_conversationactivity);
                authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                if(pfp != null){
                    Glide.with(view).load(pfp).centerCrop().into(pfpView);
                }
            }
            else{
                sendimage.setVisibility(View.VISIBLE);
                CardView imageCard = view.findViewById(R.id.imageview_sendimage_cardview_conversationactivity);
                imageCard.setPreventCornerOverlap(true);
                ImageView pfpView = view.findViewById(R.id.profile_picture_sendimage_conversationactivity);
                ImageButton pictureView = view.findViewById(R.id.imagebutton_sendimage_conversationactivity);
                pictureView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(message), "image/*");
                        view.getContext().startActivity(intent);
                    }
                });
                if(message != null){
                    Glide.with(view).load(message).centerCrop().into(pictureView);
                }
                TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendimage_conversationactivity);
                authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                if(pfp != null){
                    Glide.with(view).load(pfp).centerCrop().into(pfpView);
                }
            }
        }
        else{
            if(!isPicture){
                receivemessage.setVisibility(View.VISIBLE);
                ImageView pfpView = view.findViewById(R.id.profile_picture_receivemessage_conversationactivity);
                TextView messageView = view.findViewById(R.id.message_receivemessage_conversationactivity);
                messageView.setText(message);
                TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receivemessage_conversationactivity);
                authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                if(pfp != null){
                    Glide.with(view).load(pfp).centerCrop().into(pfpView);
                }
            }
            else{
                receiveimage.setVisibility(View.VISIBLE);
                CardView imageCard = view.findViewById(R.id.imageview_receiveimage_cardview_conversationactivity);
                imageCard.setPreventCornerOverlap(true);
                ImageView pfpView = view.findViewById(R.id.profile_picture_receiveimage_conversationactivity);
                ImageButton pictureView = view.findViewById(R.id.imagebutton_receiveimage_conversationactivity);
                pictureView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(message), "image/*");
                        view.getContext().startActivity(intent);
                    }
                });
                if(message != null){
                    Glide.with(view).load(message).centerCrop().into(pictureView);
                }
                TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receiveimage_conversationactivity);
                authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                if(pfp != null){
                    Glide.with(view).load(pfp).centerCrop().into(pfpView);
                }
            }
        }
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(!isShowingTimestamp){
            if(isCurrentUser){
                if(!isPicture){
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendmessage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.VISIBLE);
                }
                else{
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendimage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.VISIBLE);
                }
            }
            else{
                if(!isPicture){
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receivemessage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.VISIBLE);
                }
                else{
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receiveimage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.VISIBLE);
                }
            }
            isShowingTimestamp = true;
        }
        else {
            if(isCurrentUser){
                if(!isPicture){
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendmessage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.GONE);
                }
                else{
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_sendimage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.GONE);
                }
            }
            else{
                if(!isPicture){
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receivemessage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.GONE);
                }
                else{
                    TextView authortimestamp = view.findViewById(R.id.authorandtimestamp_receiveimage_conversationactivity);
                    authortimestamp.setText(author + " at " + DateFormat.getInstance().format(timestamp));
                    authortimestamp.setVisibility(View.GONE);
                }
            }
            isShowingTimestamp = false;
        }
    }
}
