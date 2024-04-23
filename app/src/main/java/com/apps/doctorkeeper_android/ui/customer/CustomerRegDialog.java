package com.apps.doctorkeeper_android.ui.customer;

import static com.apps.doctorkeeper_android.constants.CommonValue.DEFAULT_URL;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.customer.CommonResponse;
import com.apps.doctorkeeper_android.databinding.DialogCustomerRegBinding;

import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRegDialog extends DialogFragment {
    public static final String TAG_EVENT_DIALOG = "dialog_event";


    private DialogCustomerRegBinding binding;
    boolean isChart = false;
    String gender = "1";

    static MyDialogListener mListener;

    boolean[] checkItems = {}; //진료분류 체크값
    String[] cdNames ={}; //진료분류 스트링값
    List<String> names = new ArrayList(); //진료분류 출력용
    List<String> codes = new ArrayList(); //진료분류 코드

    public static CustomerRegDialog getInstance(MyDialogListener listener) {
        CustomerRegDialog letterReceiverDialog = new CustomerRegDialog();
        mListener = listener;
        return letterReceiverDialog;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCustomerRegBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        getCommonCode("039");
        initView();

        return view;
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

        binding.btChart.setOnClickListener(view16 -> {
            if (binding.etChartNmu.getText().toString().isEmpty()) {
                CommonUtils.showAlert("차트번호를 입력하세요.", getActivity());
            } else {
                getChartNo(binding.etChartNmu.getText().toString());
            }
        });

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
                // changeDisplayLegend();
                showDiagnosisType(binding.tvType);
            }

        });

        binding.btType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // changeDisplayLegend();
                showDiagnosisType(binding.tvType);
            }
        });

        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etChartNmu.getText().toString().isEmpty()) {
                    CommonUtils.showAlert("차트번호를 입력하세요.", getActivity());
                } else if (!isChart) {
                    CommonUtils.showAlert("차트번호 중복체크를 해주세요.", getActivity());
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
                    regCustomer(binding.etChartNmu.getText().toString(), binding.etName.getText().toString(), gender, binding.tvBirth.getText().toString(),
                            "", binding.etEmail.getText().toString());
                }
            }
        });

    }

    public void regCustomer(String chartNo, String custNm, String sexCd, String brthYmd, String medical, String emailAddr) {
        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
        appApi.regCustomer(chartNo, custNm, sexCd, brthYmd, names, emailAddr)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        CommonResponse commonResponse = response.body();
                        if (commonResponse.getSUCCESS().equals("1")) {
                            CommonUtils.showToast("고객 등록이 완료 되었습니다.", getActivity());
                            mListener.OnCloseDialog();
                            dismiss();
                        } else {
                            CommonUtils.showAlert("고객 등록 실패 입니다. 다시 시도해 주세요.", getActivity());
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), getActivity());

                    }
                });

    }

    public void getChartNo(String chartNo) {
        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
        appApi.checkChartNo(chartNo)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        CommonResponse commonResponse = response.body();
                        if (!commonResponse.getRESULT().equals("")) {
                            CommonUtils.showAlert("중복된 차트 번호 입니다.", getActivity());
                            binding.etChartNmu.requestFocus();
                        } else {
                            CommonUtils.showSnackBar(binding.etChartNmu, "사용 가능한 차트 번호 입니다.");
                            binding.etName.requestFocus();
                            isChart = true;
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), getActivity());

                    }
                });

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
