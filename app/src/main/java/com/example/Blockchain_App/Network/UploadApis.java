package com.example.Blockchain_App.Network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("register")
    // uploadImage -> Register
    Call<RequestBody> Register(@Part MultipartBody.Part part,
                               @Part("username") RequestBody requestBody1,
                               @Part("reqid") RequestBody requestBody2);  //MultipartBody.Part part

    @Multipart
    @POST("response")
        // Button -> Response
    Call<RequestBody> Response(@Part("username") RequestBody requestBody1,
                             @Part("reqID") RequestBody requestBody2,
                             @Part("response") RequestBody requestBody3);  //MultipartBody.Part part

    @Multipart
    @POST("verify")
    // verification route
    Call<RequestBody> Verify(@Part MultipartBody.Part part, 
                             @Part("username") RequestBody requestBody2);  //MultipartBody.Part part
}