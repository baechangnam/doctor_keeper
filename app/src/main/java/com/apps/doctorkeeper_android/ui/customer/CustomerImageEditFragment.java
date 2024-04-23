package com.apps.doctorkeeper_android.ui.customer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.databinding.FramentPhotoEditBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerImageShareAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.apps.doctorkeeper_android.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

import org.jetbrains.annotations.NotNull;

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
import yuku.ambilwarna.AmbilWarnaDialog;


public class CustomerImageEditFragment extends BaseFragment {
    FramentPhotoEditBinding binding;
    CustomerImageViewModel customerViewModel;
    View rootView;
    String custNo = "";
    String url = "";
    Bitmap oriBitmap = null;
    Bitmap editBitmap = null;
    Bitmap rotBitmap = null;

    MediaRecorder mediaRecorder;

    public static CustomerImageEditFragment newInstance(Bundle args) {
        CustomerImageEditFragment fragment = new CustomerImageEditFragment();
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
        binding = FramentPhotoEditBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();


        custNo = getArguments().getString("custNo");
        url = getArguments().getString("url");

        customerViewModel = new ViewModelProvider(requireActivity(), factory).get(CustomerImageViewModel.class);

        isRecord = customerViewModel.isViewModelRecord();
        if(isRecord){
            binding.btBlink.setVisibility(View.VISIBLE);
            binding.btBlink.startBlinkAnimation();
        }else{
            binding.btBlink.setVisibility(View.GONE);
        }

        downLoadFile(url);
        initViewEvent();
        initViewModelObserving();


        return rootView;
    }

    private void initViewModelObserving() {
//        customerViewModel.getCustomerImageData().observe(mActivity, response -> {
//
//            if (response == null) {
//
//            } else {
//
//
//            }
//        });
    }


    private void closeView() {

        mActivity.removeFragment(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("myLog" ,"onDetach edit " + isRecord);
        customerViewModel.setIsRecording(isRecord);
    }




    void initViewEvent() {

        binding.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(binding.record);
            }
        });

        binding.penWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLine(binding.penWidth);
            }
        });

        //창 닫기
        binding.btSlideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        binding.upTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rotBitmap != null && editTagRot) {
                    editTagRot = false;
                    editBitmap = rotBitmap;
                }

                editBitmap = InversionBitmap(editBitmap, 3);
                reloadBitmap(editBitmap);

            }
        });

        binding.leftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rotBitmap != null && editTagRot) {
                    editTagRot = false;
                    editBitmap = rotBitmap;
                }

                editBitmap = InversionBitmap(editBitmap, 1);
                reloadBitmap(editBitmap);

            }
        });


        binding.rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTagRot = true;
                rotateRightRatio -= 90;
                if (rotateRightRatio == 0) {
                    rotateRightRatio = 360;
                }


                Bitmap rotBitmap = rotateBitmap(editBitmap, rotateRightRatio);
                reloadBitmapRot(rotBitmap);


            }
        });

        binding.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeView();
            }
        });

        binding.rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTagRot = true;
                rotateRightRatio += 90;
                if (rotateRightRatio == 360) {
                    rotateRightRatio = 0;
                }
                Bitmap rotBitmap = rotateBitmap(editBitmap, rotateRightRatio);
                reloadBitmapRot(rotBitmap);

            }
        });

        binding.penColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int initialColor = 0;
                AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(mActivity, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // Handle the cancellation
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        colorPen = color;

                        binding.drawView.setPenColor(color);
                        binding.drawView.setVisibility(View.VISIBLE);
                        binding.drawView.initializePen();
                    }
                });

                colorPicker.show();


            }
        });

        binding.btHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.drawView.setPenSize(lineWidth);
                CommonUtils.showToast("쓰기 모드 입니다..", mActivity);
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenColor(Color.parseColor("#000000"));
                binding.drawView.initializePen();

            }
        });

        binding.btClearSome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.showToast("지우기 모드 입니다.", mActivity);
                binding.drawView.setPenSize(60f);
                binding.drawView.initializeEraser();

            }
        });

        binding.btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawView.clear();

            }
        });

    }

    int rotateRightRatio = 0;
    boolean editTagRot = false;

    int colorPen = 0xFF000000;


    public Bitmap InversionBitmap(Bitmap bitmap, int inverse) {
        Matrix sideInversion = new Matrix();
        if (inverse == 0)
            sideInversion.setScale(1, 1);
        else if (inverse == 1)
            sideInversion.setScale(-1, 1);   // 좌우반전
        else if (inverse == 2)
            sideInversion.setScale(1, 1);
        else sideInversion.setScale(1, -1);   // 상하반전

        Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), sideInversion, false);
        return sideInversionImg;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        Log.d("TEST", "ROTATE : " + rotate);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    String filename = "";

    private void downLoadFile(String fileUrl) {
        File filePath = new File(mActivity.getFilesDir() + "/edit");
        if (!filePath.exists()) {
            filePath.mkdir();
        }

        mActivity.progressON(mActivity);

        filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        AppApi appApi = RetrofitAdapter.getClient(mActivity).create(AppApi.class);

        appApi.downloadFile(fileUrl).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).flatMap(processResponse())
                .subscribe(handleResult());

    }

    public Function<Response<ResponseBody>, Observable<File>> processResponse() {
        return responseBodyResponse -> saveToDiskRx(responseBodyResponse);
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> subscriber) throws Throwable {
                File filePath = new File(mActivity.getFilesDir() + "/edit");
                File outputFile = new File(filePath, filename);

                BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputFile));
                bufferedSink.writeAll(response.body().source());
                bufferedSink.close();

                subscriber.onNext(outputFile);
                subscriber.onComplete();
            }
        });
    }

    private Observer<File> handleResult() {
        return new Observer<File>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mActivity.progressOFF();
            }

            @Override
            public void onNext(@NonNull File file) {
                mActivity.progressOFF();
                setImageView(file);
                //  setDrawView(file);

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mActivity.progressOFF();
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                mActivity.progressOFF();

            }
        };
    }

    private void setImageView(File file) {
        oriBitmap = getBitmapFromFile(file.getPath());
        editBitmap = oriBitmap;

        Glide.with(mActivity)
                .load(editBitmap)
                .into(binding.bitmapView);

    }

    private void reloadBitmap(Bitmap bitmap) {
        Glide.with(mActivity)
                .load(bitmap)
                .into(binding.bitmapView);

    }

    private void reloadBitmapRot(Bitmap bitmap) {

        rotBitmap = bitmap;
        Glide.with(mActivity)
                .load(bitmap)
                .into(binding.bitmapView);

    }


    private void setDrawView(File file) {
        Bitmap bitmap = getBitmapFromFile(file.getPath());
        binding.drawView.loadImage(bitmap);


    }


    private Bitmap getBitmapFromFile(String filePath) {
        File imgFile = new File(filePath);

        if (imgFile.exists()) {
            // Decode the file into a Bitmap
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        } else {
            // File doesn't exist
            return null;
        }
    }


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

        mediaRecorder = customerViewModel.getMediaPlayer();
        customerViewModel.setViewModelRecord(true);
        customerViewModel.setRecordingFilePath(outputPath);


        //  mediaRecorder = new MediaRecorder();
        mediaRecorder = customerViewModel.getMediaPlayer();
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

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        customerViewModel.resetMediaRecord();

        binding.btBlink.stopBlinkAnimation();
        binding.btBlink.setVisibility(View.GONE);

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
                        CommonUpdateResponse commonResponse = response.body();
                        if(commonResponse.getSUCCESS().equals("1")){
                            popupWindow.dismiss();
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


    public void showLine(View anchorView) {
        // Create a View object for the popup
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.view_line_select, null);

        // Set up the PopupWindow
        PopupWindow popupWindow = new PopupWindow(
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

        LinearLayout line1 = popupView.findViewById(R.id.line1);
        line1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = 10f;
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenSize(lineWidth);
                binding.drawView.initializePen();
                popupWindow.dismiss();
            }
        });

        LinearLayout line2 = popupView.findViewById(R.id.line2);
        line2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = 20f;
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenSize(lineWidth);
                binding.drawView.initializePen();
                popupWindow.dismiss();
            }
        });

        LinearLayout line3 = popupView.findViewById(R.id.line3);
        line3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = 30f;
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenSize(lineWidth);
                binding.drawView.initializePen();
                popupWindow.dismiss();
            }
        });

        LinearLayout line4 = popupView.findViewById(R.id.line4);
        line4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = 40f;
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenSize(lineWidth);
                binding.drawView.initializePen();
                popupWindow.dismiss();
            }
        });


        LinearLayout line5 = popupView.findViewById(R.id.line5);
        line5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineWidth = 50f;
                binding.drawView.setVisibility(View.VISIBLE);
                binding.drawView.setPenSize(lineWidth);
                binding.drawView.initializePen();
                popupWindow.dismiss();
            }
        });

        // Close the popup when clicked outside
        popupView.setOnClickListener(v -> popupWindow.dismiss());
    }

    float lineWidth = 10f;


    private void makeView(){
        binding.menuView.setVisibility(View.GONE);
        String path = ImageUtil.editImage(binding.captureView);
        File file = new File(path);

        fileUpload(file,"",custNo,custNo,path);
    }

    public void fileUpload(File mPhotoFile, String lastFileSeq, String custNo, String charNo,String path) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        mPhotoFile
                );

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", mPhotoFile.getName(), requestFile);

        RequestBody custNos = RequestBody.create(MediaType.parse("text/plain"), custNo);

        AppApi appApi = RetrofitAdapter.getClient(mActivity).create(AppApi.class);
        mActivity.progressON(mActivity);
        appApi.uploadImage(lastFileSeq, custNo, charNo, mPhotoFile.getName(), body, custNos)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        CommonUpdateResponse commonResponse = response.body();
                        if (commonResponse.getSUCCESS().equals("1")) {
                            customerViewModel.setEditImage(path);
                            closeView();

                        } else {

                        }

                        mActivity.progressOFF();

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), mActivity);
                        Log.d("myLog", "t.getMessage() " + t.getMessage());
                        mActivity.progressOFF();
                    }
                });

    }
}
