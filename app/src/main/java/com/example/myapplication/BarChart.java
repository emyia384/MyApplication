package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.sql.DBHelper;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BarChart extends AppCompatActivity implements View.OnClickListener {

    public static final int MSG_START = 1; // handler消息，开始添加点

    // 折线编号
    public static final int LINE_NUMBER_1 = 0;


    private DemoHandler mDemoHandler; // 自定义Handler
    private Random mRandom = new Random(); // 随机产生点
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.00");   // 格式化浮点数位两位小数


    Button mBtnStart;   // 开始添加点
    Button mBtnPause;   // 暂停添加点
    Button mBtnReturn;   // 返回主页

    CheckBox mCheckBox1;
    List<CheckBox> mCheckBoxList = new ArrayList<>();

    com.github.mikephil.charting.charts.BarChart mLineChart; // 折线表，存线集合
    BarData mLineData; // 线集合，所有折现以数组的形式存到此集合中
    XAxis mXAxis; //X轴
    YAxis mLeftYAxis; //左侧Y轴
    YAxis mRightYAxis; //右侧Y轴
    Legend mLegend; //图例
    LimitLine mLimitline; //限制线

    //  Y值数据链表
    List<Float> mList1 = new ArrayList<>();

    // Chart需要的点数据链表
    List<BarEntry> mEntries1 = new ArrayList<>();

    // LineDataSet:点集合,即一条线
    BarDataSet mLineDataSet1 = new BarDataSet(mEntries1, "空气温度");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barchart_activity);
        mDemoHandler = new DemoHandler(this);
        initView();
        initLineChart();
    }


    /**
     * 功能：初始化基本控件，button，checkbox
     */
    public void initView() {
        mBtnStart = findViewById(R.id.demo_start);
        mBtnPause = findViewById(R.id.demo_pause);
        mBtnReturn = findViewById(R.id.demo_return);


        mCheckBox1 = findViewById(R.id.demo_checkbox1);

        mCheckBoxList.add(mCheckBox1);

        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnReturn.setOnClickListener(this);

        mCheckBox1.setOnClickListener(this);
    }

    /**
     * 功能：初始化LineChart
     */
    public void initLineChart() {
        mLineChart = findViewById(R.id.demo_linechart);
        mXAxis = mLineChart.getXAxis(); // 得到x轴
        mLeftYAxis = mLineChart.getAxisLeft(); // 得到侧Y轴
        mRightYAxis = mLineChart.getAxisRight(); // 得到右侧Y轴
        mLegend = mLineChart.getLegend(); // 得到图例
        mLineData = new BarData();
        mLineChart.setData(mLineData);

        // 设置图标基本属性
        setChartBasicAttr(mLineChart);

        // 设置XY轴
        setXYAxis(mLineChart, mXAxis, mLeftYAxis, mRightYAxis);

        // 添加线条
        initLine();

        // 设置图例
        createLegend(mLegend);

        // 设置MarkerView
        setMarkerView(mLineChart);
    }


    /**
     * 功能：设置图标的基本属性
     * @param lineChart
     */
    void setChartBasicAttr(com.github.mikephil.charting.charts.BarChart lineChart) {
        /***图表设置***/
        lineChart.setDrawGridBackground(false); //是否展示网格线
        lineChart.setDrawBorders(true); //是否显示边界
        lineChart.setDragEnabled(true); //是否可以拖动
        lineChart.setScaleEnabled(true); // 是否可以缩放
        lineChart.setTouchEnabled(true); //是否有触摸事件
        //设置XY轴动画效果
        //lineChart.animateY(2500);
        lineChart.animateX(1500);
    }

    /**
     * 功能：设置XY轴
     */
    void setXYAxis(com.github.mikephil.charting.charts.BarChart lineChart, XAxis xAxis, YAxis leftYAxis, YAxis rightYAxis) {
        /***XY轴的设置***/
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X轴设置显示位置在底部
        xAxis.setAxisMinimum(0f); // 设置X轴的最小值
        xAxis.setAxisMaximum(50); // 设置X轴的最大值
        xAxis.setLabelCount(50, false); // 设置X轴的刻度数量，第二个参数表示是否平均分配
        xAxis.setGranularity(1f); // 设置X轴坐标之间的最小间隔
        lineChart.setVisibleXRangeMaximum(5);// 当前统计图表中最多在x轴坐标线上显示的总量
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(100f);
        rightYAxis.setAxisMaximum(100f);
        leftYAxis.setGranularity(1f);
        rightYAxis.setGranularity(1f);
        leftYAxis.setLabelCount(20);
        lineChart.setVisibleYRangeMaximum(100, YAxis.AxisDependency.LEFT);// 当前统计图表中最多在Y轴坐标线上显示的总量
        lineChart.setVisibleYRangeMaximum(50, YAxis.AxisDependency.RIGHT);// 当前统计图表中最多在Y轴坐标线上显示的总量
        leftYAxis.setEnabled(false);


        rightYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value+"℃";
            }
        });


    }

    /**
     * 功能：对图表中的曲线初始化，添加三条，并且默认显示第一条
     */
    void initLine() {

        createLine(mList1, mEntries1, mLineDataSet1, Color.GREEN, mLineData, mLineChart);
        for (int i = 0; i < mLineData.getDataSetCount(); i++) {
            mLineChart.getBarData().getDataSets().get(i).setVisible(false);

        }
        showLine(LINE_NUMBER_1);
    }

    /**
     * 功能：根据索引显示或隐藏指定线条
     */
    public void showLine(int index) {
        mLineChart
                .getBarData()
                .getDataSets()
                .get(index)
                .setVisible(mCheckBoxList.get(index).isChecked());
        mLineChart.invalidate();
    }

    /**
     * 功能：动态创建一条曲线
     */
    private void createLine(List<Float> dataList, List<BarEntry> entries, BarDataSet lineDataSet, int color, BarData lineData, com.github.mikephil.charting.charts.BarChart lineChart)  {
        for (int i = 0; i < dataList.size(); i++) {
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            BarEntry entry = new BarEntry(i, dataList.get(i));// Entry(x,y)
            entries.add(entry);
        }

        // 初始化线条
        initLineDataSet(lineDataSet, color);

        if (lineData == null) {
            lineData = new BarData();
            lineData.addDataSet(lineDataSet);
            lineChart.setData(lineData);
        } else {
            lineChart.getBarData().addDataSet(lineDataSet);
        }

        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return value+"℃";
            }
        });
        lineChart.invalidate();
    }


    /**
     * 曲线初始化设置,一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     */
    private void initLineDataSet(BarDataSet lineDataSet, int color) {
        lineDataSet.setColor(color); // 设置曲线颜色
        lineDataSet.setValueTextSize(10f);

        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

    }


    /**
     * 功能：创建图例
     */
    private void createLegend(Legend legend) {
        /***折线图例 标签 设置***/
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }


    /**
     * 设置 可以显示X Y 轴自定义值的 MarkerView
     * @param lineChart
     */
    public void setMarkerView(com.github.mikephil.charting.charts.BarChart lineChart) {
        LineChartMarkViewDemo mv = new LineChartMarkViewDemo(this);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        lineChart.invalidate();
    }


    /**
     * 动态添加数据
     * 在一个LineChart中存放的折线，其实是以索引从0开始编号的
     *
     * @param yValues y值
     */
    public void addEntry(BarData lineData, com.github.mikephil.charting.charts.BarChart lineChart, float yValues, int index) {

        // 通过索引得到一条折线，之后得到折线上当前点的数量
        int xCount = lineData.getDataSetByIndex(index).getEntryCount();


        BarEntry entry = new BarEntry(xCount, yValues); // 创建一个点
        lineData.addEntry(entry, index); // 将entry添加到指定索引处的折线中

        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();

        //把yValues移到指定索引的位置
        lineChart.moveViewToAnimated(xCount - 4, yValues, YAxis.AxisDependency.LEFT, 1000);
        lineChart.invalidate();
    }


    /**
     * 功能：第1条折线添加一个点
     */
    public void addLine1Data(float yValues) {
        addEntry(mLineData, mLineChart, yValues, LINE_NUMBER_1);
    }


    /**
     * 功能：第3条折线添加一个点
     */

    /**
     * 功能：发送开始
     */
    void sendStartAddEntry() {
        if (!mDemoHandler.hasMessages(MSG_START)) { // 判断是否有消息队列此消息，如果没有则发送
            mDemoHandler.sendEmptyMessageDelayed(MSG_START, 1000);
        }
    }

    /**
     * 功能：暂停添加点，即移除所有消息
     */
    void sendPauseAddEntry() {
        mDemoHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 功能：返回主页
     */
    void sendReturnAddEntry() {
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空消息
        mDemoHandler.removeCallbacksAndMessages(null);
        mDemoHandler = null;

        // moveViewToAnimated 移动到某个点，有内存泄漏，暂未修复，希望网友可以指着
        mLineChart.clearAllViewportJobs();
        mLineChart.removeAllViewsInLayout();
        mLineChart.removeAllViews();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.demo_start:
                sendStartAddEntry();
                break;
            case R.id.demo_pause:
                sendPauseAddEntry();
                break;
            case R.id.demo_return:
                sendReturnAddEntry();
                break;
            case R.id.demo_checkbox1:
                showLine(LINE_NUMBER_1);
                break;
            default:
        }
    }


    /**
     * 功能：自定义Handler，通过弱引用的方式防止内存泄漏
     */
    private static class DemoHandler extends Handler {

        WeakReference<BarChart> mReference;

        DemoHandler(BarChart activity) {
            mReference = new WeakReference<>(activity);
        }


        @SuppressLint("Range")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BarChart lineChartDemo = mReference.get();
            if (lineChartDemo == null) {
                return;
            }
            DBHelper dbHelper = new DBHelper(lineChartDemo.getApplicationContext(), 2);
            //1.得到连接
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //sqLiteDatabase.execSQL("select * from data order by _id desc limit 1");

            Cursor cursor = db.query("data",null,null,null,null,null,"_id desc", "1,1");
            int temp = 0;
            while (cursor.moveToNext()) {
                //用cursor.getColumnIndex获得列
                temp = cursor.getInt(cursor.getColumnIndex("Tem"));

            }
            switch (msg.what) {
                case MSG_START:
                    lineChartDemo.addLine1Data((float)temp);
                    //lineChartDemo.addLine3Data(lineChartDemo.getRandom(10f));
                    lineChartDemo.sendStartAddEntry();
                    db.close();
                    break;
                default:
            }
        }
    }
}