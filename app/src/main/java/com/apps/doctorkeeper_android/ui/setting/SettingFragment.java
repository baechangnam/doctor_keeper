package com.apps.doctorkeeper_android.ui.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.databinding.FragmentSettingBinding;

public class SettingFragment extends BaseFragment {

    FragmentSettingBinding binding;
    View rootView;

    public static SettingFragment newInstance(Bundle args) {
        SettingFragment fragment = new SettingFragment();
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
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        showFragment("home");
        setUi();

        return rootView;
    }


    private void setUi() {
        binding.btLoginSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btLoginSet.setBackgroundColor(Color.parseColor("#000000"));
                binding.btEtcSet.setBackgroundColor(Color.parseColor("#888888"));
                showFragment("login");
            }
        });

        binding.btEtcSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btLoginSet.setBackgroundColor(Color.parseColor("#888888"));
                binding.btEtcSet.setBackgroundColor(Color.parseColor("#000000"));
                showFragment("etc");
            }
        });

    }

    private void showFragment(String tab) {
        if ("home".equals(tab)) {
            SettingHomeFragment fragment = SettingHomeFragment.newInstance(null);
            replaceFragment(fragment,R.id.view_setting);
        } else if ("login".equals(tab)) {
            SettingLoginFragment fragment = SettingLoginFragment.newInstance(null);
            replaceFragment(fragment,R.id.view_setting);
        } else if ("etc".equals(tab)) {
            SettingEtcFragment fragment = SettingEtcFragment.newInstance(null);
            replaceFragment(fragment,R.id.view_setting);
        }


    }


}
