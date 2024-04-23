package com.apps.doctorkeeper_android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.apps.doctorkeeper_android.db.RbPreference;

import com.apps.doctorkeeper_android.util.CommonUtils;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;



public class BaseFragment extends Fragment {
    protected static String TAG;
    public BaseActivity mActivity;
    public RbPreference pref;
    public String login_id = "";
    public String device_id = "";
    Boolean isLogin;

    /**
     * Disposable 컨테이너
     */
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }

        pref = new RbPreference(context);
        login_id = pref.getValue(RbPreference.MEM_ID, "");
        isLogin = pref.getValue(RbPreference.IS_LOGIN, false);
        device_id = CommonUtils.getDeviceID(context);

    }

    public void hideKeypad(Context context, View view){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void replaceFragment(Fragment fragment, int resId) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(resId, fragment, fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void showProgressDialog() {
        mActivity.progressON(mActivity);
    }

    public void hideProgressDialog() {
        mActivity.progressOFF();
    }

    public void showErrorDialog(String msg) {
        mActivity.showErrorDialog(msg);
    }

    public void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();

    }

}
