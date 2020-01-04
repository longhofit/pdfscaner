package com.example.pdfScanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import maes.tech.intentanim.CustomIntent;

public class progressbar_animation extends Animation {
    private Context context;
    private ProgressBar progressBar;
    private TextView textView;
    private float from,to;

    public progressbar_animation(Context context, ProgressBar progressBar, TextView textView, float from, float to) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value =from+(to-from)*interpolatedTime;
        progressBar.setProgress((int)value);
        textView.setText((int)value+" %");
        if(value==to)
        {
            context.startActivity(new Intent(context,LoginActivity.class));
            CustomIntent.customType(context,"fadein-to-fadeout");
            Activity activity=(Activity)context;
            activity.finish();
        }
    }
}
