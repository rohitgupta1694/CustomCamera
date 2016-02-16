package com.dogether.dogether.customcamera.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.dogether.dogether.customcamera.R;
import com.dogether.dogether.customcamera.utils.Constants;
import com.dogether.dogether.customcamera.utils.Util;
import com.dogether.dogether.dogethercamera.Camera.CameraActivity;
import com.dogether.dogether.dogethercamera.Camera.ImageUtility;
import com.dogether.dogether.dogethercamera.VideoRecorder.ui.activities.VideoRecorderActivity;

import java.io.File;

/**
 * Created by dogether on 12/2/16.
 */
public class MainActivity extends AppCompatActivity{

    public static String TAG;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_VIDEO = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Point mSize;
    private TransferUtility mTransferUtility;
    private TransferObserver mTransferObserver;
    private File mImageLocation;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            mImageLocation = new File(photoUri.getPath());
            // Get the bitmap in according to the width of the device
             Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.x, mSize.x);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
        }
        else if(requestCode == REQUEST_VIDEO){
            Log.d("VideoRecorder","Video Saved");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadImageToServer(){
        mTransferUtility = Util.getTransferUtility(this);
        String fileName = mImageLocation.toString().trim().
                substring(mImageLocation.toString().trim().lastIndexOf('/')+1);
        TAG = fileName;
        Log.d("Bucket TAG",TAG);
        Log.d("Bucket FileName",fileName);
        mTransferObserver = mTransferUtility.upload(Constants.BUCKET_NAME,fileName,mImageLocation);
        Log.d("Bucket",mTransferObserver.getAbsoluteFilePath());
        Log.d("Bucket URL",Util.getBucketURL()+"");
    }

    public void requestForCameraPermission(View view) {
        mView = view;
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);
            }
        } else {
            launch();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    private void launch() {
        switch(mView.getId()){
            case R.id.launch_camera:
                Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
                break;
            case R.id.launch_video:
                Intent startCustomVideoRecorderIntent = new Intent(this, VideoRecorderActivity.class);
                startActivityForResult(startCustomVideoRecorderIntent, REQUEST_VIDEO);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launch();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}