package com.aftertastephd.ihaha.CameraFlow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aftertastephd.ihaha.R;

import java.io.File;

public class onPictureTakenActivity extends AppCompatActivity {

    private static final String PICTURE = "PICTURE";
    private File pictureTaken;
    private ImageView cameraPreview;
    private ImageButton backButton;
    private ImageButton checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.activity_on_picture_taken);
        pictureTaken = (File) getIntent().getExtras().get(PICTURE);
        final Bitmap imagePreview = BitmapFactory.decodeFile(pictureTaken.getAbsolutePath());
        cameraPreview = findViewById(R.id.image_preview);
        cameraPreview.setImageBitmap(imagePreview);
        cameraPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        backButton = findViewById(R.id.back_button);
        checkButton = findViewById(R.id.check_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), selectChatActivity.class);
                i.putExtra("PICTURE", pictureTaken);
                startActivity(i);
            }
        });
    }

    public static Intent newIntent(Context packageContext, File picture){
        Intent i = new Intent(packageContext, onPictureTakenActivity.class);
        i.putExtra(PICTURE, picture);
        return i;
    }
}
