package com.aftertastephd.ihaha.CameraFlow;


import android.content.Intent;
import android.os.Bundle;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.aftertastephd.ihaha.R;
import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;

public class cameraFragment extends Fragment {

    private static final String TAG = "cameraFragment";
    private CameraKitView cameraKitView;
    private ImageButton takepicture;
    private ImageButton switchcamera;
    private boolean facing;

    public cameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraKitView = v.findViewById(R.id.camera);
        takepicture = v.findViewById(R.id.camera_takephoto);
        switchcamera = v.findViewById(R.id.camera_switchcamera);
        facing = false;

        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                        try {
                            FileOutputStream stream = new FileOutputStream(savedPhoto.getPath());
                            stream.write(bytes);
                            stream.close();
                            Intent i = onPictureTakenActivity.newIntent(getContext(), savedPhoto);
                            startActivity(i);
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        cameraKitView.setGestureListener(new CameraKitView.GestureListener() {
            @Override
            public void onTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onLongTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onDoubleTap(CameraKitView cameraKitView, float v, float v1) {
                if(facing){
                    cameraKitView.setFacing(CameraKit.FACING_BACK);
                    facing = false;
                }
                else if(!facing){
                    cameraKitView.setFacing(CameraKit.FACING_FRONT);
                    facing = true;
                }
            }

            @Override
            public void onPinch(CameraKitView cameraKitView, float v, float v1, float v2) {
                Log.d(TAG, "tried pinching");
                cameraKitView.setZoomFactor(v1);
                //TODO:look into pinch to zoom
            }
        });

        switchcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(facing){
                    cameraKitView.setFacing(CameraKit.FACING_BACK);
                    facing = false;
                }
                else if(!facing){
                    cameraKitView.setFacing(CameraKit.FACING_FRONT);
                    facing = true;
                }
            }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
