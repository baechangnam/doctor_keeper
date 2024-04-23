package com.apps.doctorkeeper_android.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apps.doctorkeeper_android.databinding.DialogIpSettingBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.util.CommonUtils;

public class IPSettingDialog extends DialogFragment {
    public static final String TAG_EVENT_DIALOG = "dialog_event";
    public IPSettingDialog(){}
    private DialogIpSettingBinding binding;
    RbPreference pref;

    public static IPSettingDialog getInstance(){
        IPSettingDialog letterReceiverDialog = new IPSettingDialog();
        return letterReceiverDialog;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogIpSettingBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        pref = new RbPreference(getActivity());

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

        return view;
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

        dismiss();

    }

}
