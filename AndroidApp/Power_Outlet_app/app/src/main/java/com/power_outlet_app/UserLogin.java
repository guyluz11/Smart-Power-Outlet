package com.power_outlet_app;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserLogin extends Fragment {
    private View myView;
    private Button loginButton;
    private TextView newAccount;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_user_login, container, false);   // get the view of the fragment (do not delete or move)
        constructViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(myView).navigate(R.id.action_userLogin_to_toggleLights);
            }
        });


        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(myView).navigate(R.id.action_userLogin_to_register);
            }
        });

        return myView;
    }



    private void constructViews(){       // initialize all the variables in an organized way
        loginButton = myView.findViewById(R.id.loginButton_UserLogin);
        newAccount = myView.findViewById(R.id.createNewAccount_UserLogin);
    }
}







