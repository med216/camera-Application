package com.example.mycameraplante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "" ;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String LOG_TAG = "";
    private MediaRecorder mediaRecorder;
    private static final int pic_id = 123;


    private Camera mCamera;
    private CameraPreview mPreview;
    Context context;
    View view;
    SurfaceHolder holder;
    private Intent service;
    FloatingActionButton floatingActionButton2;
    int key = 60000;
    private StorageReference Folder;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Folder= FirebaseStorage.getInstance().getReference().child("ImageFolder");

        checkPermissions();
         // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        imageView = findViewById(R.id.imageView);

        preview.addView(mPreview);
        floatingActionButton2 = findViewById(R.id.floatingActionButton2);

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);

        // get an image from the camera




        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 show(key);

            }
        });

    }

    private void show(final int s){
        Calendar cal = Calendar.getInstance();
        Log.d(TAG, "onCreate: 1");
        service = new Intent(getBaseContext(), CapPhoto.class);
        Log.d(TAG, "onCreate: 2");

        Log.d(TAG, "onCreate: 3");
        //TAKE PHOTO EVERY 15 SECONDS
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, service, 0);
        Log.d(TAG, "onCreate: 3");
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "onCreate: 4");
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),s, pintent);
        Log.d(TAG, "onCreate: 5");
        startService(service);
        Log.d(TAG, "onCreate: 6");
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
        /*
          Call stopPreview() to stop updating the preview surface.
        */
            mCamera.stopPreview();
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
        /*
          Call stopPreview() to stop updating the preview surface.
        */
            mCamera.stopPreview();

        /*
          Important: Call release() to release the camera for use by other applications.
          Applications should release the camera immediately in onPause() (and re-open() it in
          onResume()).
        */
            mCamera.release();

            mCamera = null;
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        mCamera = Camera.open();
        CameraPreview cameraPreview = new CameraPreview(this, mCamera);
        FrameLayout previewFL = (FrameLayout)  findViewById(R.id.camera_preview);

        // preview is required. But you can just cover it up in the layout.
        previewFL.addView(cameraPreview);
        mCamera.startPreview();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera = Camera.open();
        CameraPreview cameraPreview = new CameraPreview(this, mCamera);
        FrameLayout previewFL = (FrameLayout)  findViewById(R.id.camera_preview);

        // preview is required. But you can just cover it up in the layout.
        previewFL.addView(cameraPreview);
        mCamera.startPreview();
    }

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};




    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
//                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
//                            .show();
                }
                // permissions list of don't granted permission
            }
            return;
        }
    }


}
