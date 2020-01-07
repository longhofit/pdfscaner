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
import java.util.Collections;

public class ListPdfFagment extends Fragment {

    private ListViewAdapterListPdfFiles adapterListPdfFiles;
    ArrayList<File> files;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_pdf, container, false);

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File[] arrayFiles = storageDir.listFiles();
        files = new ArrayList<>();
        for (int i=arrayFiles.length -1; i>=0; i--){
            files.add(arrayFiles[i]);
        }
        final ListView listPdfFiles = view.findViewById(R.id.list_files_pdf);
        adapterListPdfFiles = new ListViewAdapterListPdfFiles(getActivity(), files);
        listPdfFiles.setAdapter(adapterListPdfFiles);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File[] arrayFiles = storageDir.listFiles();
        files.clear();
        for (int i=arrayFiles.length -1; i>=0; i--){
            files.add(arrayFiles[i]);
        }
        adapterListPdfFiles.notifyDataSetChanged();
    }

}
