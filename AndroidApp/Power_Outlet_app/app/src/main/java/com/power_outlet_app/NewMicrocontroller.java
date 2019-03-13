package com.power_outlet_app;



import android.os.Bundle;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;


public class NewMicrocontroller extends Fragment {
    private View myView;
    private EditText wifiName_editText;
    private EditText wifiPassword_editText;
    private Button newMicrocontroller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_new_microcontroller, container, false);  // get the view of the fragment (do not delete or move)
        constructViews();




        newMicrocontroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUrl(wifiName_editText.getText().toString(), wifiPassword_editText.getText().toString());
            }
        });

        return myView;
    }


    private void constructViews(){       // initialize all the variables in an organized way
        newMicrocontroller = myView.findViewById(R.id.addDevice_New_Microcontroller);
        wifiName_editText = myView.findViewById(R.id.wifiNameEditText_NewMicrocontroller);
        wifiPassword_editText = myView.findViewById(R.id.wifiPasswordEditText_NewMicrocontroller);

    }



    private void requestUrl(String wifiName, String wifiPassword) {  // request UrlEnding  FromServer
        OkHttpClient client = new OkHttpClient();
        String ip = "0.0.0.0";      //TODO: insert your home ip
        String url = "http://" + ip + "/?Network_name=" + wifiName + "&Network_password=" + wifiPassword;
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No connection to Light", Toast.LENGTH_SHORT).show();
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
