package com.example.pdfScanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnCamera, btnGallery;

    public static String currentPhotoPath;

    public static String currentFilePath;

    private Bitmap currentBitmapImage = null;

    public Uri currentUri = null;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int RESULT_LOAD_IMG = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Ask for permission
        makeRequest();

        tabLayout = findViewById(R.id.tablayout_tourdetail_tab);
        viewPager = findViewById(R.id.viewpaper_tourdetail_mainview);


        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

        ListImageFagment listImageFagment = new ListImageFagment();
        ListPdfFagment listPdfFagment = new ListPdfFagment();

        adapter.AddFragment(listImageFagment, "Images");
        adapter.AddFragment(listPdfFagment, "Documents");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //Get list of archived files
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

//        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
//        Log.d("Files", "Path: " + path);


       /* //List fragment
        Fragment fragmentfirst = new FragmentPicturesItem();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentfirst).show(fragmentfirst).commit();*/
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

    private File createImageCroppedFile() throws IOException {
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

    private File createTempImage() throws IOException {
        // Create an image file name
        String imageFileName = "temp.png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);
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
                photoFile = createTempImage();
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

                File f = new File(currentPhotoPath);

                currentUri = Uri.fromFile(f);

                if (currentUri != null)
                {
                    CropImage.activity(currentUri)
                            .start(this);
                }

            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
        }
        if (requestCode == RESULT_LOAD_IMG)
        {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();

                    currentUri = data.getData();

                    if (currentUri != null)
                    {
                        CropImage.activity(currentUri)
                                .start(this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                    currentBitmapImage = BitmapFactory.decodeStream(imageStream);

                    //Save image to application external memory
                    try
                    {
                        File photoFile = null;
                        photoFile = createImageCroppedFile();
                        OutputStream stream = null;
                        stream = new FileOutputStream(photoFile);
                        currentBitmapImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        stream.close(); }
                    catch (IOException e) // Catch the exception
                    {
                        e.printStackTrace();
                    }

                    /*try {
                        currentFilePath = createPdfFile();
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        intent.putExtra("filePath", currentFilePath);
                        String filename = "bitmap.png";
                        FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                        currentBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        //Cleanup
                        stream.close();
                        currentBitmapImage.recycle();
                        intent.putExtra("bitMap",  filename);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    OpenScanAsync a = new OpenScanAsync();
                    a.execute();

                    //runTextRecognition(currentBitmapImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    // use a thread to open activity_scan
    private class OpenScanAsync extends AsyncTask<Object, Void, Void> {

        private String createPdfFile() throws IOException {
            Document document = new Document();
            String directoryPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
            //String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String FileDir = directoryPath+ "/pdf_" + timeStamp + ".pdf";
            try {
                PdfWriter.getInstance(document, new FileOutputStream(FileDir)); //  Change pdf's name.
                document.open();
                com.itextpdf.text.Image image;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                currentBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                image.scalePercent(scaler);
                image.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER | com.itextpdf.text.Image.ALIGN_TOP);
                document.add(image);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            document.close();
            return FileDir;
        }

        @Override
        protected Void doInBackground(Object... Object) {
            try {
                currentFilePath = createPdfFile();
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("filePath", currentFilePath);
                String filename = "bitmap.png";
                FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                currentBitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //Cleanup
                stream.close();
                currentBitmapImage.recycle();
                intent.putExtra("bitMap",  filename);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}
