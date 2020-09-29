package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class photoViewerActivity extends AppCompatActivity {

    private TextView authorTextView;
    private ImageButton newImageButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.activity_photo_viewer);

        authorTextView = findViewById(R.id.photoviewer_author);
        newImageButton = findViewById(R.id.photoviewer_image);
        firestore = FirebaseFirestore.getInstance();

        String uri = getIntent().getExtras().getString("PHOTO_URL", "");
        String author = getIntent().getExtras().getString("PHOTO_AUTHORUID", "");

        firestore.collection("users").document(author).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                authorTextView.setText(user.getUsername());
            }
        });

        newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(getApplicationContext()).load(uri).centerCrop().into(newImageButton);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 10000);
    }
}
