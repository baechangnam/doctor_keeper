package com.apps.doctorkeeper_android.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;


import com.apps.doctorkeeper_android.db.RbPreference;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.util.CommonUtils;


import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;



public class BaseActivity extends AppCompatActivity {
    public RbPreference pref;
    Context mContext;
    public String login_id = "";
    public String device_id = "";
    public CompositeDisposable compositeDisposable = new CompositeDisposable();
    AppCompatDialog progressDialog;
    public Activity baseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new RbPreference(this);
        mContext = BaseActivity.this;
        baseActivity = BaseActivity.this;

        login_id = pref.getValue(RbPreference.MEM_ID, "");
        device_id = CommonUtils.getDeviceID(mContext);

    }

    public void progressON(Activity activity) {

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET();
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.view_progressbar);
            progressDialog.show();
        }


    }

    public void progressSET() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

    }


    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void hideKeypad(Context context, View view){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideBottomMenu() {
    }

    public void showBottomMenu() {
    }

    public void startFragment(BaseFragment fragment) {
    }

    public void removeFragment(BaseFragment fragment) {

    }

    public void addFragment(BaseFragment fragment) {

    }


    public void exitApp() {
        finish();
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification() {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String id = "doctorkeeper_android";
        CharSequence name = "doctorkeeper_android";
        String description = "doctorkeeper_android desc";
        NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mChannel.setLightColor(Color.RED);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        mChannel.setSound(RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);


        mChannel.setShowBadge(true);
        mNotificationManager.createNotificationChannel(mChannel);
    }


    public void showErrorDialogNoCancel(String msg) {
        AlertDialog.Builder adialog = new AlertDialog.Builder(
                BaseActivity.this);
        adialog.setMessage(msg)
                .setPositiveButton("확인",
                        (dialog, which) -> finish())
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });

        AlertDialog alert = adialog.create();
        alert.show();
    }

    public void showErrorDialog(String msg) {
        AlertDialog.Builder adialog = new AlertDialog.Builder(
                BaseActivity.this);
        adialog.setMessage(msg)
                .setPositiveButton("확인",
                        (dialog, which) -> dialog.dismiss());

        AlertDialog alert = adialog.create();
        alert.show();
    }

    public void showErrorDialogTheme(String msg) {
        AlertDialog.Builder adialog = new AlertDialog.Builder(
                BaseActivity.this);
        adialog.setMessage(msg)
                .setPositiveButton("확인",
                        (dialog, which) -> dialog.dismiss());

        AlertDialog alert = adialog.create();
        alert.show();
    }


    @Override
    protected void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }


        super.onDestroy();

    }

    /**
     * disposable을 추가한다.
     * 추가된 disposable은 Activity가 destroy될 때 일괄 처분된다.
     *
     * @param disposable disposable 인스턴스
     */
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void moveActivity(Bundle bundle, Class<? extends Activity> activityClass) {
        try {
            Intent intent = new Intent(baseActivity, activityClass);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        Intent i = new Intent(BaseActivity.this, BaseActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void showBottomMenu(String tabname){

    }

    public void refreshRecord(){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

