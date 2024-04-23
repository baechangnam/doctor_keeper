package com.apps.doctorkeeper_android.ui.customer;

import static com.apps.doctorkeeper_android.util.ImageUtil.getBitmapFromView;
import static com.apps.doctorkeeper_android.util.ImageUtil.pdfToBitmap;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.databinding.ViewPdfBinding;
import com.apps.doctorkeeper_android.databinding.ViewPdfSignBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.util.CommonUtils;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

public class PDFSignFragment extends BaseFragment {

    ViewPdfSignBinding binding;
    View rootView;
    String fileUri = "";
    boolean isWriting = false;
    boolean isEraser = false;
    String custNo = "";
    String fileName="";


    public static PDFSignFragment newInstance(Bundle args) {
        PDFSignFragment fragment = new PDFSignFragment();
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
        binding = ViewPdfSignBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        fileUri = getArguments().getString("url");
        custNo = getArguments().getString("custNo");
        fileName  = getArguments().getString("fileName");
        binding.back.setOnClickListener(view1 -> closeView());

        initView();
        downLoadPDF();

        return rootView;
    }

    private void initView() {
        binding.write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWriting = !isWriting;
                isEraser = false;
                if (isWriting) {
                    binding.write.setBackgroundResource(R.drawable.reds);
                    drawingView.initialize();
                    drawingView.setDrawMode(isWriting);

                    CommonUtils.showToast("쓰기 모드 입니다..", mActivity);

                } else {
                    binding.write.setBackgroundResource(R.drawable.pencle1);
                    drawingView.setDrawMode(isWriting);
                    CommonUtils.showToast("쓰기 모드 해제 되었습니다. ", mActivity);

                }

            }
        });

        binding.eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEraser = !isEraser;
                isWriting = false;
                binding.write.setBackgroundResource(R.drawable.pencle1);
                if (isEraser) {
                    drawingView.setDrawMode(true);
                    drawingView.initializeEraser();
                    //    binding.scratchPad.setPenSize(20f);

                } else {
                    drawingView.setDrawMode(false);
                    CommonUtils.showToast("지우개 모드 해제 되었습니다.", mActivity);

                }

            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adialog = new AlertDialog.Builder(
                        mActivity);
                adialog.setMessage("저장 하시겠습니까?")
                        .setPositiveButton("확인",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                   takeScreenshot(binding.scView);



                                }

                        ).setNegativeButton("취소",
                                (dialog, which) -> dialog.dismiss());

                AlertDialog alert = adialog.create();
                alert.show();

            }
        });
    }

    private void closeView() {
        mActivity.removeFragment(this);
    }

    private void downLoadPDF() {
        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);

        mActivity.progressON(getActivity());
        appApi.downloadFile(fileUri).subscribeOn(Schedulers.io())
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
                String filename = fileUri.substring(fileUri.lastIndexOf("/") + 1);

                File filePath = new File(getActivity().getFilesDir() + "/pdf");
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
                try {

                    mPhotoFile = file;
                    showPDF();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

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

    PDFDrawingView drawingView;

    private void showPDF() {

        Bitmap pdf = pdfToBitmap(mActivity, mPhotoFile, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawingView = new PDFDrawingView(mActivity, pdf.getWidth(), pdf.getHeight());
                drawingView.setBitmap(pdf);
                binding.pratt.addView(drawingView);
            }
        }, 1000);
    }


    File mPhotoFile = null;

    public void saveAgreement(File mPhotoFile, String custNo) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("application/pdf"),
                        mPhotoFile
                );

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", fileName, requestFile);

        Log.d("myLog", "mPhotoFile.getName() " + mPhotoFile.getName());

        RequestBody custNos = RequestBody.create(MediaType.parse("text/plain"), custNo);

        AppApi appApi = RetrofitAdapter.getClient(mActivity).create(AppApi.class);
        mActivity.progressON(mActivity);
        appApi.regAgreement(body, custNos)
                .enqueue(new Callback<CommonUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CommonUpdateResponse> call, Response<CommonUpdateResponse> response) {
                        mActivity.progressOFF();
                        CommonUpdateResponse commonResponse = response.body();
                        if(commonResponse==null){
                            CommonUtils.showAlert("서버 오류입니다. 잠시 후 다시 시도해 주세요", getActivity());
                        }else{
                            if (commonResponse.getSUCCESS().equals("1")) {
                                Log.d("myLog", "regAgreement  ok!! ");
                                Bundle bundle = new Bundle();
                                bundle.putString("custNo", custNo);
                                CustomerAgreeFragment customerAgreeFragment = CustomerAgreeFragment.newInstance(bundle);
                                mActivity.hideBottomMenu();
                                mActivity.startFragment(customerAgreeFragment);

                                closeView();

                            } else {
                                CommonUtils.showAlert("서버 오류입니다. 잠시 후 다시 시도해 주세요", getActivity());
                            }
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

    public void takeScreenshot(ScrollView v) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String fileName = custNo+"_sign.pdf";

        int totalHeight = v.getChildAt(0).getHeight();
        int totalWidth = v.getChildAt(0).getWidth();

        Bitmap bitmap = getBitmapFromView(v, totalHeight, totalWidth);

        int quality = 100;

        try {
            OutputStream fos;
            //String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File filePath = new File(v.getContext().getFilesDir()+"/sign/");
            if(!filePath.exists()){
                filePath.mkdir();
            }
            File image = new File(filePath, fileName);
            fos = new FileOutputStream(image);

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.close();

            convertBitmapToPdf(bitmap,image.getPath());



        }catch (Exception e){

        }


    }

    public  void convertBitmapToPdf(Bitmap bitmap, String pdfFilePath) {
        File pdfFile=null;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        try {
            pdfFile = new File(pdfFilePath);
            document.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        if(pdfFile!=null){
            saveAgreement(pdfFile, custNo);
        }

    }




}
