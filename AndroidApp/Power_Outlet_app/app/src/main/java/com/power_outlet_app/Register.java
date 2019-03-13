package com.power_outlet_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Register extends Fragment {
    private View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_register, container, false);   // get the view of the fragment (do not delete or move)
        constructViews();

        return myView;
    }

    private void constructViews(){       // initialize all the variables in an organized way


    }
}
