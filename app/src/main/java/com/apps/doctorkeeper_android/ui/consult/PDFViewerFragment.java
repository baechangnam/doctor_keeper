package com.apps.doctorkeeper_android.ui.consult;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;

import com.apps.doctorkeeper_android.databinding.ViewPdfBinding;
import com.apps.doctorkeeper_android.databinding.ViewPdfSignBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;

import java.io.File;

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

public class PDFViewerFragment extends BaseFragment {

    ViewPdfBinding binding;
    View rootView;
    String fileUri="";

    public static PDFViewerFragment newInstance(Bundle args) {
        PDFViewerFragment fragment = new PDFViewerFragment();
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
        binding = ViewPdfBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        fileUri = getArguments().getString("url");

        Log.d("myLog" , "fileUri " + fileUri);

        binding.back.setOnClickListener(view1 -> closeView());

        downLoadPDF();

        return rootView;
    }

    private void closeView(){
        mActivity.showBottomMenu();
        mActivity.removeFragment(this);
    }

    private void downLoadPDF(){
        File filePath = new File(getActivity().getFilesDir()+"/pdf");
        if(!filePath.exists()){
            filePath.mkdir();
        }
        String filename=fileUri.substring(fileUri.lastIndexOf("/")+1);

//        File outputFile = new File(filePath, filename);
//        if (outputFile.exists()) { // 기존 파일 존재시 바로 출력 ,없을시 다운 후 출력
//            binding.pdfView.fromFile(outputFile).load();
//        }else{
//            AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);
//
//            mActivity.progressON(getActivity());
//            appApi.downloadFile(fileUri).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread()).flatMap(processResponse())
//                    .subscribe(handleResult());
//        }

        AppApi appApi = RetrofitAdapter.getClient(getActivity()).create(AppApi.class);

        mActivity.progressON(getActivity());
        appApi.downloadFile(fileUri).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).flatMap(processResponse())
                .subscribe(handleResult());


    }
    public Function<Response<ResponseBody>, Observable<File>> processResponse(){
        return responseBodyResponse -> saveToDiskRx(responseBodyResponse);
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response){
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> subscriber) throws Throwable {
                String filename=fileUri.substring(fileUri.lastIndexOf("/")+1);

                File filePath = new File(getActivity().getFilesDir()+"/pdf");
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
                binding.pdfView.fromFile(file).load();
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


}
