package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.sql.DBHelper;
import com.example.myapplication.util.Datapoints;
import com.example.myapplication.util.Datastreams;
import com.example.myapplication.util.JsonRootBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_1 extends Fragment {

    private static final String key1="AIR_TEM";
    private static final String key2="AIR_HUM";
    private static final String key3="LIGHT";
    private static final String key4="CAR";
    private static final String key5="SOIL_HUM";
    Button m_start;
    String value1,value2,value3,value4,value5;

    private static final String DeviceID = "959849707";
    private static final String Apikey = "k9QnGxbAbnESIfH=CoVSMyO0ZlM=";

    Button button1,button2,button3,button4;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建Fragment布局
        View view = inflater.inflate(R.layout.my_fragment1,container,false);
        //deleteDate();

        //启动接收数据
        m_start = view.findViewById(R.id.button);
        m_start.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                Toast.makeText(getActivity(),"正在从云平台获取数据", Toast.LENGTH_SHORT).show();
                Timer timer=new Timer(true);
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        Get();
                    }
                };
                timer.schedule(task,5000,10000);
            }
        });

        button1=view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BarChart.class);
                startActivity(i);
            }
        });


        button2=view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LineChart.class);
                startActivity(i);
            }
        });

        button3=view.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CircleChart.class);
                startActivity(i);
            }
        });

        button4=view.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), WaveChart.class);
                startActivity(i);
            }
        });

        return view;
    }


    public void deleteDate() {
        DBHelper dbHelper = new DBHelper(getActivity(),2);
        //1.得到连接
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM data");
    }

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

    public void Get() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient clientTemp = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key1)
                            .header("api-key", Apikey)
                            .build();
                    Response response = clientTemp.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    value1=points.get(0).getValue();
//                    data1.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            data1.setText(String.format("温度：%s ℃",value1));
//                        }
//                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    OkHttpClient clientHumi = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key2).header("api-key", Apikey).build();
                    Response response = clientHumi.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);


                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    value2=points.get(0).getValue();
//                    data2.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            data2.setText(String.format("湿度：%s %%",value2));
//                        }
//                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    OkHttpClient clientSun = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key3).header("api-key", Apikey).build();
                    Response response = clientSun.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);


                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    value3=points.get(0).getValue();
//                    data3.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            data3.setText(String.format("光强：%s lux",value3));
//                        }
//                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    OkHttpClient clientSun = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key4).header("api-key", Apikey).build();
                    Response response = clientSun.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);


                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    value4=points.get(0).getValue();
//                    data4.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            data4.setText(String.format("CO2：%s %%",value4));
//                        }
//                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    OkHttpClient clientSun = new OkHttpClient();
                    Request request = new Request.Builder().url("https://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key5).header("api-key", Apikey).build();
                    Response response = clientSun.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);


                    JsonRootBean app=new Gson().fromJson(responseData,JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    value5=points.get(0).getValue();
//                    data5.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            data5.setText(String.format("土壤湿度：%s %%",value5));
//                        }
//                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DBHelper dbHelper = new DBHelper(getActivity(),2);
                //1.得到连接
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                //2.执行insert
                ContentValues values = new ContentValues();
                values.put("Tem",value1);
                values.put("Humi",value2);
                values.put("Light",value3);
                values.put("Co2",value4);
                values.put("Soil",value5);

                //返回插入的id
                long id = sqLiteDatabase.insert("data",null,values);

                System.out.println("-----------------------------------------------1111");
                //3.关闭连接
                sqLiteDatabase.close();
            }
        }).start();

    }
}