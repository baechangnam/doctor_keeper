package com.apps.doctorkeeper_android.ui.customer;


import static com.apps.doctorkeeper_android.util.CommonUtils.getDateYYmmdd;
import static com.apps.doctorkeeper_android.util.ImageUtil.createImageFile;
import static com.apps.doctorkeeper_android.util.ImageUtil.takeScreenshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

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
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseActivity;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerXrayModel;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerImageAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerXrayImageAdapter;
import com.apps.doctorkeeper_android.ui.login.IPSettingDialog;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.apps.doctorkeeper_android.util.PDFPrintDocumentAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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


public class CustomerImageFragment extends BaseFragment implements ItemClick {
    FragmentCustomerImageBinding binding;
    CustomerImageViewModel customerViewModel;
    View rootView;
    String custNo = "";
    String charNo = "";
    String baseImageUrl = "";
    ArrayList<CustomerModel> csModel = new ArrayList<>();
    ArrayList<CustomerImageModel> csImageList = new ArrayList<>();
    CustomerImageAdapter mAdapter;

    private final int REQUEST_LOAD_IMG = 1102;
    private final int REQUEST_LOAD_CAMERA = 1103;

    private final int REQUEST_CROP = UCrop.REQUEST_CROP;


    ArrayList<CustomerXrayModel> csXrayList = new ArrayList<>();
    CustomerXrayImageAdapter xAdapter;


    int lastFileSeq = 0;
    Uri mCameraImagePath;
    File mPhotoFile;
    String baseXrayUrl = "";

    int selectedPos = 0; //이미지 선택된 위치
    int selectedXrayPos = 0; //x레이이미지 선택된 위치
    private int mode = 1; //1일반이미지 , 2 엑스레이 ,3 분할화면
    boolean isOneSelect = false;//다중선택왼쪽
    boolean isTwoSelect = false;//다중선택 오른쪽

    String oneImageUrl = "";//왼쪽 이미지경로
    String twoImageUrl = "";//오른쪽 이미지 경로

    public static CustomerImageFragment newInstance(Bundle args) {
        CustomerImageFragment fragment = new CustomerImageFragment();
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


    MediaRecorder mediaRecorder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerImageBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();


        baseImageUrl = pref.getValue(RbPreference.IMG_URL, CommonValue.IMAGE_URL);
        baseXrayUrl = pref.getValue(RbPreference.BASE_URL, CommonValue.BASE_URL);

        custNo = getArguments().getString("code");
        charNo = getArguments().getString("charNo");
        customerViewModel = new ViewModelProvider(requireActivity(), factory).get(CustomerImageViewModel.class);


        initViewModelObserving();
        initViewEvent();

        customerViewModel.getPatientImageList(custNo);
        isRecord = customerViewModel.isViewModelRecord();

        return rootView;
    }

    private void closeView() {
        requireActivity().getViewModelStore().clear();
        mActivity.showBottomMenu();
        mActivity.removeFragment(this);
    }


    private void initViewModelObserving() {
        customerViewModel.getCustomerImageData().observe(mActivity, response -> {

            if (response == null) {
                CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {

                csModel.addAll(response.getRESULT1());
                csImageList.addAll(response.getRESULT());

                if (csModel.size() > 0) {
                    binding.tvUserName.setText(csModel.get(0).getCustNm() + "님");
                }

                for (int i = 0; i < csImageList.size(); i++) {
                    if (i == csImageList.size() - 1) {
                        lastFileSeq = Integer.valueOf(csImageList.get(i).getFileSeq());
                    }
                }

                setImage();

            }
        });

        //녹음 체크
        customerViewModel.getIsRecording().observe(mActivity, response -> {

            if (response == null) {
            } else {
                isRecord = customerViewModel.isViewModelRecord();
                if (isRecord) {
                    binding.btBlink.setVisibility(View.VISIBLE);
                    binding.btBlink.startBlinkAnimation();
                } else {
                    binding.btBlink.setVisibility(View.GONE);
                }

            }
        });

        customerViewModel.getXrayListData().observe(mActivity, response -> {

            if (response == null) {
                CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {

                csXrayList.addAll(response.getRESULT());

                setImageXray();

            }
        });

        customerViewModel.getMergeData().observe(mActivity, response -> {

            if (response == null) {
                //  CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                Log.d("myLog", "merge " + response.toString());

                mode = 1;
                showImageView();


                String path = response.toString();


                mergeResponce = true;
                mergePath = path;

                //mPhotoFile.delete();
                mPhotoFile = new File(path);

                customerViewModel.fileUpload(mPhotoFile, "", custNo, charNo);

            }
        });

        customerViewModel.getImageDelete().observe(mActivity, response -> {
            if (response == null) {
                //  CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                if (response) {
                    if (mode == 1) {
                        csImageList.remove(selectedPos);
                        if (csImageList.size() == 0) {
                            binding.ivMainImage.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.setSelPos(selectedPos);
                            reloadImage("image", selectedPos + "");

                        }

                    } else {
                        csXrayList.remove(selectedXrayPos);
                        if (csXrayList.size() == 0) {
                            binding.ivXRayImage.setVisibility(View.GONE);
                            xAdapter.notifyDataSetChanged();
                        } else {
                            xAdapter.setSelPos(selectedXrayPos);
                            reloadImage("xray", selectedPos + "");

                        }

                    }
                    CommonUtils.showSnackBar(binding.ivMainImage, "이미지가 삭제 되었습니다.");
                }
            }
        });

        customerViewModel.setImageupload().observe(mActivity, response -> {
            if (response == null) {
                //  CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                if (response) {
                    if (mergeResponce) {
                        mergeResponce = false;
                        reLoadMainImage(mergePath);
                    } else {
                        reLoadMainImage(uploadPathIntenal);
                    }

                }
            }

        });

        customerViewModel.getEditImage().observe(mActivity, response -> {
            if (response == null) {
                //  CommonUtils.showSnackBar(binding.ivMainImage, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                reLoadMainImage(response);
            }

        });
    }

    boolean mergeResponce = true;
    String mergePath = "";


    void initViewEvent() {


        //창 닫기
        binding.btSlideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        //고객정보 수정
        binding.btUpdateUser.setOnClickListener(view -> {
            CustomerUpdateDialog customerRegDialog = CustomerUpdateDialog.getInstance();

            if (csModel.size() > 0) {
                Bundle ex = new Bundle();
                ex.putString("name", csModel.get(0).getCustNm());
                ex.putString("charNo", csModel.get(0).getChrtNo());
                ex.putString("gender", csModel.get(0).getSexCd());
                ex.putString("mail", csModel.get(0).getEmailAddr());
                ex.putString("birth", csModel.get(0).getBrthYmd());
                ex.putString("type", csModel.get(0).getCustInfo());
                ex.putString("custNo", csModel.get(0).getCustNo());
                ex.putString("custInfo", csModel.get(0).getCustInfo());
                customerRegDialog.setArguments(ex);
            }

            customerRegDialog.show(getFragmentManager(), IPSettingDialog.TAG_EVENT_DIALOG);
        });

        //사진촬영전면
        binding.btCameraFront.setOnClickListener(view -> {
            takePictureFront();
        });

        //사진촬영후면
        binding.btCameraRear.setOnClickListener(view -> {
            takePictureRear();
        });

        //갤러리 사진
        binding.btGallery.setOnClickListener(view -> {
            if (mode == 1) {
                pickImage();
            } else if (mode == 2) {
                mode = 1;
                showImageView();
            }


        });

        if (pref.getValue(RbPreference.IS_X_RAY, false)) {
            binding.btXray.setVisibility(View.VISIBLE);
        } else {
            binding.btXray.setVisibility(View.GONE);
        }
        //엑스레이 사진
        binding.btXray.setOnClickListener(view -> {
            mode = 2;
            customerViewModel.getXrayList(charNo);
        });

        //리프레시
        binding.btRefresh.setOnClickListener(view -> {
            if (mode == 1) {
                csImageList.clear();
                customerViewModel.getPatientImageList(custNo);
            } else if (mode == 2) {
                csImageList.clear();
                customerViewModel.getXrayList(charNo);
            }


        });

        //스크린캡쳐
        binding.btCapture.setOnClickListener(view -> {
            if (mode == 1) {
                if (csImageList.size() > 0) {
                    takeScreenshots(binding.ivMainImage);
                } else {
                    CommonUtils.showToast("사진이 없습니다.", mActivity);
                }
            } else if (mode == 2) {
                if (csXrayList.size() > 0) {
                    takeScreenshots(binding.ivXRayImage);
                } else {
                    CommonUtils.showToast("사진이 없습니다.", mActivity);
                }
            } else {

            }

        });

        //분할화면
        binding.tvOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1) {
                    if (csImageList.size() > 0) {
                        isOneSelect = true;
                        binding.tvOne.setBackgroundColor(Color.parseColor("#00A0E9"));
                        binding.tvOne.setTextColor(Color.parseColor("#ffffff"));

                        String fileNm = csImageList.get(selectedPos).getFileNm();
                        String filePathNm = csImageList.get(selectedPos).getFilePathNm();

                        if (csImageList.get(selectedPos).getUseFlg() == null) {
                            oneImageUrl = baseImageUrl + filePathNm + fileNm;
                        } else {
                            oneImageUrl = filePathNm;
                        }

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                } else if (mode == 2) {
                    if (csXrayList.size() > 0) {
                        isOneSelect = true;
                        binding.tvOne.setBackgroundColor(Color.parseColor("#00A0E9"));
                        binding.tvOne.setTextColor(Color.parseColor("#ffffff"));

                        String fileNm = csXrayList.get(selectedXrayPos).getFileNm();
                        String filePathNm = csXrayList.get(selectedXrayPos).getFilePathNm();

                        if (csXrayList.get(selectedXrayPos).getUseFlg() == null) {
                            oneImageUrl = baseXrayUrl + fileNm;
                        } else {
                            oneImageUrl = filePathNm;
                        }

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                }


            }
        });

        binding.tvTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMergeImage();

            }
        });

        binding.btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("custNo", custNo);
                CustomerImageShareFragment customerImageShareFragment = CustomerImageShareFragment.newInstance(bundle);
                mActivity.hideBottomMenu();
                mActivity.addFragment(customerImageShareFragment);
            }
        });

        binding.btAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("custNo", custNo);
                CustomerAgreeFragment customerAgreeFragment = CustomerAgreeFragment.newInstance(bundle);
                mActivity.hideBottomMenu();
                mActivity.addFragment(customerAgreeFragment);
            }
        });

        if (isRecord) {
            binding.btBlink.setVisibility(View.VISIBLE);
            binding.btBlink.startBlinkAnimation();
        } else {
            binding.btBlink.setVisibility(View.GONE);
        }
        binding.btRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mActivity.showPopup(binding.btRecord,custNo);
                showPopup(binding.btRecord);
            }
        });

        binding.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1) {
                    if (csImageList.size() > 0) {
                        String downImageUrl = "";
                        String fileNm = csImageList.get(selectedPos).getFileNm();
                        String filePathNm = csImageList.get(selectedPos).getFilePathNm();

                        if (csImageList.get(selectedPos).getUseFlg() == null) {
                            downImageUrl = baseImageUrl + filePathNm + fileNm;
                        } else {
                            downImageUrl = filePathNm;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("custNo", custNo);
                        bundle.putString("url", downImageUrl);

                        CustomerImageEditFragment customerAgreeFragment = CustomerImageEditFragment.newInstance(bundle);
                        mActivity.hideBottomMenu();
                        mActivity.addFragment(customerAgreeFragment);

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                } else if (mode == 2) {
                    if (csXrayList.size() > 0) {
                        String downImageUrl = "";

                        String fileNm = csXrayList.get(selectedXrayPos).getFileNm();
                        String filePathNm = csXrayList.get(selectedXrayPos).getFilePathNm();

                        if (csXrayList.get(selectedXrayPos).getUseFlg() == null) {
                            downImageUrl = baseXrayUrl + fileNm;
                        } else {
                            downImageUrl = filePathNm;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("custNo", custNo);
                        bundle.putString("url", downImageUrl);

                        CustomerImageEditFragment customerAgreeFragment = CustomerImageEditFragment.newInstance(bundle);
                        mActivity.hideBottomMenu();
                        mActivity.addFragment(customerAgreeFragment);

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                }

            }
        });

        binding.btPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1) {
                    if (csImageList.size() > 0) {
                        binding.prLoading.setVisibility(View.VISIBLE);
                        String downImageUrl = "";
                        String fileNm = csImageList.get(selectedPos).getFileNm();
                        String filePathNm = csImageList.get(selectedPos).getFilePathNm();

                        if (csImageList.get(selectedPos).getUseFlg() == null) {
                            downImageUrl = baseImageUrl + filePathNm + fileNm;
                        } else {
                            downImageUrl = filePathNm;
                        }

                      //  mActivity.progressON(mActivity);
                        downLoadPrintFile(downImageUrl);

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                } else if (mode == 2) {
                    if (csXrayList.size() > 0) {
                        binding.prLoading.setVisibility(View.VISIBLE);
                        String downImageUrl = "";

                        String fileNm = csXrayList.get(selectedXrayPos).getFileNm();
                        String filePathNm = csXrayList.get(selectedXrayPos).getFilePathNm();

                        if (csXrayList.get(selectedXrayPos).getUseFlg() == null) {
                            downImageUrl = baseXrayUrl + fileNm;
                        } else {
                            downImageUrl = filePathNm;
                        }
                       // mActivity.progressON(mActivity);
                        downLoadPrintFile(downImageUrl);

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                }
            }
        });


        //이미지 삭제
        binding.btTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 1) {
                    if (csImageList.size() > 0) {
                        AlertDialog.Builder adialog = new AlertDialog.Builder(
                                mActivity);
                        adialog.setMessage("삭제 하시겠습니까?")
                                .setPositiveButton("확인",
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            customerViewModel.deleteImage(custNo, csImageList.get(selectedPos).getFileNm());
                                        }

                                ).setNegativeButton("취소",
                                        (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = adialog.create();
                        alert.show();

                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                } else if (mode == 2) {
                    if (csXrayList.size() > 0) {
                        AlertDialog.Builder adialog = new AlertDialog.Builder(
                                mActivity);
                        adialog.setMessage("삭제 하시겠습니까?")
                                .setPositiveButton("확인",
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            customerViewModel.deleteImage(custNo, csXrayList.get(selectedXrayPos).getFileNm());
                                        }

                                ).setNegativeButton("취소",
                                        (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = adialog.create();
                        alert.show();


                    } else {
                        CommonUtils.showToast("사진이 없습니다.", mActivity);
                    }
                }

            }
        });
    }


    private void checkMergeImage() {
        if (mode == 1) {
            if (csImageList.size() > 0) {
                if (isOneSelect) {
                    isTwoSelect = true;
                    binding.tvTwo.setBackgroundColor(Color.parseColor("#00A0E9"));
                    binding.tvTwo.setTextColor(Color.parseColor("#ffffff"));

                    String fileNm = csImageList.get(selectedPos).getFileNm();
                    String filePathNm = csImageList.get(selectedPos).getFilePathNm();

                    if (csImageList.get(selectedPos).getUseFlg() == null) {
                        twoImageUrl = baseImageUrl + filePathNm + fileNm;
                    } else {
                        twoImageUrl = filePathNm;
                    }

                    moveTwoImageMerge();

                } else {
                    CommonUtils.showToast("첫번째 사진을 먼저 선택하세요..", mActivity);
                }


            } else {
                CommonUtils.showToast("사진이 없습니다.", mActivity);
            }
        } else if (mode == 2) {
            if (csXrayList.size() > 0) {
                if (isOneSelect) {
                    isTwoSelect = true;
                    binding.tvTwo.setBackgroundColor(Color.parseColor("#00A0E9"));
                    binding.tvTwo.setTextColor(Color.parseColor("#ffffff"));

                    String fileNm = csXrayList.get(selectedXrayPos).getFileNm();
                    String filePathNm = csXrayList.get(selectedXrayPos).getFilePathNm();

                    if (csXrayList.get(selectedXrayPos).getUseFlg() == null) {
                        twoImageUrl = baseXrayUrl + fileNm;
                    } else {
                        twoImageUrl = filePathNm;
                    }

                    moveTwoImageMerge();
                } else {
                    CommonUtils.showToast("첫번째 사진을 먼저 선택하세요..", mActivity);
                }
            } else {
                CommonUtils.showToast("사진이 없습니다.", mActivity);
            }
        }
    }

    private void moveTwoImageMerge() {
        Bundle bundle = new Bundle();
        bundle.putString("oneImageUrl", oneImageUrl);
        bundle.putString("twoImageUrl", twoImageUrl);
        CustomerImageMergeFragment customerImageFragment = CustomerImageMergeFragment.newInstance(bundle);
        mActivity.hideBottomMenu();
        mActivity.addFragment(customerImageFragment);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void takePictureFront() {
        mPhotoFile = createImageFile(mActivity, custNo);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraImagePath = FileProvider.getUriForFile(mActivity,
                mActivity.getPackageName() + ".fileprovider",
                mPhotoFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImagePath);
        startActivityForResult(takePictureIntent, REQUEST_LOAD_CAMERA);
    }


    private void takePictureRear() {
        mPhotoFile = createImageFile(mActivity, custNo);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCameraImagePath = FileProvider.getUriForFile(mActivity,
                mActivity.getPackageName() + ".fileprovider",
                mPhotoFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImagePath);
        startActivityForResult(takePictureIntent, REQUEST_LOAD_CAMERA);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진 선택"), REQUEST_LOAD_IMG);
    }


    private File createImageFiles() throws IOException { //이미지 파일 생성

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String tempImg = "doctorkeeper_android_" + timeStamp + "";
        File storageDir = new File(mActivity.getFilesDir() + "/doctorkeeper_android_/"); //clip 경로에 이미지를 저장하기 위함

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                tempImg,
                ".jpg",
                storageDir
        );

        return image;
    }

    //사진촬영 , 갤러리 이미지 선택 후 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myLog", "requestCode " + requestCode);

        //사진촬영
        if (requestCode == REQUEST_LOAD_CAMERA && resultCode == -1) {
            uploadPathIntenal = mPhotoFile.getPath();

            mergeResponce = false;
            //   reLoadMainImage(uploadPathIntenal);
            fileUpload();

//            Uri mImageCaptureUri = Uri.fromFile(mPhotoFile);
//
//            UCrop uCrop = null;
//            UCrop.Options options = new UCrop.Options();
//            options.setCompressionQuality(50);
//            try {
//                uCrop = UCrop.of(mImageCaptureUri, Uri.fromFile(createImageFiles()));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            uCrop.withOptions(options);
//
//            uCrop.start(mActivity, CustomerImageFragment.this ,UCrop.REQUEST_CROP);

        } else if (requestCode == REQUEST_LOAD_IMG && resultCode == -1) {
            mPhotoFile = createImageFile(mActivity, custNo);

            try {
                Uri imageUri = data.getData();

                UCrop uCrop = null;
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(40);
                try {
                    uCrop = UCrop.of(imageUri, Uri.fromFile(createImageFiles()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                uCrop.withOptions(options);

                uCrop.start(mActivity, CustomerImageFragment.this, UCrop.REQUEST_CROP);

            } catch (Exception e) {

            }

//            mPhotoFile = createImageFile(mActivity, custNo);
//
//            try {
//                Uri imageUri = data.getData();
//                String imagePath = getPath(mActivity, imageUri);
//
//                resetImage(imagePath);
//
//                mPhotoFile = new File(imagePath);
//                long bytes = mPhotoFile.length();
//                long kilobyte = bytes / 1024;
//                long megabyte = kilobyte / 1024;
//
//                if (megabyte >= 4) {
//                    mPhotoFile = createImageFile(mActivity, custNo);
//                    resizeBitmap(imageUri, imagePath);
//                } else {
//                    fileUpload();
//                }
//
//
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }


        } else if (resultCode == -1 && requestCode == REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            mPhotoFile.delete();
            mPhotoFile = new File(resultUri.getPath());
            uploadPathIntenal = mPhotoFile.getPath();
            //   reLoadMainImage(mPhotoFile.getPath());
            mergeResponce = false;
            fileUpload();
        }

    }

    String uploadPathIntenal = "";


    //사진촬영후 하단 리스트에 임시 파일로 이미지 추가한다.
    private void reLoadMainImage(String path) {
        csImageList.clear();
        customerViewModel.getPatientImageList(custNo);


//        String imagePath = path;
//
//        Glide.with(mActivity).load(imagePath).apply(new RequestOptions()
//                        .override(Target.SIZE_ORIGINAL)
//                        .format(DecodeFormat.PREFER_ARGB_8888))
//                .into(binding.ivMainImage);
//
//        //  Glide.with(mActivity).load(imagePath)
//        //  .override(Target.SIZE_ORIGINAL).into(binding.ivMainImage);
//
//        csImageList.add(0, new CustomerImageModel(custNo, charNo, "1", "",
//                "", "", "", "", "", imagePath, "", "", "", ""
//                , getDateYYmmdd(), "", "", "", "0"));
//
//        mAdapter.setSelPos(0);
//        mAdapter.notifyDataSetChanged();
    }

    //파일업로드
    private void fileUpload() {
        customerViewModel.fileUpload(mPhotoFile, String.valueOf(lastFileSeq++), custNo, charNo);
    }

    private void setImage() {
        if (csImageList.size() > 0) {
            String fileNm = csImageList.get(0).getFileNm();
            String filePathNm = csImageList.get(0).getFilePathNm();

            String imageUrl = baseImageUrl + filePathNm + fileNm;

            Log.d("myLog", "imageUrl " + imageUrl);

            binding.prLoading.setVisibility(View.VISIBLE);

            Glide.with(mActivity)
                    .load(imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.prLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.prLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.ivMainImage);


        }
        binding.rvImagelist.setNestedScrollingEnabled(true);

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);

        binding.rvImagelist.setLayoutManager(mLayoutManager);
        mAdapter = new CustomerImageAdapter(mActivity,
                csImageList, this);// 리스트
        mAdapter.setHasStableIds(true);
        binding.rvImagelist.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void reloadImage(String code, String idx) {
        if (code.equals("image")) {
            String fileNm = csImageList.get(Integer.parseInt(idx)).getFileNm();
            String filePathNm = csImageList.get(Integer.parseInt(idx)).getFilePathNm();

            String imageUrl = baseImageUrl + filePathNm + fileNm;
            if (csImageList.get(Integer.parseInt(idx)).getUseFlg() == null) {
//                Glide.with(mActivity).load(imageUrl).apply(new RequestOptions()
//                                .override(Target.SIZE_ORIGINAL)
//                                .format(DecodeFormat.PREFER_ARGB_8888))
//                        .into(binding.ivMainImage);

                binding.prLoading.setVisibility(View.VISIBLE);

                Glide.with(mActivity)
                        .load(imageUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.prLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.prLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.ivMainImage);
            } else {
                Glide.with(mActivity).load(filePathNm).thumbnail(0.3f)
                        .into(binding.ivMainImage);
            }

            mAdapter.notifyDataSetChanged();
        } else {
            String fileNm = csXrayList.get(Integer.parseInt(idx)).getFileNm();
            String filePathNm = csXrayList.get(Integer.parseInt(idx)).getFilePathNm();

            String imageUrl = baseXrayUrl + fileNm;
            if (csXrayList.get(Integer.parseInt(idx)).getUseFlg() == null) {
//                Glide.with(mActivity).load(imageUrl).apply(new RequestOptions()
//                                .override(Target.SIZE_ORIGINAL)
//                                .format(DecodeFormat.PREFER_ARGB_8888))
//                        .into(binding.ivXRayImage);
//
//                binding.prLoading.setVisibility(View.VISIBLE);

                binding.prLoading.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(imageUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.prLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.prLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.ivXRayImage);

            } else {
                Glide.with(mActivity).load(filePathNm).thumbnail(0.3f)
                        .into(binding.ivXRayImage);
            }

            xAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void itemClick(String code, String idx) {
        if (code.equals("image")) {
            selectedPos = Integer.valueOf(idx);
        } else {
            selectedXrayPos = Integer.valueOf(idx);
        }

        reloadImage(code, idx);

    }


    private void showImageView() {
        binding.rvImagelist.setVisibility(View.VISIBLE);
        binding.ivMainImage.setVisibility(View.VISIBLE);

        binding.rvXraylist.setVisibility(View.GONE);
        binding.ivXRayImage.setVisibility(View.GONE);

        binding.tvOne.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.tvOne.setTextColor(Color.parseColor("#00A0E9"));

        binding.tvTwo.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.tvTwo.setTextColor(Color.parseColor("#00A0E9"));

        isOneSelect = false;
        isTwoSelect = false;

    }

    private void showXrayView() {
        binding.ivXRayImage.setVisibility(View.VISIBLE);
        binding.rvXraylist.setVisibility(View.VISIBLE);

        binding.ivMainImage.setVisibility(View.GONE);
        binding.rvImagelist.setVisibility(View.GONE);

        binding.tvOne.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.tvOne.setTextColor(Color.parseColor("#00A0E9"));

        binding.tvTwo.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.tvTwo.setTextColor(Color.parseColor("#00A0E9"));

        isOneSelect = false;
        isTwoSelect = false;

    }

    private void setImageXray() {
        showXrayView();

        if (csXrayList.size() > 0) {
            String fileNm = csXrayList.get(0).getFileNm();
            String imageUrl = baseXrayUrl + fileNm;

//            Glide.with(mActivity).load(imageUrl).apply(new RequestOptions()
//                            .override(Target.SIZE_ORIGINAL)
//                            .format(DecodeFormat.PREFER_ARGB_8888))
//                    .into(binding.ivXRayImage);

            binding.prLoading.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.prLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.prLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.ivXRayImage);


        }

        binding.rvXraylist.setNestedScrollingEnabled(true);

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);

        binding.rvXraylist.setLayoutManager(mLayoutManager);
        xAdapter = new CustomerXrayImageAdapter(mActivity,
                csXrayList, this);// 리스트
        xAdapter.setHasStableIds(true);
        binding.rvXraylist.setAdapter(xAdapter);

    }


    String filename = "";

    private void downLoadPrintFile(String fileUrl) {
        File filePath = new File(getActivity().getFilesDir() + "/print");
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);


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
                File filePath = new File(getActivity().getFilesDir() + "/print");
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
                //mActivity.progressOFF();
                binding.prLoading.setVisibility(View.GONE);
                printImages(file, mActivity);

            }

            @Override
            public void onError(@NonNull Throwable e) {
                binding.prLoading.setVisibility(View.GONE);
                mActivity.progressOFF();
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                mActivity.progressOFF();

            }
        };
    }

    public  void printImages(File pdfFile, Activity activity) {
        PrintHelper photoPrinter = new PrintHelper(activity);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);


        Bitmap bitmap = BitmapFactory.decodeFile(pdfFile.getPath());
        photoPrinter.printBitmap( "document_"+pdfFile.getName(), bitmap);

    }


    //  private MediaRecorder mediaRecorder;
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

        File filePath = new File(getActivity().getFilesDir() + "/record");
        if (!filePath.exists()) {
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

    public void regRecord(File mPhotoFile, String custNo) {
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
        appApi.regRecordFile(body, custNos)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        mActivity.progressOFF();
                        CommonUpdateResponse commonResponse = response.body();
                        if (commonResponse.getSUCCESS().equals("1")) {
                            popupWindow.dismiss();
                            CommonUtils.showAlert("녹음 파일 업로드 완료 되었습니다.", mActivity);
                        } else {

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonUpdateResponse> call, Throwable t) {
                        CommonUtils.showAlert(t.getMessage(), mActivity);
                        Log.d("myLog", "t.getMessage() " + t.getMessage());
                        mActivity.progressOFF();
                    }
                });

    }


    public void takeScreenshots(View v) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        File image = null;
        File imageShare = null;
        try {
            String fileName = "doctorkeeper_android_scshot_" + now + ".jpg";
            View v1 = v;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            int quality = 100;

            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = v1.getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            CommonUtils.showToast("이미지가 캡쳐 되었습니다.", v.getContext());

            File filePath = new File(v.getContext().getFilesDir() + "/etc/");
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            imageShare = new File(filePath, fileName);
            fos = new FileOutputStream(imageShare);

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

        } catch (IOException e) {
            e.printStackTrace();
        }


        customerViewModel.fileUpload(imageShare, String.valueOf(lastFileSeq++), custNo, charNo);


    }


}
