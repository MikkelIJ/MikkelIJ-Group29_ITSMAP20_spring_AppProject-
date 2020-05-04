package com.smap.group29.getmoving.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.snackbar.Snackbar;
import com.smap.group29.getmoving.activities.LoginActivity;

// heavily inspired by https://stackoverflow.com/questions/34040355/how-to-check-the-multiple-permission-at-single-request-in-android-m
public class CheckPermissions extends Activity {


    private static final int RQ_CODE = 1;
    private Context mContext;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermissions(final LoginActivity loginActivity){

        if ((ContextCompat.checkSelfPermission(loginActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(loginActivity,
                Manifest.permission.ACTIVITY_RECOGNITION)) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (loginActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (loginActivity, Manifest.permission.ACTIVITY_RECOGNITION)) {
                Snackbar.make(loginActivity.findViewById(android.R.id.content),
                        "GetMoving needs your permission to use activity sensor and storage " + ("\ud83d\ude2f"),
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                loginActivity.requestPermissions(
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACTIVITY_RECOGNITION},
                                        RQ_CODE);
                            }
                        }).show();
            } else {

                    ActivityCompat.requestPermissions((loginActivity),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACTIVITY_RECOGNITION},
                            RQ_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case RQ_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile)
                    {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to upload profile photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{Manifest.permission
                                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.ACTIVITY_RECOGNITION},
                                                RQ_CODE);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }
}