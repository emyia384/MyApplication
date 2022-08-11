package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.sql.DBHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_2 extends Fragment {

    SlideSwitch sSwitch1,sSwitch2,sSwitch3;

    private static final String DeviceID = "959849707";
    private static final String Apikey = "k9QnGxbAbnESIfH=CoVSMyO0ZlM=";

    public void deleteDate() {
        DBHelper dbHelper = new DBHelper(getActivity(),2);
        //1.得到连接
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM data");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建Fragment布局
        View view = inflater.inflate(R.layout.my_fragment2,container,false);
        sSwitch1 = (SlideSwitch) view.findViewById(R.id.slide_switch1);
        sSwitch2 = (SlideSwitch) view.findViewById(R.id.slide_switch2);
        sSwitch3 = (SlideSwitch) view.findViewById(R.id.slide_switch3);

        deleteDate();

        sSwitch1.setOnStateChangedListener(new SlideSwitch.OnStateChangedListener(){
            @Override
            public void onStateChanged(boolean state) {
                // TODO Auto-generated method stub
                if(true == state)
                {
                    Toast.makeText(getActivity(), "小灯开关已打开", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:1}\n");
                }
                else
                {
                    Toast.makeText(getActivity(), "小灯开关已关闭", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:0}\n");
                }
            }

        });


        sSwitch2.setOnStateChangedListener(new SlideSwitch.OnStateChangedListener(){
            @Override
            public void onStateChanged(boolean state) {
                // TODO Auto-generated method stub
                if(true == state)
                {
                    Toast.makeText(getActivity(), "风扇开关已打开", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:1}\n");
                }
                else
                {
                    Toast.makeText(getActivity(), "风扇开关已关闭", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:1}\n");
                }
            }

        });


        sSwitch3.setOnStateChangedListener(new SlideSwitch.OnStateChangedListener(){
            @Override
            public void onStateChanged(boolean state) {
                // TODO Auto-generated method stub
                if(true == state)
                {
                    Toast.makeText(getActivity(), "水泵开关已打开", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:1}\n");
                }
                else
                {
                    Toast.makeText(getActivity(), "水泵开关已关闭", Toast.LENGTH_SHORT).show();
                    Post("{“LED0”:1}\n");
                }
            }

        });


        return view;
    }


    public void Post(final String val){
    new Thread(new Runnable() {
        @Override
        public void run() {
            String cmd=val;
            String body=String.format(cmd);
            String url = String.format("http://api.heclouds.com/cmds?device_id="+DeviceID);
            OkHttpClient client = new OkHttpClient();
            //数据格式从官方文档看，type用5情况
            //String data=new String(",;light,12");
            //发送type写法
            //RequestBody requestBody = RequestBody.create(data,MediaType.get("application/json"));
            RequestBody requestBody=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),body);
            Request request = new Request.Builder()
                    .url(url)
                    .header("api-key", Apikey)
                    .post(requestBody)
                    .build();

            Call call=client.newCall(request);//获取响应
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String insertStr="POST请求失败";
                    //Toast.makeText(MainActivity.this,insertStr,Toast.LENGTH_SHORT).show();
                    System.out.println("No!!!");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    System.out.println("Yes!!!");
                }
            });
        }
    }).start();
}

}