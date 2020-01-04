package com.example.bartertradeapp.JavaClasses;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bartertradeapp.R;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProductFragment extends Fragment {


    public UpdateProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_product,container,false);



        return view;
    }

}
