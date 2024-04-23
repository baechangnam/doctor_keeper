package com.apps.doctorkeeper_android.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ConsultDocFragment;
import com.apps.doctorkeeper_android.ui.consult.PDFViewerFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerAgreeFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerAgreementTemplateDialog;
import com.apps.doctorkeeper_android.ui.customer.CustomerFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerImageFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerImageMergeFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerImageShareFragment;
import com.apps.doctorkeeper_android.ui.customer.PDFSignFragment;
import com.apps.doctorkeeper_android.ui.setting.SettingFragment;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private com.apps.doctorkeeper_android.databinding.ActivityMainBinding binding;
    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = com.apps.doctorkeeper_android.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

    }

    private void init() {
        HomeFragment fragment = HomeFragment.newInstance(null);
        startFragment(fragment);

        binding.btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMenu("setting");
                SettingFragment settingFragment = SettingFragment.newInstance(null);
                startFragment(settingFragment);

            }
        });

        binding.btConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMenu("consult");
                ConsultDocFragment consultDocFragment = ConsultDocFragment.newInstance(null);
                startFragment(consultDocFragment);

            }
        });

        binding.btCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomMenu("customer");
                CustomerFragment customerFragment = CustomerFragment.newInstance(null);
                startFragment(customerFragment);

            }
        });
    }

    @Override
    public void startFragment(BaseFragment fragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();

        mCurrentFragment = fragment;
    }

    @Override
    public void addFragment(BaseFragment fragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, fragment, fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();

        mCurrentFragment = fragment;
    }

    @Override
    public void removeFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commitAllowingStateLoss();

        mCurrentFragment = fragment;

    }

    @Override
    public void refreshRecord() {

    }

    @Override
    public void hideBottomMenu() {
        binding.viewBottom.setVisibility(View.GONE);
    }

    @Override
    public void showBottomMenu() {
        binding.viewBottom.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBottomMenu(String tabname) {
        binding.viewBottom.setVisibility(View.VISIBLE);
        clearBtn();
        if ("setting".equals(tabname)) {
            binding.btSetting.setBackgroundColor(getColor(R.color.colorMain));
            binding.btSetting.setTextColor(getColor(R.color.white));

            Drawable img = getResources().getDrawable(R.drawable.btn_home_setting_on);
            binding.btSetting.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        } else if ("consult".equals(tabname)) {
            binding.btConsult.setBackgroundColor(getColor(R.color.colorMain));
            binding.btConsult.setTextColor(getColor(R.color.white));

            Drawable img = getResources().getDrawable(R.drawable.btn_home_consult_on);
            binding.btConsult.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        } else if ("customer".equals(tabname)) {
            binding.btCustomer.setBackgroundColor(getColor(R.color.colorMain));
            binding.btCustomer.setTextColor(getColor(R.color.white));

            Drawable img = getResources().getDrawable(R.drawable.btn_home_customer_on);
            binding.btCustomer.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        }
    }

    private void clearBtn(){
        binding.btSetting.setBackgroundColor(getColor(R.color.white));
        binding.btSetting.setTextColor(getColor(R.color.gray));

        Drawable img = getResources().getDrawable(R.drawable.btn_home_setting_gray);
        binding.btSetting.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        binding.btConsult.setBackgroundColor(getColor(R.color.white));
        binding.btConsult.setTextColor(getColor(R.color.gray));

        Drawable img1 = getResources().getDrawable(R.drawable.btn_home_consult_gray);
        binding.btConsult.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

        binding.btCustomer.setBackgroundColor(getColor(R.color.white));
        binding.btCustomer.setTextColor(getColor(R.color.gray));

        Drawable img2 = getResources().getDrawable(R.drawable.btn_home_customer_gray);
        binding.btCustomer.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
    }


    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null) {
            //PDF 뷰어일때 뒤로가기 처리
            if (mCurrentFragment instanceof PDFViewerFragment) {
                showBottomMenu();
                removeFragment(mCurrentFragment);

                mCurrentFragment = ConsultDocFragment.newInstance(null);
            }
            //고객 이미지 리스트 뷰어일때 뒤로가기 처리
            else if (mCurrentFragment instanceof CustomerImageFragment) {
                removeFragment(mCurrentFragment);
                getViewModelStore().clear();
                showBottomMenu();
                mCurrentFragment = CustomerFragment.newInstance(null);

            }

            else if (mCurrentFragment instanceof CustomerImageMergeFragment) {
                removeFragment(mCurrentFragment);
                mCurrentFragment = CustomerImageFragment.newInstance(null);
            }

            else if (mCurrentFragment instanceof CustomerImageShareFragment) {
                removeFragment(mCurrentFragment);
                mCurrentFragment = CustomerImageFragment.newInstance(null);
            }

            else if (mCurrentFragment instanceof CustomerAgreeFragment) {
                removeFragment(mCurrentFragment);
                mCurrentFragment = CustomerImageFragment.newInstance(null);
            }

            else if (mCurrentFragment instanceof CustomerAgreementTemplateDialog) {
                removeFragment(mCurrentFragment);
                mCurrentFragment = CustomerAgreeFragment.newInstance(null);
            }

            else if (mCurrentFragment instanceof PDFSignFragment) {
                removeFragment(mCurrentFragment);
                mCurrentFragment = CustomerAgreementTemplateDialog.newInstance(null);
            }


            //홈이 아닐때는 모두다 홈으로 이동 시킴
            else if (mCurrentFragment instanceof HomeFragment == false) {
                hideBottomMenu();
                HomeFragment fragment = HomeFragment.newInstance(null);
                startFragment(fragment);
            }
            else {
                exitApp();
            }
        } else {
            exitApp();

        }
    }


}
