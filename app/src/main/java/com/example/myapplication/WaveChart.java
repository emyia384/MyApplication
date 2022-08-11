package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.sql.DBHelper;
import com.example.myapplication.util.Datapoints;
import com.example.myapplication.util.Datastreams;
import com.example.myapplication.util.JsonRootBean;
import com.example.myapplication.util.WaveProgress;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WaveChart  extends AppCompatActivity {

    WaveProgress waveProgress;
    private static final String DeviceID = "959849707";
    private static final String Apikey = "k9QnGxbAbnESIfH=CoVSMyO0ZlM=";
    private static final String key4="CAR";

    Button mBtnReturn;   // 返回主页

    private void parseJSONWithGSON(String jsonData) {
        JsonRootBean app = new Gson().fromJson(jsonData, JsonRootBean.class);
        List<Datastreams> streams = app.getData().getDatastreams();
        List<Datapoints> points = streams.get(0).getDatapoints();
        int count = app.getData().getCount();//获取数据的数量
        for (int i = 0; i < points.size(); i++) {
            String time=points.get(i).getAt();
            String value=points.get(i).getValue();
            Log.w("www","time="+time);
            Log.w("www","value="+value);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wavechart_activity);
        waveProgress=findViewById(R.id.wave_progress_bar);
        mBtnReturn = findViewById(R.id.demo_return);

        Timer timer=new Timer(true);
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Get();
            }
        };
        timer.schedule(task,5000,10000);
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WaveChart.this,MainActivity.class));
            }
        });
    }

    public void Get() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    OkHttpClient clientSun = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key4).header("api-key", Apikey).build();
                    Response response = clientSun.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);


                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String value=points.get(0).getValue();
                    float num=(float) (Integer.parseInt(value));
                    waveProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            waveProgress.setValue(num);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
