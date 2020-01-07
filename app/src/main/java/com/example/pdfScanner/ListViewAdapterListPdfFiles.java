package com.example.pdfScanner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ListViewAdapterListPdfFiles extends BaseAdapter {

    Activity activity;
    private ArrayList<File> files;

    public ListViewAdapterListPdfFiles(Activity activity, ArrayList<File> files){
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
        convertView = inflater.inflate(R.layout.list_files_pdf_item_layout, null);

        TextView fileName = convertView.findViewById(R.id.file_name);
        TextView lastModified = convertView.findViewById(R.id.file_lastModified);

        fileName.setText(files.get(position).getName());
        String stringLastModified = DateTimeTool.convertMillisToDateTime(files.get(position).lastModified());
        lastModified.setText(stringLastModified);

        return convertView;
    }
}
