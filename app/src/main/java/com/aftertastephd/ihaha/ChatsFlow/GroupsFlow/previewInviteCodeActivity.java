package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;

public class previewInviteCodeActivity extends AppCompatActivity {

    private ImageView groupPicture;
    private TextView inviteCodeView;
    private ImageButton copyToClipboard;
    private Button thanksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_invite_code);

        groupPicture = findViewById(R.id.grouppicture_previewinvitecodeactivity);
        inviteCodeView = findViewById(R.id.invitecode_previewinvitecodeactivity);
        copyToClipboard = findViewById(R.id.copytoclipboard_previewinvitecodeactivity);
        thanksButton = findViewById(R.id.thanks_previewinvitecodeactivity);

        Uri picture = (Uri) getIntent().getExtras().get("GROUPPICTURE");
        Glide.with(getApplicationContext()).load(picture).centerCrop().into(groupPicture);
        final String inviteCode = getIntent().getExtras().getString("INVITECODE", "");
        inviteCodeView.setText(inviteCode);
        copyToClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Invite Code", inviteCode);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        thanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
