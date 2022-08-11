package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener, View.OnTouchListener {

    private AppBarConfiguration appBarConfiguration;

    TextView down,up;
    //声明Fragment对象
    private Fragment fragment1,fragment2,nowFragemnt ;

    public static final int VIEW_PAGER_DELAY = 2000;
    private MyPagerAdapter mAdapter;
    private List<ImageView> mItems;
    private ImageView[] mBottomImages;
    private LinearLayout mBottomLiner;
    private ViewPager mViewPager;

    private int currentViewPagerItem;
    //是否自动播放
    private boolean isAutoPlay;

    private MyHandler mHandler;
    private Thread mThread;
    @RequiresApi(api= Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);
//        DBHelper dbHelper = new DBHelper(this,1);
//        SQLiteDatabase sqLiteDatabase=dbHelper.getReadableDatabase();
        initUI();

        mHandler = new MyHandler(this);
        //配置轮播图ViewPager
        mViewPager = ((ViewPager) findViewById(R.id.live_view_pager));
        mItems = new ArrayList<>();
        mAdapter = new MyPagerAdapter(mItems, this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnTouchListener((View.OnTouchListener) this);
        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) this);
        isAutoPlay = true;

        //TODO: 添加ImageView
        addImageView();
        mAdapter.notifyDataSetChanged();
        //设置底部4个小点
        setBottomIndicator();
    }

    private void addImageView(){
        ImageView view0 = new ImageView(this);
        view0.setImageResource(R.mipmap.pic0);
        ImageView view1 = new ImageView(this);
        view1.setImageResource(R.mipmap.pic1);
        ImageView view2 = new ImageView(this);
        view2.setImageResource(R.mipmap.pic2);

        view0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view2.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mItems.add(view0);
        mItems.add(view1);
        mItems.add(view2);

    }

    private void setBottomIndicator() {
        //获取指示器(下面三个小点)
        mBottomLiner = (LinearLayout) findViewById(R.id.live_indicator);
        //右下方小圆点
        mBottomImages = new ImageView[mItems.size()];
        for (int i = 0; i < mBottomImages.length; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);
            //如果当前是第一个 设置为选中状态
            if (i == 0) {
                imageView.setImageResource(R.drawable.indicator_select);
            } else {
                imageView.setImageResource(R.drawable.indicator_no_select);
            }
            mBottomImages[i] = imageView;
            //添加到父容器
            mBottomLiner.addView(imageView);
        }

        //让其在最大值的中间开始滑动, 一定要在 mBottomImages初始化之前完成
        int mid = MyPagerAdapter.MAX_SCROLL_VALUE / 2;
        mViewPager.setCurrentItem(mid);
        currentViewPagerItem = mid;

        //定时发送消息
        mThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(MainActivity.VIEW_PAGER_DELAY);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        mThread.start();
    }


    //初始化UI界面
    private void initUI(){

        //初始化底部标签
        down = findViewById(R.id.down);
        up = findViewById(R.id.up);

        //设置底部标签的变化，默认第一个被选中
        down.setBackgroundColor(Color.LTGRAY);
        up.setBackgroundColor(Color.WHITE);

//        public static final int BLACK = -16777216;
//        public static final int BLUE = -16776961;
//        public static final int CYAN = -16711681;
//        public static final int DKGRAY = -12303292;
//        public static final int GRAY = -7829368;
//        public static final int GREEN = -16711936;
//        public static final int LTGRAY = -3355444;
//        public static final int MAGENTA = -65281;
//        public static final int RED = -65536;
//        public static final int TRANSPARENT = 0;
//        public static final int WHITE = -1;
//        public static final int YELLOW = -256;
        //为底部标签内设置点击事件
        down.setOnClickListener(this);
        up.setOnClickListener(this);
        showFragment1();

    }


    //第一个标签被点击
    private void showFragment1(){

        //开启事务,Fragment的切换是由事务控制
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //判断Fragment是否为空
        if (fragment1 == null){
            fragment1 = new Fragment_1();
            //添加Fragmet1到事务中
            transaction.add(R.id.content_layout,fragment1);
        }
        //隐藏所有的Fragment
        hideAllFragment(transaction);
        //显示Fragment
        transaction.show(fragment1);
        //记录Fragment
        nowFragemnt = fragment1;
        //提交事务
        transaction.commit();
        //设置底部标签的变化
        down.setBackgroundColor(Color.LTGRAY);
        up.setBackgroundColor(Color.WHITE);
    }


    //第二个标签被点击
    private void showFragment2(){

        //开启事务,Fragment的切换是由事务控制
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //判断Fragment是否为空
        if (fragment2 == null){
            fragment2 = new Fragment_2();
            //添加Fragmet1到事务中
            transaction.add(R.id.content_layout,fragment2);
        }
        //隐藏所有的Fragment
        hideAllFragment(transaction);
        //显示Fragment
        transaction.show(fragment2);
        //记录Fragment
        nowFragemnt = fragment2;
        //提交事务
        transaction.commit();
        //设置底部标签的变化
        up.setBackgroundColor(Color.LTGRAY);
        down.setBackgroundColor(Color.WHITE);
        SQLiteStudioService.instance().stop();
    }

    //隐藏所有的Fragment
    private void hideAllFragment(FragmentTransaction transaction){
        if (fragment1!= null){
            transaction.hide(fragment1);
        }
        if (fragment2!= null){
            transaction.hide(fragment2);
        }
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.down: showFragment1();break;
            case R.id.up: showFragment2();break;
            default: break;
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
///////////////////////////////////////////////////////////////////////////
// ViewPager的监听事件
///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        currentViewPagerItem = position;
        if (mItems != null) {
            position %= mBottomImages.length;
            int total = mBottomImages.length;

            for (int i = 0; i < total; i++) {
                if (i == position) {
                    mBottomImages[i].setImageResource(R.drawable.indicator_select);
                } else {
                    mBottomImages[i].setImageResource(R.drawable.indicator_no_select);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isAutoPlay = false;
                break;
            case MotionEvent.ACTION_UP:
                isAutoPlay = true;
                break;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 为防止内存泄漏, 声明自己的Handler并弱引用Activity
    ///////////////////////////////////////////////////////////////////////////
    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mWeakReference;

        public MyHandler(MainActivity activity) {
            mWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MainActivity activity = mWeakReference.get();
                    if (activity.isAutoPlay) {

                        activity.mViewPager.setCurrentItem(++activity.currentViewPagerItem);
                    }

                    break;
            }

        }
    }
}