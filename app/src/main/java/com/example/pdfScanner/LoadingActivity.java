package com.example.pdfScanner;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
ProgressBar progressBar;
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar=findViewById(R.id.progress_animation);
        textView=findViewById(R.id.tv_loading);
        progressBar.setMax(100);
        progressBar.setScaleY(3f);
        progressbar_animation();
    }

    public void progressbar_animation ()
    {
        progressbar_animation animation=new progressbar_animation(this, progressBar,textView,0f,100f);
        animation.setDuration(3500);
        progressBar.setAnimation(animation);
    }
}
