package com.apps.doctorkeeper_android.api;


import com.apps.doctorkeeper_android.data.login.LoginResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppApiOld {


    @GET("auth/login.stc")
    Observable<Response<LoginResponse>> login(@Query("empNo") String empNo , @Query("empPwd") String empPwd );


}
