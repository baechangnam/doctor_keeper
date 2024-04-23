package com.apps.doctorkeeper_android.ui.customer;

import android.app.Activity;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.customer.CommonResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerAgreementTemplateResponse;
import com.apps.doctorkeeper_android.data.customer.model.AgreementTemplateModel;
import com.apps.doctorkeeper_android.databinding.DialogAgreementTemplateBinding;
import com.apps.doctorkeeper_android.databinding.DialogCustomerRegBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.consult.PDFViewerFragment;
import com.apps.doctorkeeper_android.ui.consult.adapter.ConsultDocAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.AgreementDocAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAgreementTemplateDialog extends BaseFragment implements ItemClick {
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    private DialogAgreementTemplateBinding binding;

    static MyDialogListener mListener;
    ArrayList<AgreementTemplateModel> agreementTemplateModels = new ArrayList<>();

    AgreementDocAdapter cAdapter;
    String custNo="";

    public static CustomerAgreementTemplateDialog newInstance(Bundle args) {
        CustomerAgreementTemplateDialog fragment = new CustomerAgreementTemplateDialog();
        if (args != null) {
            fragment.setArguments(args);

        }
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAgreementTemplateBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        custNo = getArguments().getString("custNo");
        mActivity.hideBottomMenu();

        getAgreementTemplate("99");
        initView();

        return view;
    }

    private void initView(){
        binding.btBack.setOnClickListener(view1 -> closeView());

    }

    private void closeView() {
        mActivity.removeFragment(this);
    }

    private void setListDoc(){
        binding.rvTemplate.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5, GridLayoutManager.VERTICAL, false);
        binding.rvTemplate.setLayoutManager(gridLayoutManager);
        cAdapter = new AgreementDocAdapter(getActivity(),
                agreementTemplateModels,this);// 리스트
        cAdapter.setHasStableIds(true);
        binding.rvTemplate.setAdapter(cAdapter);
    }


    public void getAgreementTemplate(String cnsltClssCd) {

        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
        appApi.getAgreementTemplate(cnsltClssCd)
                .enqueue(new Callback<CustomerAgreementTemplateResponse>() {
                    @Override
                    public void onResponse(Call<CustomerAgreementTemplateResponse> call, Response<CustomerAgreementTemplateResponse> response) {
                        agreementTemplateModels.addAll(response.body().getRESULT());
                        setListDoc();
                    }

                    @Override
                    public void onFailure(Call<CustomerAgreementTemplateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(),getActivity());

                    }
                });

    }


    @Override
    public void itemClick(String code, String idx) {
        Bundle bundle = new Bundle();
        bundle.putString("url", idx);
        bundle.putString("custNo", custNo);
        bundle.putString("fileName", code);

        PDFSignFragment pdfViewerFragment = PDFSignFragment.newInstance(bundle);

        mActivity.addFragment(pdfViewerFragment);
    }
}
