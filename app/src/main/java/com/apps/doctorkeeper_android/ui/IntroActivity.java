package com.apps.doctorkeeper_android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.db.RbPreference;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.ui.login.LoginActivity;
import com.apps.doctorkeeper_android.ui.main.MainActivity;
import com.apps.doctorkeeper_android.util.CommonUtils;

public class IntroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                checkNetWork();

            } else {
                ActivityCompat.requestPermissions(IntroActivity.this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, 1);
            }
        }else{
            if ( ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                checkNetWork();

            } else {
                ActivityCompat.requestPermissions(IntroActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, 1);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                checkNetWork();
            }
        }

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    //네트워크 체크
    private void checkNetWork() {
        if (CommonUtils.getInstance().isNetwork(this)) {
            goMain();
        } else {
            showErrorDialogNoCancel(getResources().getString(R.string.toast_network_error));
        }
    }

    private void goMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pref.getValue(RbPreference.IS_LOGIN, false)) {
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                }


            }
        },2000);

    }



}