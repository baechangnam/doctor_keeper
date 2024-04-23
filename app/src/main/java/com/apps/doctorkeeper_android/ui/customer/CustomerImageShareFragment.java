package com.apps.doctorkeeper_android.ui.customer;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageMergeBinding;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerImageShareBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerImageAdapter;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerImageShareAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.apps.doctorkeeper_android.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;


public class CustomerImageShareFragment extends BaseFragment implements ItemClick {
    FragmentCustomerImageShareBinding binding;
    CustomerImageViewModel customerViewModel;
    View rootView;
    String baseImageUrl = "";
    CustomerImageShareAdapter mAdapter;

    ArrayList<CustomerModel> csModel = new ArrayList<>();
    ArrayList<CustomerImageModel> csImageList = new ArrayList<>();

    public static CustomerImageShareFragment newInstance(Bundle args) {
        CustomerImageShareFragment fragment = new CustomerImageShareFragment();
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
        binding = FragmentCustomerImageShareBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        baseImageUrl = pref.getValue(RbPreference.IMG_URL, CommonValue.IMAGE_URL);
        String custNo = getArguments().getString("custNo");

        customerViewModel = new ViewModelProvider(requireActivity(), factory).get(CustomerImageViewModel.class);

        initViewEvent();
        initViewModelObserving();
        customerViewModel.getPatientImageList(custNo);

        return rootView;
    }

    private void initViewModelObserving() {
        customerViewModel.getCustomerImageData().observe(mActivity, response -> {

            if (response == null) {
                CommonUtils.showSnackBar(binding.rvImagelist, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {

                csModel.addAll(response.getRESULT1());
                csImageList.addAll(response.getRESULT());

                setImage();

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
        mAdapter = new CustomerImageShareAdapter(mActivity,
                csImageList, this);// 리스트
        mAdapter.setHasStableIds(true);
        binding.rvImagelist.setAdapter(mAdapter);

    }

    void initViewEvent() {
        //창 닫기
        binding.btSlideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.progressON(mActivity);
                String downImageUrl="";
                String fileNm = csImageList.get(selectedPos).getFileNm();
                String filePathNm = csImageList.get(selectedPos).getFilePathNm();
                downImageUrl = baseImageUrl + filePathNm + fileNm;

                downLoadFile(downImageUrl);

            }
        });

    }

    private void share(Uri uri) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");

            // Set the image file URI
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Optional: Add a subject and text to the sharing content
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "이미지 공유");

            // Grant permission for receiving apps to read the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the intent
            startActivity(Intent.createChooser(shareIntent, "이미지 공유"));
        }catch (Exception e){
            Log.d("myLog", " share  error  " + e.toString());
        }


    }




    @Override
    public void itemClick(String code, String idx) {
        selectedPos = Integer.valueOf(idx);
        mAdapter.setSelPos(selectedPos);
    }

    int selectedPos = 0;


    String filename="";
    private void downLoadFile(String fileUrl) {
        File filePath = new File(mActivity.getFilesDir() + "/share");
        if (!filePath.exists()) {
            filePath.mkdir();
        }

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
              //  File filePath = new File(mActivity.getFilesDir() + "/share");
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
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

            }

            @Override
            public void onNext(@NonNull File file) {
                Bitmap bitmap = decodeFile(file);
                Uri uri = shareFile(bitmap);

                Log.d("myLog", "uri " + uri);

                share(uri);

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

    public  Uri shareFile(Bitmap bitmap) {
        Date now = new Date();
        String fileName = "doctorkeeper_android_share_" + now + ".jpg";
        Uri imageUri=null;

        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            int quality = 100;

            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getActivity().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);

                imageUri = Uri.fromFile(image);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUri;

    }

    private Bitmap decodeFile(File file) {
        // Set options for decoding the file
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Adjust based on your requirements

        // Decode the file into a Bitmap
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }



}
