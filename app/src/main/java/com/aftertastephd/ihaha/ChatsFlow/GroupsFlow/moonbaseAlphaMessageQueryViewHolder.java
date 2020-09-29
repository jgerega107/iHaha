package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aftertastephd.ihaha.R;
import com.aftertastephd.ihaha.getRedirectURLAsyncTask;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DateFormat;

public class moonbaseAlphaMessageQueryViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "mbaMessageQueryVH";
    private String redirectURL;
    private View view;
    private String author;
    private String message;
    private long timestamp;
    private boolean allowT2S;
    private ImageView profilePicture;
    private TextView timestampTextView;
    private ImageButton copyT2S;
    private RelativeLayout viewholder;

    public moonbaseAlphaMessageQueryViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setViews(String author, String pfp, final String message, long timestamp, final boolean allowT2S) {
        this.author = author;
        this.timestamp = timestamp;
        this.message = message;
        this.allowT2S = allowT2S;

        profilePicture = view.findViewById(R.id.profile_picture_moonbasealphamessagequeryvh);
        timestampTextView = view.findViewById(R.id.timestamp_textview_moonbasealphamessagequeryvh);
        copyT2S = view.findViewById(R.id.copyt2s_moonbasealphamessagequeryvh);
        viewholder = view.findViewById(R.id.viewholder_t2smessage);
        viewholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getRedirectURLAsyncTask().execute(message);
            }
        });
        if (pfp != null) {
            Glide.with(view).load(pfp).centerCrop().into(profilePicture);
        }
        timestampTextView.setText(author + " at " + DateFormat.getInstance().format(timestamp));
        copyT2S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Message", message);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(view.getContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
        if (allowT2S) {
            new getRedirectURLAsyncTask().execute(message);
            //this api sucks, perhaps get raspberry pi???
        }
    }
}
