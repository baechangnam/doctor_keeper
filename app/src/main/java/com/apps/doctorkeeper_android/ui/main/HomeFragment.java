package com.apps.doctorkeeper_android.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.databinding.FragmentHomeBinding;
import com.apps.doctorkeeper_android.ui.consult.ConsultDocFragment;
import com.apps.doctorkeeper_android.ui.customer.CustomerFragment;
import com.apps.doctorkeeper_android.ui.setting.SettingFragment;

public class HomeFragment extends BaseFragment {

    FragmentHomeBinding binding;
    View rootView;

    public static HomeFragment newInstance(Bundle args) {
        HomeFragment fragment = new HomeFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        setUi();

        return rootView;
    }

    private void setUi(){
        binding.btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showBottomMenu("setting");
                SettingFragment settingFragment = SettingFragment.newInstance(null);
                mActivity.startFragment(settingFragment);

            }
        });

        binding.btConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showBottomMenu("consult");
                ConsultDocFragment consultDocFragment = ConsultDocFragment.newInstance(null);
                mActivity.startFragment(consultDocFragment);

            }
        });

        binding.btCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showBottomMenu("customer");
                CustomerFragment customerFragment = CustomerFragment.newInstance(null);
                mActivity.startFragment(customerFragment);

            }
        });
    }
}
