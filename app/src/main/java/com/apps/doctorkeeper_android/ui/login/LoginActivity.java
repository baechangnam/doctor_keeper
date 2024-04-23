package com.apps.doctorkeeper_android.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.apps.doctorkeeper_android.databinding.ActivityLoginBinding;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.ui.main.MainActivity;
import com.apps.doctorkeeper_android.util.CommonUtils;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    LoginViewModel loginViewModel;

    ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LoginViewModel(getApplication(),
                    baseActivity);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        init();
    }

    private void init() {
        binding.save.setOnClickListener(view -> {
            String userID = binding.etId.getText().toString().trim();
            String password = binding.etPass.getText().toString().trim();
            if (!userID.isEmpty() && !password.isEmpty()) {
                login();
            } else {
                CommonUtils.showSnackBar(binding.save, "아이디/비밀번호를 입력해주세요");
            }
        });

        binding.etPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeypad(baseActivity, binding.etPass);

                    String userID = binding.etId.getText().toString().trim();
                    String password = binding.etPass.getText().toString().trim();
                    if (!userID.isEmpty() && !password.isEmpty()) {
                        login();
                    } else {
                        CommonUtils.showSnackBar(binding.save, "아이디/비밀번호를 입력해주세요");
                    }
                    return true;
                }
                return false;
            }
        });

        binding.btSetting.setOnClickListener(view -> {
            IPSettingDialog ipSettingDialog = IPSettingDialog.getInstance();
            ipSettingDialog.show(getSupportFragmentManager(), IPSettingDialog.TAG_EVENT_DIALOG);
        });


        loginViewModel.getLoginResultLiveData().observe(this, loginResult -> {
            Log.d("myLog", "loginResult.getRESULT() " + loginResult.getRESULT());

            if (loginResult == null) {
                CommonUtils.showSnackBar(binding.save, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else if (loginResult.getRESULT().equals("true")) {

                if(pref.getValue(RbPreference.AUTO_LOGIN,false)){
                    pref.put(RbPreference.IS_LOGIN, true);
                }

                pref.put(RbPreference.MEM_ID, binding.etId.getText().toString().trim());

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                CommonUtils.showSnackBar(binding.save, "아이디/비밀번호가 맞지 않습니다");
            }
        });

    }

    private void login() {
        String userID = binding.etId.getText().toString().trim();
        String password = binding.etPass.getText().toString().trim();
        loginViewModel.login(userID, password);

    }


}