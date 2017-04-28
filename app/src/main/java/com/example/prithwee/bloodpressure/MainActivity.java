package com.example.prithwee.bloodpressure;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {
    JobScheduler mjobScheduler;
    boolean isNotice = false;
    int noticeId = 1;
    int LastVal;
    boolean notfirsttime = false;
    int[] valuelist = new int[50];
    int i;
    LineChart chart;
    Runnable run = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        setContentView(R.layout.activity_main);
        //final TextView value = (TextView) findViewById(R.id.last);
        chart = (LineChart) findViewById(R.id.chart);
        if (isNetworkAvailable()){
            chart.setNoDataText("Wait");
            getData(handler);
            handler.postDelayed(run,0);
        }else{
            chart.setNoDataText("Something went Wrong!");
        }
        Description description =new Description();
        description.setTextColor(Color.parseColor("#d045f7"));
        description.setText("Data Graph.....");
        chart.setDescription(description);
        chart.setHighlightPerDragEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        LineData data = new LineData();
        data.setValueTextColor(Color.parseColor("#d045f7"));
        chart.setData(data);
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.EMPTY);
        XAxis x = chart.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.parseColor("#d045f7"));
        YAxis yl = chart.getAxisLeft();
        yl.setTextColor(Color.parseColor("#d045f7"));
        yl.setAxisMaxValue(1000f);
        YAxis yr = chart.getAxisRight();
        yr.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_by_date){
            Intent Search = new Intent(this,searchByData.class);
            startActivity(Search);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(final Handler handler){
        run = new Runnable() {
            @Override
            public void run() {
                NetworkUtils.get("GetAllValues", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // If the response is JSONObject instead of expected JSONArray
                        //progress.setVisibility(View.INVISIBLE);
                        //value.setText(response.toString());
                        int val=0;
                        try {
                            val = response.getJSONObject(0).getInt("value");
                            for(i=0;i<response.length();i++){
                                valuelist[i] = response.getJSONObject(i).getInt("value");
                            }
                            if (!notfirsttime){
                                for(i=response.length()-1;i>=0;i--){
                                    addEntry(valuelist[i]);
                                }
                                notfirsttime = true;
                            }else if (notfirsttime && LastVal != val){
                                addEntry(val);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (LastVal != val && val > 400 &&isNotice == true){
                            notification(val);
                        }else{
                            isNotice = true;
                        }
                        LastVal = val;
                    }
                });
                handler.postDelayed(this,1500);
            }
        };
    }
    private  void addEntry(int val)
    {
        LineData data = chart.getData();
        if (data != null){
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount()+1,val), 0);
            chart.notifyDataSetChanged();
            chart.setVisibleXRange(0,10);
            chart.moveViewToX(data.getEntryCount()+5);

        }
    }
    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null,"IOT");
        set.setDrawCircles(true);
        set.setCircleRadius((float) 0.2);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(Color.parseColor("#d045f7"));
        set.setLineWidth(1.2f);
        set.setFillAlpha(Color.parseColor("#d045f7"));
        set.setFillColor(Color.parseColor("#d045f7"));
        set.setHighLightColor(Color.parseColor("#d045f7"));
        set.setValueTextColor(Color.parseColor("#d045f7"));
        set.setValueTextSize(10f);
        return set;
    }
    private void notification(int val) {
        NotificationManager nm;
        NotificationCompat.Builder notice = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                                .setContentTitle("Danger!!")
                                                .setContentText(val + "  BP exceeded Optimal limit!")
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setTicker("Warning").setAutoCancel(true);
        Intent dangerIntent = new Intent(this,danger.class);
        TaskStackBuilder task = TaskStackBuilder.create(this);
        task.addParentStack(MainActivity.class);
        task.addNextIntent(dangerIntent);
        PendingIntent pIntent = task.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        notice.setContentIntent(pIntent);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(noticeId,notice.build());
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
