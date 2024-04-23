package com.apps.doctorkeeper_android.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.databinding.FragmentSettingEtcBinding;
import com.apps.doctorkeeper_android.databinding.FragmentSettingLoginBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.util.CommonUtils;

public class SettingEtcFragment extends BaseFragment {

    FragmentSettingEtcBinding binding;
    View rootView;
    boolean isXray=false;

    public static SettingEtcFragment newInstance(Bundle args) {
        SettingEtcFragment fragment = new SettingEtcFragment();
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
        binding = FragmentSettingEtcBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        binding.etIp01.setText(pref.getValue(RbPreference.IP_01,"14"));
        binding.etIp02.setText(pref.getValue(RbPreference.IP_02,"40"));
        binding.etIp03.setText(pref.getValue(RbPreference.IP_03,"30"));
        binding.etIp04.setText(pref.getValue(RbPreference.IP_04,"73"));

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etIp01 = binding.etIp01.getText().toString().trim();
                String etIp02 = binding.etIp02.getText().toString().trim();
                String etIp03 = binding.etIp03.getText().toString().trim();
                String etIp04 = binding.etIp04.getText().toString().trim();
                if (!etIp01.isEmpty() && !etIp02.isEmpty()  && !etIp03.isEmpty() && !etIp04.isEmpty()) {
                    saveIP();
                }else{
                    CommonUtils.showSnackBar(binding.etIp01 , "IP값을 입력해주세요.");
                }
            }
        });

        isXray = pref.getValue(RbPreference.IS_X_RAY,false);

        if(pref.getValue(RbPreference.IS_X_RAY,false)){
            binding.setLogin.setBackgroundResource(R.drawable.push_on);
        }else{
            binding.setLogin.setBackgroundResource(R.drawable.push_off);
        }

        binding.setLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isXray=!isXray;

                if(isXray){
                    binding.setLogin.setBackgroundResource(R.drawable.push_on);
                    CommonUtils.showAlert("X-RAY 사용 On 되었습니다." ,mActivity);
                }else{
                    CommonUtils.showAlert("X-RAY 사용 Off 되었습니다." ,mActivity);
                    binding.setLogin.setBackgroundResource(R.drawable.push_off);
                }

                pref.put(RbPreference.IS_X_RAY,isXray);
            }
        });

        return rootView;
    }

    private void saveIP(){
        pref.put(RbPreference.IP_01 ,  binding.etIp01.getText().toString().trim());
        pref.put(RbPreference.IP_02 ,  binding.etIp02.getText().toString().trim());
        pref.put(RbPreference.IP_03 ,  binding.etIp03.getText().toString().trim());
        pref.put(RbPreference.IP_04 ,  binding.etIp04.getText().toString().trim());

        pref.put(RbPreference.API_URL ,  "http://" + binding.etIp01.getText().toString().trim()+"." + binding.etIp02.getText().toString().trim()+"."
                + binding.etIp03.getText().toString().trim()+"."+ binding.etIp04.getText().toString().trim()+":8080/dplus/");

        pref.put(RbPreference.IMG_URL ,  "http://" + binding.etIp01.getText().toString().trim()+"." + binding.etIp02.getText().toString().trim()+"."
                + binding.etIp03.getText().toString().trim()+"."+ binding.etIp04.getText().toString().trim()+":8080/dslr");

        pref.put(RbPreference.BASE_URL ,  "http://" + binding.etIp01.getText().toString().trim()+"." + binding.etIp02.getText().toString().trim()+"."
                + binding.etIp03.getText().toString().trim()+"."+ binding.etIp04.getText().toString().trim()+":8080");
        CommonUtils.showAlert("아이피가 저장되었습니다." ,mActivity);
    }


}
