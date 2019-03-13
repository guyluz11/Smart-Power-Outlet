package com.power_outlet_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class NavigationActivity extends AppCompatActivity {

    MenuItem topMenu;
    private View childFragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        constructViews();

    }


    private void constructViews(){       // initialize all the variables in an organized way

    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        topMenu = menu.findItem(R.id.addWorker);
        topMenu.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.devices_status_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addWorker){
            showMenu(false);
            Navigation.findNavController(childFragmentView).navigate(R.id.action_toggleLights_to_newMicrocontroller);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setChildFragment(View fragmentView){
        childFragmentView = fragmentView;

    }

    public void showMenu(boolean show){     // show = true show to menu show = false don't show
        this.topMenu.setVisible(show);
    }


    @Override
    public void onBackPressed() {
        showMenu(false);
        super.onBackPressed();
    }


}
