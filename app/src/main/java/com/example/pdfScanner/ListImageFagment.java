package com.example.pdfScanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
