package com.power_outlet_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;

import androidx.constraintlayout.widget.Placeholder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwitchListAdapter extends BaseAdapter {

    private ArrayList <SwitchObject> objects;
    private LayoutInflater pLayoutInflater;
    private Activity activity;

    SwitchListAdapter(Activity parentActivity, LayoutInflater layoutInflater, ArrayList <SwitchObject> objectList){
        this.pLayoutInflater = layoutInflater;
        this.objects = objectList;
        this.activity = parentActivity;
    }


    @Override
    public View getView(int position, View view, ViewGroup container) {
        view = pLayoutInflater.inflate(R.layout.switch_list_view_layout, null);

        view.setId(Placeholder.generateViewId());
        TextView textView = view.findViewById(R.id.title_Switch_ListView);
        Button offButton = view.findViewById(R.id.offButton_ListView);
        Button onButton = view.findViewById(R.id.onButton_ListView);
        final ImageView imageView = view.findViewById(R.id.imageView_ListView);
        SwitchObject mSwitchObject = objects.get(position);

        textView.setText(Html.fromHtml("<u>" + view.getContext().getString(R.string.switch_name, mSwitchObject.getName()) + "</u>"));

        requestUrl(((ViewGroup)view),position, "LED=STATE");

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewNumber = getViewNumber(v);
                if (viewNumber == -1){
                    return;
                }
                requestUrl(((ViewGroup) v.getParent()), viewNumber, "LED=ON");

            }});

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewNumber = getViewNumber(v);
                if (viewNumber == -1){
                    return;
                }
                requestUrl(((ViewGroup) v.getParent()), viewNumber, "LED=OFF");
            }});


        return view;
    }



    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }




    private int getViewNumber(View currentView){    // Taking the current View and returning the number of the corresponding in the objects list
        ViewGroup singleRow = (ViewGroup) (currentView.getParent().getParent());   // This specific row view group
        ViewGroup allRows = (ViewGroup) (currentView.getParent()).getParent().getParent();     // All the rows view group

        for (int i =0; i < allRows.getChildCount(); i++){   // Lop to get the object number that we are in from the objects list


            if(singleRow == allRows.getChildAt(i)) {
                return i;
            }
        }
        return -1;
    }


    private void requestUrl(final ViewGroup rootView, int objectNumber, final String urlEnding ) {  // request UrlEnding  FromServer



        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.0." + objects.get(objectNumber).getIP()+ "/" + urlEnding;
        Log.i("go","This is url:" + url);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {        // run request on background thread
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("go", "you clicked onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("go", "you clicked onResponse");
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    Log.i("go", "This is the response from the server:" + myResponse);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                    ImageView imageView = rootView.findViewById(R.id.imageView_ListView);
                                    if(myResponse.toLowerCase().contains("LED=ON".toLowerCase())) {
                                        Glide.with(rootView.getContext()).load(R.drawable.lamp_off).into(imageView);

                                        Toast.makeText(rootView.getContext(), "Light is OFF", Toast.LENGTH_SHORT).show();
                                        Log.i("go", "onResponse ON");
                                    }
                                    else if (myResponse.toLowerCase().contains("LED=OFF".toLowerCase())) {
                                        Glide.with(rootView.getContext()).load(R.drawable.lamp_on).into(imageView);
                                        Toast.makeText(rootView.getContext(), "Light is on", Toast.LENGTH_SHORT).show();
                                        Log.i("go", "onResponse OFF");
                                    }
                                    else{
                                        Glide.with(rootView.getContext()).load(R.drawable.lamp_broken).into(imageView);

                                        Toast.makeText(rootView.getContext(), "No connection to Light", Toast.LENGTH_SHORT).show();
                                    }
                        }
                    });
                }
                else{
                    Log.i("go", "This is me going on:");
                }
            }
        });
    }
}

