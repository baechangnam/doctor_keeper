package com.apps.doctorkeeper_android.api;

import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.consult.ConsultDocResponse;
import com.apps.doctorkeeper_android.data.customer.CommonUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerAgreeResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerAgreementTemplateResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerImageResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerResponse;
import com.apps.doctorkeeper_android.data.customer.CommonResponse;

import com.apps.doctorkeeper_android.data.customer.CustomerUpdateResponse;
import com.apps.doctorkeeper_android.data.customer.CustomerXrayResponse;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.data.login.LoginResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface AppApi {

    //로그인
    @GET("auth/login.stc")
    Call<LoginResponse> login(@Query("empNo") String empNo , @Query("empPwd") String empPwd );

    //상담자료리스트
    @GET("resource/counselingresources.stc")
    Call<ConsultDocResponse> getCounseling(@Query("cnsltClssCd") String cnsltClssCd);

    //상담자료리스트
    @GET("common/commoncode.stc")
    Call<CommonCodeResponse> getCommonCode(@Query("cdGrpId") String cdGrpId);

    //최근 고객검색
    @GET("customer/customersrarch.stc")
    Call<CustomerResponse> getPatientList(@Query("lstVistDt") String lstVistDt,@Query("pageCheck") String pageCheck,@Query("page") String page,@Query("rows") String rows);

    //고객검색
    @GET("customer/customersrarch.stc")
    Call<CustomerResponse> getPatientListSearch(@Query("custNm") String custNm);

    //진료분류로 검색
    @GET("customer/customersrarch.stc")
    Call<CustomerResponse> getPatientListCustInfoSearch(@Query("custInfo") String custInfo);

    //고객상세정보 검색
    @GET("customer/customersrarch.stc")
    Call<CustomerUpdateResponse> getPatientInfo(@Query("custNo") String custNo, @Query("custNm") String custNm, @Query("custInfo") String custInfo);

    //PDF파일 다운
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFile(@Url String fileUrl);

    //차트번호 중복체크
    @GET("customer/selectchrtnocheck.stc")
    Call<CommonResponse> checkChartNo(@Query("chrtNo") String chrtNo);

    //고객등록
    @POST("customer/newcustomer.stc")
    Call<CommonResponse> regCustomer(@Query("chrtNo") String chrtNo, @Query("custNm") String custNm, @Query("sexCd") String sexCd, @Query("brthYmd") String brthYmd
    , @Query("medical") List<String> names, @Query("emailAddr") String emailAddr);

    @POST("customer/updatecustomer.stc")
    Call<CommonUpdateResponse> updateCustomer(@Query("custNo") String custNo, @Query("chrtNo") String chrtNo, @Query("custNm") String custNm, @Query("sexCd") String sexCd, @Query("brthYmd") String brthYmd
            , @Query("medical") List<String> names, @Query("emailAddr") String emailAddr);

    //상세환자 이미지 리스트
    @GET("customer/customerimgsrarch.stc")
    Call<CustomerImageResponse> getPatientImageList(@Query("custNo") String custNo);

    //파일업로드
    @Multipart
    @POST("customer/customerimgupload.stc")
    Call<CommonUpdateResponse> uploadImage(@Query("fileSeq") String fileSeq, @Query("custNo") String custNo, @Query("chrtNo") String chrtNo,
                                           @Query("filePathNm") String filePathNm, @Part MultipartBody.Part file,
                                           @Part("custNo") RequestBody custNos);


    //x-ray 사진
    @POST("customer/externalprogramxraysearch.stc")
    Call<CustomerXrayResponse> getXrayList(@Query("chrtNo") String chrtNo);

    //이미지 삭제
    @POST("customer/appcustomerimgdelete.stc")
    Call<CommonUpdateResponse> deleteImage(@Query("custNo") String custNo,@Query("fileNm") String fileNm);


    //동의서 템플렛 리스트 받기
    @GET("resource/counselingresources.stc")
    Call<CustomerAgreementTemplateResponse> getAgreementTemplate(@Query("cnsltClssCd") String cnsltClssCd);

    //동의서 리스트
    @GET("customer/customeragreementsearch.stc")
    Call<CustomerAgreeResponse> getCustomerAgreeList(@Query("custNo") String custNo);



    //환자 동의서 등록
    @Multipart
    @POST("customer/customeragreementupload.stc")
    Call<CommonUpdateResponse> regAgreement(@Part MultipartBody.Part file,
                                           @Part("custNo") RequestBody custNos);


    //녹음파일 등록
    @Multipart
    @POST("customer/customeraudioupload.stc")
    Call<CommonUpdateResponse> regRecordFile(@Part MultipartBody.Part file,
                                            @Part("custNo") RequestBody custNos);


}
