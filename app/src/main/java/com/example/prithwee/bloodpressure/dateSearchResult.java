package com.example.prithwee.bloodpressure;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

public class dateSearchResult extends AppCompatActivity {
    String data = null;
    String[] date= null;
    String title = null;
    JSONArray json;
    LineChart chart;
    Legend legend;
    XAxis x;
    YAxis yl;
    YAxis yr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_search_result);
        TextView Title = (TextView) findViewById(R.id.date);
        Intent reciever = getIntent();
        if (reciever.hasExtra(Intent.EXTRA_TEXT)){
            data = reciever.getStringExtra(Intent.EXTRA_TEXT);
            date = reciever.getStringExtra("date").split("/");
        }
        try {
            json = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        title = date[0]+"\n" +date[1]+"  -  "+date[2];
        Title.setText(title);
        chart = (LineChart) findViewById(R.id.linechart);
        chartInit();
        for (int i =0;i<=json.length();i++){
            try {
                addEntry(json.getJSONObject(i).getInt("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void chartInit() {
        Description description =new Description();
        description.setTextColor(Color.parseColor("#d045f7"));
        description.setText("Info...");
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
        legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.EMPTY);
        x = chart.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.parseColor("#d045f7"));
        yl = chart.getAxisLeft();
        yl.setTextColor(Color.parseColor("#d045f7"));
        yl.setAxisMaxValue(1000f);
        yr = chart.getAxisRight();
        yr.setEnabled(false);
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
            chart.moveViewToX(data.getEntryCount()-1);

        }
    }
    private LineDataSet createSet()
    {
        LineDataSet set = new LineDataSet(null,"Record Set");
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
}
