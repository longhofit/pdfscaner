package com.example.pdfscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnCamera, btnGallery;

    public static String currentPhotoPath;

    private Bitmap currentBitmapImage = null;

    public Uri currentUri = null;



    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int RESULT_LOAD_IMG = 101;
    private static final int PIC_CROP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask for permission
        makeRequest();

        //Button take photo by camera
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);

        //Button get photo from gallery
        btnGallery = findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(this);



    }

    protected void makeRequest() {
        int permission_read = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_write = ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_camera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        if (permission_camera != PackageManager.PERMISSION_GRANTED || permission_read != PackageManager.PERMISSION_GRANTED || permission_write != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnCamera:
            {
                try{
                    //Take photo
                    //onTakePhoto();
                    dispatchTakePictureIntent();
                } catch (Exception e) {
                    System.out.println("exception camera");
                }
                break;
            }

            case R.id.btnGallery:
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_ID_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                /*Bitmap bp = (Bitmap) data.getExtras().get("data");
                ImageView test = (ImageView) this.findViewById(R.id.testIMG);
                test.setImageBitmap(bp);*/


                File f = new File(currentPhotoPath);
                currentUri = Uri.fromFile(f);
                /*try {
                    performCrop(currentUri);
                } catch (Exception e){
                    int a;
                };*/
                /*File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);

                try {
                    currentBitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                currentBitmapImage = readBitmapAndScale(currentPhotoPath);
                runTextRecognition(currentBitmapImage);
                //OCR


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == RESULT_LOAD_IMG)
        {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                    currentBitmapImage = BitmapFactory.decodeStream(imageStream);
                    //scaleDown(currentBitmapImage,3000000,true);
                    runTextRecognition(currentBitmapImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap readBitmapAndScale(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Chỉ đọc thông tin ảnh, không đọc dữ liwwuj
        BitmapFactory.decodeFile(path,options); //Đọc thông tin ảnh
        options.inSampleSize = 4; //Scale bitmap xuống 4 lần
        options.inJustDecodeBounds=false; //Cho phép đọc dữ liệu ảnh ảnh
        return BitmapFactory.decodeFile(path,options);
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
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            TextView resultView = (TextView) findViewById(R.id.result_test);
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
        TextView resultView = (TextView) findViewById(R.id.result_test);
        resultView.setText(sb.toString());
    }

}
