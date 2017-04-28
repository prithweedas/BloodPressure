package com.example.prithwee.bloodpressure;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class searchByData extends AppCompatActivity {
    EditText date,time1,time2;
    String Date,Time1,Time2;
    Button search;
    Intent results;
    Runnable r = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_data);
        date = (EditText) findViewById(R.id.Date);
        time1 = (EditText) findViewById(R.id.time1);
        time2 = (EditText) findViewById(R.id.time2);
        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });

    }

    private void searchData() {
        Date = date.getText().toString();
        Time1 = time1.getText().toString();
        Time2 = time2.getText().toString();
        final String query = String.format("GetValuesByDateTime/%s/%s/%s",Date,Time1,Time2);
        Handler handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                NetworkUtils.get(query, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        results = new Intent(getApplicationContext(),dateSearchResult.class);
                        results.putExtra(Intent.EXTRA_TEXT,response.toString());
                        results.putExtra("date",Date+"/"+Time1+"/"+Time2);
                        startActivity(results);
                    }
                });
            }
        };
        if (isNetworkAvailable()){
            handler.postDelayed(r,0);
        }else{
            Toast t = Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT);
            t.show();
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
