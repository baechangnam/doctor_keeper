package com.apps.doctorkeeper_android.ui.customer;

import android.app.Activity;
import android.app.Application;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerAgreeResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerImageResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerRepository;
import com.apps.doctorkeeper_android.data.customer.CustomerXrayResponse;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerImageViewModel extends AndroidViewModel {
    private CustomerRepository repository;
    private MutableLiveData<CustomerImageResponse> _customerImageListData = new MutableLiveData<>();

    private MutableLiveData<CustomerAgreeResponse> _customerAgreeListData = new MutableLiveData<>();
    private MutableLiveData<CustomerXrayResponse> customerXrayResponseMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mergeImage = new MutableLiveData<>();
    private MutableLiveData<String> editImage = new MutableLiveData<>();

    private MutableLiveData<Boolean> imageDelete = new MutableLiveData<>();
    private MutableLiveData<Boolean> imageUpload = new MutableLiveData<>();

    BaseActivity baseActivity;

    private MutableLiveData<Boolean> isRecording = new MutableLiveData<>();
    private MediaRecorder mediaPlayer;
    private boolean isViewModelRecord =false;


    private String recordingFilePath ="";

    public boolean isViewModelRecord() {
        return isViewModelRecord;
    }

    public void setViewModelRecord(boolean viewModelRecord) {
        isViewModelRecord = viewModelRecord;
    }


    public String getRecordingFilePath() {
        return recordingFilePath;
    }

    public void setRecordingFilePath(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath;
    }


    public MediaRecorder getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaRecorder();
        }
        return mediaPlayer;
    }

    public void  resetMediaRecord() {
        mediaPlayer = null;
    }

    public void stopRecording() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


    public CustomerImageViewModel(@NonNull Application application, Activity activity) {
        super(application);
        baseActivity = (BaseActivity)activity;
        repository = new CustomerRepository(baseActivity);
    }

    public void getPatientImageList(String chrtNo) {
        repository.getPatientImageList(chrtNo);
    }

    public void getXrayList(String chrtNo) {
        repository.getXrayList(chrtNo);
    }

    public MutableLiveData<CustomerImageResponse> getCustomerImageData() {
        _customerImageListData = repository.getCustomerImage();

        return _customerImageListData;
    }


    public void getAgreeList(String custNo) {
        repository.getAgreeList(custNo);
    }

    public MutableLiveData<CustomerAgreeResponse> getAgreeList() {
        _customerAgreeListData = repository.getCustomerAgreeList();

        return _customerAgreeListData;
    }

    public MutableLiveData<CustomerXrayResponse> getXrayListData() {
        customerXrayResponseMutableLiveData = repository.getXrayResponseMutableLiveData();

        return customerXrayResponseMutableLiveData;
    }



    public MutableLiveData<String> getMergeData() {
        return mergeImage;
    }

    public MutableLiveData<String> getEditImage() {
        return editImage;
    }

    public void setEditImage(String image){
        editImage.setValue(image);
    }

    public MutableLiveData<Boolean> getImageDelete() {
        return imageDelete;
    }

    public MutableLiveData<Boolean> getIsRecording() {
        return isRecording;
    }
    public void setIsRecording(Boolean isRecordings){
        isRecording.setValue(isRecordings);
    }

    public MutableLiveData<Boolean> setImageupload() {
        return imageUpload;
    }



    public void setMergeImage(String merageImage){
        mergeImage.setValue(merageImage);
    }



    public void fileUpload(File mPhotoFile,String lastFileSeq,String custNo, String charNo){
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        mPhotoFile
                );

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", mPhotoFile.getName(), requestFile);

        RequestBody custNos = RequestBody.create(MediaType.parse("text/plain"), custNo);

        AppApi appApi = RetrofitAdapter.getClient(baseActivity).create(AppApi.class);
        baseActivity.progressON(baseActivity);
        appApi.uploadImage(lastFileSeq,custNo, charNo, mPhotoFile.getName(), body, custNos)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        CommonUpdateResponse commonResponse = response.body();
                        if(commonResponse.getSUCCESS().equals("1")){
                            imageUpload.setValue(true);
                        }else{

                        }
                        baseActivity.progressOFF();

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(),baseActivity);
                        Log.d("myLog", "t.getMessage() " +t.getMessage());
                        baseActivity.progressOFF();
                    }
                });

    }

    public void deleteImage(String custNo, String fileNm){
        AppApi appApi = RetrofitAdapter.getClient(baseActivity).create(AppApi.class);
        baseActivity.progressON(baseActivity);
        appApi.deleteImage(custNo, fileNm)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        CommonUpdateResponse commonResponse = response.body();
                        if(commonResponse.getSUCCESS().equals("1")){
                            imageDelete.setValue(true);
                        }else{

                        }

                        baseActivity.progressOFF();

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(),baseActivity);
                        Log.d("myLog", "t.getMessage() " +t.getMessage());
                        baseActivity.progressOFF();
                    }
                });

    }


}
