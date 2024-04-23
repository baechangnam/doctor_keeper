package com.apps.doctorkeeper_android.ui.customer;


import static com.apps.doctorkeeper_android.util.CommonUtils.getDateYYmmdd;
import static com.apps.doctorkeeper_android.util.ImageUtil.createImageFile;
import static com.apps.doctorkeeper_android.util.ImageUtil.takeScreenshot;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerXrayModel;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageBinding;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageMergeBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.login.IPSettingDialog;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.apps.doctorkeeper_android.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CustomerImageMergeFragment extends BaseFragment {
    FragmentCustomerImageMergeBinding binding;
    CustomerImageViewModel customerViewModel;
    View rootView;

    String baseImageUrl = "";
    String baseXrayUrl = "";


    public static CustomerImageMergeFragment newInstance(Bundle args) {
        CustomerImageMergeFragment fragment = new CustomerImageMergeFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CustomerImageViewModel(getActivity().getApplication(),
                    getActivity());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerImageMergeBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        baseImageUrl = pref.getValue(RbPreference.IMG_URL, CommonValue.IMAGE_URL);
        baseXrayUrl = pref.getValue(RbPreference.BASE_URL, CommonValue.BASE_URL);

        customerViewModel = new ViewModelProvider(requireActivity(), factory).get(CustomerImageViewModel.class);

        String oneImageUrl = getArguments().getString("oneImageUrl");
        String twoImageUrl = getArguments().getString("twoImageUrl");

        Log.d("myLog", "oneImageUrl " + oneImageUrl);
        Log.d("myLog", "twoImageUrl " + twoImageUrl);

        Glide.with(mActivity).load(oneImageUrl)
                .override(Target.SIZE_ORIGINAL).into(binding.ivImageLeft);

        Glide.with(mActivity).load(twoImageUrl)
                .override(Target.SIZE_ORIGINAL).into(binding.ivImageRight);

        initViewEvent();


        return rootView;
    }


    private void closeView() {
        mActivity.removeFragment(this);
    }


    void initViewEvent() {
        //창 닫기
        binding.btSlideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ImageUtil.mergeImage(binding.mergeView)==null){
                    Log.d("myLog" , "merge " + "null ");
                }else{
                    String path = ImageUtil.mergeImage(binding.mergeView);
                    customerViewModel.setMergeImage(path);
                    closeView();
                }

            }
        });

    }



}
