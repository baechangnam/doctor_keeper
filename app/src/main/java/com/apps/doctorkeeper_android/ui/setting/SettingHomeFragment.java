package com.apps.doctorkeeper_android.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.databinding.FragmentSettingBinding;
import com.apps.doctorkeeper_android.databinding.FragmentSettingHomeBinding;

public class SettingHomeFragment extends BaseFragment {

    FragmentSettingHomeBinding binding;
    View rootView;

    public static SettingHomeFragment newInstance(Bundle args) {
        SettingHomeFragment fragment = new SettingHomeFragment();
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
        binding = FragmentSettingHomeBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();


        return rootView;
    }


}
