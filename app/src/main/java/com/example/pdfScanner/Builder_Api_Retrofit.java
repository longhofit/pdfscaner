package com.example.pdfScanner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Builder_Api_Retrofit extends Application {
    private static final String baseURL="http://192.168.89.141:8000/ui_functions/";
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static String getBaseURL() {
        return baseURL;
    }

    private  static final Retrofit.Builder builder= new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new Builder_Api_Retrofit().okHttpClient());
    private static final Retrofit retrofit=builder.build();
    private static final UserInterface userInterface=retrofit.create(UserInterface.class);
    public static UserInterface getServiceUserInterface ()
    {
        return  userInterface;
    }
    // set timeout
    public OkHttpClient okHttpClient ()
    {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        return  okHttpClient;
    }

    public void showSuccessInActivityNotClose (final Activity thisActivity, String whatSuccess)
    {
        new SweetAlertDialog(thisActivity,SweetAlertDialog.SUCCESS_TYPE)
                .setContentText(whatSuccess)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        thisActivity.startActivity(new Intent(thisActivity,MainActivity.class));
                        CustomIntent.customType(thisActivity,"right-to-left");
                        thisActivity.finish();
                    }
                })
                .show();
    }
    public void showErrorInActivity (final Context context, String whatError)
    {
        SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE)
                .setContentText(whatError)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.show();
    }
}
