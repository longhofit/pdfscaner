package com.example.pdfScanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListViewAdapterListImageFiles extends BaseAdapter {

    Activity activity;
    private ArrayList<File> files;

    public ListViewAdapterListImageFiles(Activity activity, ArrayList<File> files){
        this.activity = activity;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_files_item_layout, null);

        TextView fileName = convertView.findViewById(R.id.file_name);
        TextView lastModified = convertView.findViewById(R.id.file_lastModified);
        ImageView fileImage = convertView.findViewById(R.id.file_image_view);

        fileName.setText(files.get(position).getName());
        String stringLastModified = DateTimeTool.convertMillisToDateTime(files.get(position).lastModified());
        lastModified.setText(stringLastModified);

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 0;
        //FileOutputStream outputStream = new FileOutputStream("location to read");

        DecodeTask task = (DecodeTask)fileImage.getTag(R.id.file_image_view);
        if(task != null) {
            task.cancel(true);
        }
        fileImage.setClipToOutline(true);
        fileImage.setImageBitmap(null);
        DecodeTask task2 = new DecodeTask(fileImage);
        task2.execute(files.get(position).getPath());
        fileImage.setTag(R.id.file_image_view, task2);


        return convertView;
    }
}
