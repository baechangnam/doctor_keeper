package com.apps.doctorkeeper_android.ui.customer;

import static com.apps.doctorkeeper_android.constants.CommonValue.DIAGNOSIS_LIST;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerUpdateResponse;

import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.data.customer.model.MedicalModel;
import com.apps.doctorkeeper_android.databinding.DialogCustomerUpdateBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerUpdateDialog extends DialogFragment {
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    private DialogCustomerUpdateBinding binding;
    String gender = "1";

    String medical = "";
    String custNo="";
    boolean[] checkItems = {}; //진료분류 체크값
    String[] cdNames ={}; //진료분류 스트링값
    List<String> names = new ArrayList(); //진료분류 출력용
    List<String> codes = new ArrayList(); //진료분류 코드

    List<String> oldList= new ArrayList<>(); //수정전 진료분류 리스트
    public static CustomerUpdateDialog getInstance() {
        CustomerUpdateDialog letterReceiverDialog = new CustomerUpdateDialog();
        return letterReceiverDialog;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCustomerUpdateBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        initView();

        return view;
    }



    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mon = monthOfYear + 1;
            int days = dayOfMonth;

            String str1 = "";
            if (mon < 10) {
                str1 = "0" + mon;
            } else {
                str1 = Integer.toString(mon);
            }

            String str2 = "";
            if (days < 10) {
                str2 = "0" + days;
            } else {
                str2 = Integer.toString(days);
            }
            String dateStr = year + "-" + str1 + "-" + str2;

            binding.tvBirth.setText(dateStr);

        }
    };



    private void initView() {
        custNo = getArguments().getString("custNo");
        getInfo(custNo,getArguments().getString("name"),getArguments().getString("custInfo"));
        getCommonCode("039");


        binding.btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.tvMan.setOnClickListener(view15 -> {
            binding.btMan.setBackgroundResource(R.drawable.radio_select);
            binding.btWoman.setBackgroundResource(R.drawable.radio_normal);
            gender = "1";
        });

        binding.btMan.setOnClickListener(view14 -> {
            binding.btMan.setBackgroundResource(R.drawable.radio_select);
            binding.btWoman.setBackgroundResource(R.drawable.radio_normal);
            gender = "1";
        });

        binding.tvWoman.setOnClickListener(view13 -> {
            binding.btMan.setBackgroundResource(R.drawable.radio_normal);
            binding.btWoman.setBackgroundResource(R.drawable.radio_select);
            gender = "2";
        });

        binding.btWoman.setOnClickListener(view1 -> {
            binding.btMan.setBackgroundResource(R.drawable.radio_normal);
            binding.btWoman.setBackgroundResource(R.drawable.radio_select);
            gender = "2";
        });

        binding.tvBirth.setOnClickListener(view12 -> {
            String strCurYear = "1980";
            String strCurMon = "07";
            String strCurday = "01";

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, Integer.parseInt(strCurYear),
                    Integer.parseInt(strCurMon) - 1, Integer.parseInt(strCurday));

            dialog.show();
        });

        binding.tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiagnosisType(binding.tvType);
            }

        });

        binding.btType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiagnosisType(binding.tvType);
            }
        });

        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etChartNmu.getText().toString().isEmpty()) {
                    CommonUtils.showAlert("차트번호를 입력하세요.", getActivity());
                } else if (binding.etName.getText().toString().isEmpty()) {
                    CommonUtils.showAlert("이름을 입력하세요.", getActivity());
                }
//                else if (binding.etEmail.getText().toString().isEmpty()) {
//                    CommonUtils.showAlert("이메일을 입력하세요.", getActivity());
//                }
                else if (binding.tvBirth.getText().toString().isEmpty()) {
                    CommonUtils.showAlert("생년월일을 입력하세요.", getActivity());
                } else if (binding.tvType.getText().toString().isEmpty()) {
                    CommonUtils.showAlert("진료분류를 입력하세요.", getActivity());
                } else {
                    updateCustomer(binding.etChartNmu.getText().toString(), binding.etName.getText().toString(), gender, binding.tvBirth.getText().toString(),
                            medical, binding.etEmail.getText().toString());
                }
            }
        });

    }

    public void updateCustomer(String chartNo, String custNm, String sexCd, String brthYmd, String medical, String emailAddr) {
        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
        appApi.updateCustomer(custNo,chartNo, custNm, sexCd, brthYmd, names, emailAddr)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        CommonUpdateResponse commonResponse = response.body();
                        if (commonResponse.getSUCCESS().equals("1")) {
                            CommonUtils.showToast("고객 정보 수정 완료 되었습니다.", getActivity());
                            dismiss();
                        } else {
                            CommonUtils.showAlert("고객 정보 수정 실패 입니다. 다시 시도해 주세요.", getActivity());
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), getActivity());
                        Log.d("myLog", "t.getMessage() " +t.getMessage());

                    }
                });

    }

    public void getInfo(String custNo, String custNm , String custInfo) {
        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
        appApi.getPatientInfo(custNo,custNm,custInfo)
                .enqueue(new Callback<CustomerUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CustomerUpdateResponse> call, Response<CustomerUpdateResponse> response) {
                        CustomerUpdateResponse commonResponse = response.body();

                        if(commonResponse.getRESULT().size()>0){
                            CustomerModel cs = commonResponse.getRESULT().get(0);

                            String str="";
                            String codes="";
                            ArrayList<MedicalModel> medicalModels = new ArrayList<>();
                            if(commonResponse.getMEDICAL().size()>0){
                                medicalModels.addAll(commonResponse.getMEDICAL());

                                for(MedicalModel mds : medicalModels){
                                    if(mds.getMedical().equals("Y")){
                                        str=str+ mds.getCdNm()+" ";
                                        codes=codes+ mds.getCdId()+" ";
                                        oldList.add(mds.getCdId());
                                    }

                                }

                            }

                            setData(cs,str,codes);
                        }


                    }

                    @Override
                    public void onFailure(Call<CustomerUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), getActivity());

                    }
                });

    }


    private void setData(CustomerModel cs, String str, String code){

        for(int i=0 ; i < checkItems.length; i++){
            if(code.contains(codes.get(i))){
                checkItems[i] =true;
            }
        }

        binding.etChartNmu.setText(cs.getChrtNo());
        binding.etName.setText(cs.getCustNm());
        binding.etEmail.setText(cs.getEmailAddr());
        binding.tvBirth.setText(cs.getBrthYmd());
        gender = cs.getSexCd();

        names.addAll(oldList);

        if (gender.equals("1")) {
            binding.btMan.setBackgroundResource(R.drawable.radio_select);
            binding.btWoman.setBackgroundResource(R.drawable.radio_normal);
        } else {
            binding.btMan.setBackgroundResource(R.drawable.radio_normal);
            binding.btWoman.setBackgroundResource(R.drawable.radio_select);
        }

        binding.tvType.setText(str);

    }


    private void showDiagnosisType(TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("진료 분류 선택");
        builder.setMultiChoiceItems(cdNames, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos, boolean b) {
                if (b) {
                    checkItems[pos] = true;
                } else {
                    checkItems[pos] = false;
                }

            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String medicals = "";
                String dStr = "";
                names.clear();

                for (int i = 0; i < checkItems.length; i++) {
                    if (checkItems[i]) {
                        dStr += cdNames[i] + " ";
                        medicals = codes.get(i);

                        names.add(medicals);
                    }
                }
                textView.setText(dStr);

            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void getCommonCode(String cdGrpId) {
        AppApi appApi = RetrofitAdapter.getClient(requireActivity()).create(AppApi.class);
        appApi.getCommonCode(cdGrpId)
                .enqueue(new Callback<CommonCodeResponse>() {
                    @Override
                    public void onResponse(Call<CommonCodeResponse> call, Response<CommonCodeResponse> response) {
                        ArrayList<CommonCode> commonCodes = response.body().getRESULT();

                        checkItems = new boolean[commonCodes.size()];
                        cdNames = new String[commonCodes.size()];
                        int i=0;
                        for(CommonCode cCode : commonCodes){
                            checkItems[i] = false;
                            cdNames[i] = cCode.getCdNm();
                            codes.add(cCode.getCdId());
                            i++;
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonCodeResponse> call, Throwable t) {


                    }
                });

    }


}
