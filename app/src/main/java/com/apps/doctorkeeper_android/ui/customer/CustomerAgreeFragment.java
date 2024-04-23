package com.apps.doctorkeeper_android.ui.customer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerAgreeBinding;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageShareBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.consult.PDFViewerFragment;
import com.apps.doctorkeeper_android.ui.customer.adapter.AgreementDocAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.AgreementListAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerImageShareAdapter;
import com.apps.doctorkeeper_android.ui.login.IPSettingDialog;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerAgreeFragment extends BaseFragment implements  ItemClick {
    FragmentCustomerAgreeBinding binding;
    CustomerImageViewModel customerViewModel;
    View rootView;
    String baseImageUrl = "";
    AgreementListAdapter mAdapter;

    String custNo = "";
    ArrayList<CustomerModel> csModel = new ArrayList<>();
    ArrayList<CustomerImageModel> csImageList = new ArrayList<>();

    public static CustomerAgreeFragment newInstance(Bundle args) {
        CustomerAgreeFragment fragment = new CustomerAgreeFragment();
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
        binding = FragmentCustomerAgreeBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        mActivity.hideBottomMenu();


        baseImageUrl = pref.getValue(RbPreference.IMG_URL, CommonValue.IMAGE_URL);
        custNo = getArguments().getString("custNo");

        customerViewModel = new ViewModelProvider(requireActivity(), factory).get(CustomerImageViewModel.class);


        initViewEvent();
        initViewModelObserving();

        customerViewModel.getAgreeList(custNo);
        isRecord = customerViewModel.isViewModelRecord();

        if(isRecord){
            binding.btBlink.setVisibility(View.VISIBLE);
            binding.btBlink.startBlinkAnimation();
        }else{
            binding.btBlink.setVisibility(View.GONE);
        }

        Log.d("myLog", "isRecord  CustomerAgreeFragment " + customerViewModel.isViewModelRecord());

        return rootView;
    }

    private void initViewModelObserving() {
        customerViewModel.getAgreeList().observe(mActivity, response -> {

            if (response == null) {
                CommonUtils.showSnackBar(binding.rvImagelist, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {

                csImageList.clear();
               // csModel.addAll(response.getRESULT1());
                csImageList.addAll(response.getRESULT());

                setImage();

            }
        });

        customerViewModel.getIsRecording().observe(mActivity, response -> {

            if (response == null) {

            } else {

            }
        });



    }


    private void closeView() {
        mActivity.removeFragment(this);
    }

    private void setImage() {
        binding.rvImagelist.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 6, GridLayoutManager.VERTICAL, false);

        binding.rvImagelist.setLayoutManager(gridLayoutManager);
        mAdapter = new AgreementListAdapter(mActivity,
                csImageList, this::itemClick);// 리스트
        mAdapter.setHasStableIds(true);
        binding.rvImagelist.setAdapter(mAdapter);

        if(csImageList.size()>0){
            binding.noData.setVisibility(View.GONE);
        }else{
            binding.noData.setVisibility(View.VISIBLE);
        }

    }

    void initViewEvent() {
        //창 닫기
        binding.btSlideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        binding.btRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mActivity.showPopup(binding.btRecord,custNo);
                showPopup(binding.btRecord);
            }
        });

        binding.btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csImageList.clear();
                customerViewModel.getAgreeList(custNo);
            }
        });

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("custNo", custNo);
                CustomerAgreementTemplateDialog customerAgreementTemplateDialog = CustomerAgreementTemplateDialog.newInstance(bundle);

                mActivity.addFragment(customerAgreementTemplateDialog);

            }
        });

    }


//    @Override
//    public void OnCloseDialog() {
//
//    }

    @Override
    public void itemClick(String code, String idx) {
        Bundle bundle = new Bundle();

        bundle.putString("url", idx);

        Log.d("myLog" ,"idx " + idx);
        PDFViewerFragment pdfViewerFragment = PDFViewerFragment.newInstance(bundle);
        mActivity.hideBottomMenu();
        mActivity.addFragment(pdfViewerFragment);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("myLog" ,"onDetach " + isRecord);

        customerViewModel.setIsRecording(isRecord);
    }


    private MediaRecorder mediaRecorder;
    boolean isRecord = false;
    boolean isPlay = false;

    String outputPath = null;
    private MediaPlayer mediaPlayer;

    Button play;
    PopupWindow popupWindow;

    public void showPopup(View anchorView) {
        mediaRecorder = customerViewModel.getMediaPlayer();
        isRecord = customerViewModel.isViewModelRecord();

        Log.d("myLog", "isRecord  CustomerImageFragment " + customerViewModel.isViewModelRecord());

        // Create a View object for the popup
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.view_record, null);

        // Set up the PopupWindow
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set background color and other properties
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);

        // Show the popup at the specified location on the screen
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0] - 20, 131);

        Button record = popupView.findViewById(R.id.record);

        if (isRecord) {
            record.setBackgroundResource(R.drawable.stops);
            startBlinkAnimation(record);
            outputPath = customerViewModel.getRecordingFilePath();
        }

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = !isRecord;
                if (isRecord) {
                    record.setBackgroundResource(R.drawable.stops);
                    startRecording(record);
                } else {
                    record.setBackgroundResource(R.drawable.record);
                    stopRecording(record);
                }

            }
        });

        play = popupView.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord) {
                    CommonUtils.showToast("녹음 중 입니다.", mActivity);
                } else {
                    isPlay = !isPlay;
                    if (isPlay) {
                        play.setBackgroundResource(R.drawable.stops);
                        if (outputPath == null) {
                            isPlay = false;
                            CommonUtils.showToast("녹음 파일이 없습니다.", mActivity);
                        } else {
                            onPlayButtonClicked();
                            startBlinkAnimation(play);
                        }

                    } else {
                        play.setBackgroundResource(R.drawable.plays);
                        stopPlay();
                        stopAnimation(play);
                    }
                }


            }
        });


        Button upload = popupView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord) {
                    CommonUtils.showToast("녹음 중 입니다.", mActivity);
                } else if (isPlay) {
                    CommonUtils.showToast("녹음 파일 재생 중 입니다.", mActivity);
                } else {
                    if (outputPath != null) {
                        File file = new File(outputPath);
                        regRecord(file, custNo);
                    } else {
                        CommonUtils.showToast("녹음 파일이 없습니다.", mActivity);
                    }
                }
            }
        });

        // Close the popup when clicked outside
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
                if (isRecord) {
                    binding.btBlink.setVisibility(View.VISIBLE);
                    binding.btBlink.startBlinkAnimation();
                } else {
                    binding.btBlink.setVisibility(View.GONE);
                }
            }
        });

    }

    private void startRecording(Button record) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String tempFile = "/doctorkeeper_record_" + timeStamp + ".mp3";

        File filePath = new File(getActivity().getFilesDir()+"/record");
        if(!filePath.exists()){
            filePath.mkdir();
        }

        outputPath = filePath + tempFile;

        //  mediaRecorder = new MediaRecorder();
        mediaRecorder = customerViewModel.getMediaPlayer();

        customerViewModel.setViewModelRecord(true);
        customerViewModel.setRecordingFilePath(outputPath);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            startBlinkAnimation(record);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.btBlink.setVisibility(View.VISIBLE);
        binding.btBlink.startBlinkAnimation();

    }

    private void stopRecording(Button button) {
        customerViewModel.setViewModelRecord(false);
       // customerViewModel.stopRecording();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        binding.btBlink.stopBlinkAnimation();
        binding.btBlink.setVisibility(View.GONE);


        customerViewModel.resetMediaRecord();
        stopAnimation(button);

        if (outputPath != null) {
            File file = new File(outputPath);
            regRecord(file, custNo);
        }
    }

    public void onPlayButtonClicked() {
        if (outputPath != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    stopAnimation(play);
                    play.setBackgroundResource(R.drawable.plays);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlay() {
        try {
            mediaPlayer.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startBlinkAnimation(View view) {
        // Create a new AlphaAnimation
        Animation blinkAnimation = new AlphaAnimation(1, 0); // 1 means fully visible, 0 means fully transparent
        blinkAnimation.setDuration(500); // Set the duration of each animation cycle in milliseconds
        blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when it reaches the end
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Infinite looping

        // Set the AnimationListener to handle animation events
        blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });

        // Start the animation on the TextView
        view.startAnimation(blinkAnimation);
    }

    private void stopAnimation(View view) {
        view.clearAnimation();
    }

    public void regRecord(File mPhotoFile,String custNo){
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("audio/mp3"),
                        mPhotoFile
                );

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", mPhotoFile.getName(), requestFile);

        RequestBody custNos = RequestBody.create(MediaType.parse("text/plain"), custNo);

        AppApi appApi = RetrofitAdapter.getClient(mActivity).create(AppApi.class);
        mActivity.progressON(mActivity);
        appApi.regRecordFile(body,custNos)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        mActivity.progressOFF();
                        popupWindow.dismiss();
                        CommonUpdateResponse commonResponse = response.body();
                        if(commonResponse.getSUCCESS().equals("1")){
                            CommonUtils.showAlert("녹음 파일 업로드 완료 되었습니다.", mActivity);
                        }else{

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(),mActivity);
                        Log.d("myLog", "t.getMessage() " +t.getMessage());
                        mActivity.progressOFF();
                    }
                });

    }
}
