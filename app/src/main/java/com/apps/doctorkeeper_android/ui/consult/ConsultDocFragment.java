package com.apps.doctorkeeper_android.ui.consult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.data.consult.DocumentModel;
import com.apps.doctorkeeper_android.databinding.FragmentConsultBinding;
import com.apps.doctorkeeper_android.ui.consult.adapter.CommonCodeAdapter;
import com.apps.doctorkeeper_android.ui.consult.adapter.ConsultDocAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.util.ArrayList;

public class ConsultDocFragment extends BaseFragment implements ItemClick{

    FragmentConsultBinding binding;
    View rootView;
    ConsultDocViewModel consultDocViewModel;
    CommonCodeAdapter mAdapter;
    ConsultDocAdapter cAdapter;

    ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ConsultDocViewModel(getActivity().getApplication(),
                    getActivity());
        }
    };

    ArrayList<CommonCode> commonCodes = new ArrayList<>();
    ArrayList<DocumentModel> documentModels = new ArrayList<>();

    public static ConsultDocFragment newInstance(Bundle args) {
        ConsultDocFragment fragment = new ConsultDocFragment();
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
        binding = FragmentConsultBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        consultDocViewModel = new ViewModelProvider(this, factory).get(ConsultDocViewModel.class);
        setListCommon();
        setListDoc();
        initViewModelObserving();

        consultDocViewModel.getCommonCode("039");
        return rootView;
    }

    private void setListCommon(){
        binding.rvCommon.setNestedScrollingEnabled(true);

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        binding.rvCommon.setLayoutManager(mLayoutManager);
        mAdapter = new CommonCodeAdapter(mActivity,
                commonCodes,this);// 리스트
        mAdapter.setHasStableIds(true);
        binding.rvCommon.setAdapter(mAdapter);
    }

    private void setListDoc(){
        binding.rvList.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 6, GridLayoutManager.VERTICAL, false);
        binding.rvList.setLayoutManager(gridLayoutManager);
        cAdapter = new ConsultDocAdapter(mActivity,
                documentModels,this);// 리스트
        cAdapter.setHasStableIds(true);
        binding.rvList.setAdapter(cAdapter);
    }


    private void initViewModelObserving() {
        consultDocViewModel.getConsultLiveData().observe(mActivity, response -> {
            if (response == null) {
                CommonUtils.showSnackBar(binding.viewHome, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                documentModels.addAll(response.getRESULT());
                if(documentModels.size()==0){
                    binding.noData.setVisibility(View.VISIBLE);
                }else{
                    binding.noData.setVisibility(View.GONE);
                }
                cAdapter.notifyDataSetChanged();
            }
        });


        consultDocViewModel.getCommcodeResultLiveData().observe(mActivity, getResponse -> {
            if (getResponse == null) {
                CommonUtils.showSnackBar(binding.viewHome, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            }
            else {
                commonCodes.addAll(getResponse.getRESULT());
                setCommonList();

            }
        });

    }

    private void setCommonList(){
        mAdapter.notifyDataSetChanged();
    }

    private void setDocList(){
        cAdapter.notifyDataSetChanged();
    }


    @Override
    public void itemClick(String code, String idx) {
        if(code.equals("cateClick")){
            binding.rvList.setVisibility(View.VISIBLE);
            binding.viewHome.setVisibility(View.GONE);
            documentModels.clear();

            consultDocViewModel.getDocList(idx);
           // getDocList(idx);
        }else{
            Bundle bundle = new Bundle();
            bundle.putString("url", idx);
            PDFViewerFragment pdfViewerFragment = PDFViewerFragment.newInstance(bundle);
            mActivity.hideBottomMenu();
            mActivity.addFragment(pdfViewerFragment);
        }
    }




}
