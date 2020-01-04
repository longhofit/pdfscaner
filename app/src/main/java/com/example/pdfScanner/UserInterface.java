package com.example.pdfScanner;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserInterface {
    @FormUrlEncoded
    @POST("login.php")
    Call<reponse_obj> login(@Field("email")String email,
                             @Field("password")String password);
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> register(@Field("name")String name,
                                @Field("email")String email,
                                @Field("password")String password);
}
