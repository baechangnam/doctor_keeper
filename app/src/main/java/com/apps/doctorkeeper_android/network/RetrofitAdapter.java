package com.apps.doctorkeeper_android.network;

import android.content.Context;
import android.util.Log;

import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.db.RbPreference;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {
    static Retrofit retrofit = null;
    public static Retrofit getClient(Context context) {
        RbPreference preference = new RbPreference(context);
        String baseUrl = preference.getValue(RbPreference.API_URL,CommonValue.DEFAULT_URL);

     //   if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(createOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
     //   }
        return retrofit;
    }

    public static OkHttpClient createOkHttpClient(Context context) {
        RbPreference pref = new RbPreference(context);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //builder.cookieJar(new RetrofitCookieJar()); // Session 통신을 위한 기능
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder().addHeader("Cookie", pref.getValue(RbPreference.CSID, ""))
                    .addHeader("Cookie", pref.getValue(RbPreference.CSNM, ""))
                    .addHeader("Cookie", pref.getValue(RbPreference.CSPB, ""))
                    .addHeader("Cookie", pref.getValue(RbPreference.USERNM, ""))

                    .build();
            return chain.proceed(request);
        });

        builder.addInterceptor(httpLoggingInterceptor);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);
        return builder.build();
    }
}
