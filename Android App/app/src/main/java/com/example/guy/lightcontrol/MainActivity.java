package com.example.guy.lightcontrol;

import android.content.Context;
import android.os.AsyncTask;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String urlString;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.ConstLayout);
        Button onButton = findViewById(R.id.OnButton);
        Button offButton = findViewById(R.id.Off_Button);

        urlString  = "http://10.0.0.20/LED=STATE";
        new RequestTas(getApplicationContext(), constraintLayout).execute(urlString);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                urlString = "http://10.0.0.20/LED=ON";
                new RequestTas(getApplicationContext(), constraintLayout).execute(urlString);
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                urlString  = "http://10.0.0.20/LED=OFF";
                new RequestTas(getApplicationContext(), constraintLayout).execute(urlString);
            }

        });

    }
}





class RequestTas extends AsyncTask<String, String, String> {      // new http
    private HttpURLConnection urlConnection = null;

    private View rootView;
    private Context mContext;

    RequestTas(Context context1, View rootView1){
        this.mContext = context1;
        this.rootView = rootView1;
    }


    @Override
    protected String doInBackground(String... uri) {
        URL url;
        try {
            url = new URL(uri[0]);
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            StringBuilder fromServer = new StringBuilder();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                fromServer.append(current);
            }

            Log.i("go", "Text from server:" + fromServer);
            if (fromServer.toString().contains("Off"))
                return "OFF";
            else
                return "ON";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return "Error";
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        ImageView outSideLight_imageView = rootView.findViewById(R.id.outSideLight_imageView);
        if (result.equals("ON")){
            outSideLight_imageView.setImageResource(R.drawable.lamp_on);
            Toast.makeText(mContext, "Light is On", Toast.LENGTH_LONG).show();
        }
        else if (result.equals("OFF")){
            outSideLight_imageView.setImageResource(R.drawable.lamp_off);
            Toast.makeText(mContext, "Light is Off", Toast.LENGTH_LONG).show();
        }
        else{
            outSideLight_imageView.setImageResource(R.drawable.lamp_broken);
            Toast.makeText(mContext, "Signal Error" +
                    " The device is offline", Toast.LENGTH_LONG).show();
        }
    }

}
