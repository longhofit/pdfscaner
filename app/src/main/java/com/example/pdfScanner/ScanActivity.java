package com.example.pdfScanner;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import java.io.FileInputStream;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    String currentFilePath;
    Bitmap currentBitmapImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().hide();
        ImageView imageView = findViewById(R.id.pdf_view);
        ImageButton button = findViewById(R.id.ocrBtn);
        Intent intent = getIntent();
        currentFilePath = intent.getStringExtra("filePath");
        String filename = getIntent().getStringExtra("bitMap");
        try {
            FileInputStream is = this.openFileInput(filename);
            currentBitmapImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(currentBitmapImage);
        button.setOnClickListener(this);
    }

    private void runTextRecognition(Bitmap mSelectedImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(
                new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText texts) {
                        processTextRecognitionResult(texts);
                    }
                });
    }
    @SuppressLint("SetTextI18n")
    private void processTextRecognitionResult(FirebaseVisionText texts) {
        final Dialog dialog = new Dialog(ScanActivity.this);
        dialog.setContentView(R.layout.ocr_layout);
        dialog.show();
        TextView resultView = dialog.findViewById(R.id.ocr_view);
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {

            resultView.setText("No text found!");
            return;
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) sb.append(elements.get(k).getText()).append(" ");
                sb.append("\n");
            }
            sb.append("\n");
        }
        resultView.setText(sb.toString());
        String finalResult;
        finalResult = sb.toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("coppiedText", finalResult);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Coppied to clipboard", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ocrBtn:
            {
                runTextRecognition(currentBitmapImage);
            }
        }
    }
}
