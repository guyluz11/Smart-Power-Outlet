package com.power_outlet_app;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class Switch extends Fragment {
    private View mView;


    private String urlString;
    private ConstraintLayout constraintLayout;
    private String deviceIp = "0.0.0.0";     // TODO: insert device specific ip
    ListView listView;
    ArrayList<SwitchObject> switchList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_toggle_lights, container, false);   // get the view of the fragment (do not delete or move)
        constructViews();


        constructListOfSwitchObjects();
        listViewConfig();


        return mView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//
//        getMenuInflater().inflate(R.menu.workers_screen_top_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void constructViews(){       // initialize all the variables in an organized way
        constraintLayout = mView.findViewById(R.id.constraintLayout_ToggleLights);
        listView = mView.findViewById(R.id.listView_ToggleLights);
        NavigationActivity navigationActivity =((NavigationActivity)getActivity());

        navigationActivity.setChildFragment(mView);
        navigationActivity.showMenu(true);     // show option menu
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void constructListOfSwitchObjects(){
        for (int i = 0; i<2; i++)
        {
            if(i==0) {
                switchList.add(new SwitchObject("light " + i, 20));
            }
            else{

                switchList.add(new SwitchObject("light " + i, 21));
            }
        }
    }


    private void listViewConfig(){
        SwitchListAdapter switchListAdapter = new SwitchListAdapter(getActivity() ,getLayoutInflater(), switchList);
        listView.setAdapter(switchListAdapter);
    }




}
