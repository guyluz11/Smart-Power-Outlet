package com.power_outlet_app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SwitchObject {
    String mName;
    int mIP;
    AsyncTask requestTas;

    SwitchObject(String switchName, int IP){
        this.mName = switchName;
        this.mIP = IP;
    }


    public String getName(){
        return mName;
    }

    public int getIP(){
        return mIP;
    }


    public void setRequestTas(AsyncTask request){
//        if (requestTas != null){
//            request.cancel(true);
//        }
        requestTas = request;
    }



}
