package com.apps.doctorkeeper_android.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.databinding.FragmentSettingHomeBinding;
import com.apps.doctorkeeper_android.databinding.FragmentSettingLoginBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.util.CommonUtils;

public class SettingLoginFragment extends BaseFragment {

    FragmentSettingLoginBinding binding;
    View rootView;
    boolean isAutoLogin=false;

    public static SettingLoginFragment newInstance(Bundle args) {
        SettingLoginFragment fragment = new SettingLoginFragment();
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
        binding = FragmentSettingLoginBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        binding.etId.setText(login_id);

        isAutoLogin = pref.getValue(RbPreference.AUTO_LOGIN,false);

        if(pref.getValue(RbPreference.AUTO_LOGIN,false)){
            binding.setLogin.setBackgroundResource(R.drawable.push_on);
        }else{
            binding.setLogin.setBackgroundResource(R.drawable.push_off);
        }

        binding.setLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutoLogin=!isAutoLogin;

                if(isAutoLogin){
                    CommonUtils.showAlert("자동로그인 On 되었습니다." ,mActivity);
                    binding.setLogin.setBackgroundResource(R.drawable.push_on);
                }else{
                    CommonUtils.showAlert("자동로그인 Off 되었습니다." ,mActivity);
                    binding.setLogin.setBackgroundResource(R.drawable.push_off);
                }

                pref.put(RbPreference.AUTO_LOGIN,isAutoLogin);
            }
        });


        return rootView;
    }



}
