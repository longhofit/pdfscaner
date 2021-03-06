package com.example.pdfScanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ListImageFagment extends Fragment {

    private ListViewAdapterListImageFiles adapterListImageFiles;
    ArrayList<File> files;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_list_image, container, false);

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] arrayFiles = storageDir.listFiles();
        files = new ArrayList<>();
        for (int i=arrayFiles.length -1; i>=0; i--){
            files.add(arrayFiles[i]);
        }

        final ListView listImageFiles = view.findViewById(R.id.list_files);
        adapterListImageFiles = new ListViewAdapterListImageFiles(getActivity(), files);

        listImageFiles.setAdapter(adapterListImageFiles);

        listImageFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri path = Uri.fromFile(files.get(position));
                Intent imgOpenintent = new Intent(Intent.ACTION_VIEW);
                imgOpenintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                imgOpenintent.setDataAndType(path, "image/jpeg");
                Log.d("OUUUUUT", "OUTTTTTT");
                try {
                    Log.d("AAAA", "AAAAAAAA");
                    getActivity().startActivity(imgOpenintent);
                }
                catch (ActivityNotFoundException e) {

                }
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] arrayFiles = storageDir.listFiles();
        files.clear();
        for (int i=arrayFiles.length -1; i>=0; i--){
            files.add(arrayFiles[i]);
        }
        adapterListImageFiles.notifyDataSetChanged();

    }

}
