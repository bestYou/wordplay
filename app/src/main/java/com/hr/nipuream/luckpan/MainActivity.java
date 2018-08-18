package com.hr.nipuream.luckpan;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.mobstat.StatService;
import com.bestyou.servive.LocationService;
import com.bestyou.wtc1.ActivityManagerApplication;
import com.bestyou.wtc1.GeoWord;
import com.bestyou.wtc1.HintPopupWindow;
import com.bestyou.wtc1.MapActivity;
import com.bestyou.wtc1.R;
import com.bestyou.wtc1.SetingUs;
import com.bestyou.wtc1.SystemUtil;
import com.example.administrator.huaweiview.MyView;
import com.hr.nipuream.luckpan.view.LuckPanLayout;
import com.hr.nipuream.luckpan.view.RotatePan;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.views.PgyerDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bluemobi.dylan.step.step.UpdateUiCallBack;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import grap.yyh.nf.com.myapplication.utils.GetToast;
import grap.yyh.nf.com.myapplication.view.BofangView;
import grap.yyh.nf.com.myapplication.view.FlakeView;

import static com.bestyou.servive.LocationService.isCurrentInTimeScope;

public class MainActivity extends AppCompatActivity implements LuckPanLayout.AnimationEndListener{


    private double lat;
    private double lon;

    //用于调试时主界面显示当前位置
    //public TextView location;


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private Timer mTimer;
    public final String TAG="从Activity获取位置";




    //大转盘模块
    private RotatePan rotatePan;
    private LuckPanLayout luckPanLayout;
    private ImageView goBtn;
    private ImageView yunIv;
    private String[] strs ;

    private ImageView go2Btn;

    //抢红包模块
    //图标跳动
    @Bind(R.id.ibtn_show)
    ImageButton ibtn_show;
    @Bind(R.id.ibtn_map)
    ImageButton ibtn_map;
    @Bind(R.id.ibtn_word)
    ImageButton ibtn_word;
    @Bind(R.id.ibtn_run)
    ImageButton ibtn_run;
    //金币掉落动画的主体动画
    private FlakeView flakeView;
    private BofangView iv_onclick;
    private ImageView iv_button_word;
    private ImageView iv_continue;

    //沉浸式状态栏
    Toolbar mToolbar;
    //EditText editTextWord;

    //仿QQ右上角选项弹出
    Button button_hint_pop;
    private HintPopupWindow hintPopupWindow;

    // 动态权限申请，要申请的权限
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    //能量球效果
    private TextView tv;
    MyView mv;
    LinearLayout ll;
    private int energy = 300;   //记录能量值

    //记步模块
    private boolean isBind = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bmob.initialize(this, "e012c9c6f96fbfe7df2054e88fde29b4");

        //用于调试时主界面显示当前位置
//        location = (TextView) findViewById(R.id.location);
//        location.setText("0"+lat+","+lon);

        //用于在其他activity关闭本activity
        ActivityManagerApplication.addDestoryActivity(this,"LuckPan");


        mTimer = new java.util.Timer();
        TimerTask task = new java.util.TimerTask(){
            @Override
            public void run() {
                initLocation();
                locationClient.startLocation();
            }};
        mTimer.scheduleAtFixedRate(task, 0, 10*1000);


        startAlarm();

//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mLocationListener);
//        //init();



//        //启动实时定位服务
//        Intent it=new Intent(this, com.bestyou.servive.LocationService.class);
//        startService(it);

//        //启动记步服务
//        setupService();

        strs = getResources().getStringArray(R.array.names);
        luckPanLayout = (LuckPanLayout) findViewById(R.id.luckpan_layout);
        luckPanLayout.setAnimationEndListener(this);
        goBtn = (ImageView)findViewById(R.id.go);

        ibtn_map = (ImageButton) findViewById(R.id.ibtn_map);
        go2Btn = (ImageView)findViewById(R.id.go_map);

        //抢红包
        ButterKnife.bind(this);
        flakeView = new FlakeView(com.hr.nipuream.luckpan.MainActivity.this);
        doTreeWaggleAnimation(ibtn_show);
        doWaggleAnimation(ibtn_run);
        doWaggleAnimation(ibtn_map);
        doWaggleAnimation(ibtn_word);

        //沉浸式状态栏
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        //mTextView = (TextView) findViewById(R.id.tvCenter);

        initTopView();

        button_hint_pop = (Button) findViewById(R.id.button_right_top);
        button_hint_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出选项弹窗
                hintPopupWindow.showPopupWindow(v);
            }
        });

        //下面的操作是初始化弹出数据
        ArrayList<String> strList = new ArrayList<>();
        strList.add("分享");
        strList.add("反馈");
        strList.add("设置");

        ArrayList<View.OnClickListener> clickList = new ArrayList<>();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "跳转分享页面", Toast.LENGTH_SHORT).show();
                showShare();
            }
        };
        View.OnClickListener clickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "跳转反馈页面", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    int i1 = ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]);
                    int i2 = ContextCompat.checkSelfPermission(MainActivity.this, permissions[1]);
                    int i3 = ContextCompat.checkSelfPermission(MainActivity.this, permissions[2]);
                    int i4 = ContextCompat.checkSelfPermission(MainActivity.this, permissions[3]);

                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (i1 != PackageManager.PERMISSION_GRANTED ||
                            i2 != PackageManager.PERMISSION_GRANTED ||
                            i3 != PackageManager.PERMISSION_GRANTED ||
                            i4 != PackageManager.PERMISSION_GRANTED
                            ) {
                        // 如果没有授予该权限，就去提示用户请求
                        startRequestPermission();
                        Toast.makeText(MainActivity.this, "请再次点击反馈按钮~", Toast.LENGTH_SHORT).show();
                    }
                }
                feedback();
            }
        };
        View.OnClickListener clickListener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Power by Info_Coding Team.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SetingUs.class);
                startActivity(intent);
            }
        };
        //第4个点击事件未被触发
        View.OnClickListener clickListener4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击事件触发44", Toast.LENGTH_SHORT).show();
            }
        };
        clickList.add(clickListener);
        clickList.add(clickListener2);
        clickList.add(clickListener3);
        clickList.add(clickListener4);

        //具体初始化逻辑看下面的图
        hintPopupWindow = new HintPopupWindow(this, strList, clickList);

        //上报 Crash 异常
        PgyCrashManager.register(this);

        //能量球效果
        tv= (TextView) findViewById(R.id.tv);
        mv= (MyView) findViewById(R.id.mv);
        mv.change(energy);
        mv.moveWaterLine();
        ll= (LinearLayout) findViewById(R.id.ll);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mv.change(energy-10);
                go2Btn.performClick();
            }
        });
        mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mv.change(energy-10);
                //Toast.makeText(getApplicationContext(), "功能模块待添加", Toast.LENGTH_SHORT).show();
                go2Btn.performClick();
            }
        });
        mv.setOnAngleColorListener(onAngleColorListener);

        PgyUpdateManager.setIsForced(false); //设置是否强制更新。true为强制更新；false为不强制更新（默认值）。
        PgyUpdateManager.register(MainActivity.this);

        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        StatService.start(this);
    }


    private void upPoint(double latup,double lonup,Date date){

        BmobGeoPoint point = new BmobGeoPoint(lonup, latup);
        BmobDate bmobDate = new BmobDate(date);

        GeoWord p2 = new GeoWord();
        p2.setUserName(SystemUtil.getSystemModel());
        p2.setGpsAdd(point);
        p2.setTimeDingwei(bmobDate);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Toast.makeText(getApplicationContext(), " MainActivity: 添加数据成功，返回objectId为："+objectId,
                            Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getApplicationContext(), "创建数据失败："+ e.getMessage(),
                            Toast.LENGTH_SHORT);
                }
            }
        });

    }

//    /**
//     * 开启计步服务
//     */
//    private void setupService() {
//        Intent intent = new Intent(this, StepService.class);
//        startService(intent);
//    }




//    private void init() {
//        setUpMap();
//    }
//
//    private void setUpMap() {
//        //初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置是否返回地址信息（默认返回地址信息）
//        mLocationOption.setNeedAddress(true);
//        //设置是否只定位一次,默认为false
//        mLocationOption.setOnceLocation(false);
//        //设置是否强制刷新WIFI，默认为强制刷新
//        mLocationOption.setWifiActiveScan(true);
//        //设置是否允许模拟位置,默认为false，不允许模拟位置
//        mLocationOption.setMockEnable(false);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();
//    }




    //反馈模块
    private void feedback(){
        PgyerDialog.setDialogTitleBackgroundColor("#42D5BB");
        PgyerDialog.setDialogTitleTextColor("#ffffff");
        // 以对话框的形式弹出
        PgyFeedback.getInstance().showDialog(MainActivity.this);
    }

    //接口回调实现背景同步色彩
    private MyView.OnAngleColorListener onAngleColorListener=new MyView.OnAngleColorListener() {
        @Override
        public void onAngleColorListener(int red, int green) {
            Color color=new Color();
            int c=color.argb(150, red, green, 0);
            // ll.setBackgroundColor(c);
        }
    };

    // 开始提交请求动态权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
        // 以对话框的形式弹出
        PgyFeedback.getInstance().showDialog(MainActivity.this);
    }

    //转盘开启转动
    public void rotation(View view){
        //go2Btn.setImageDrawable(getResources().getDrawable((R.drawable.btn_down)));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //注释掉的两行可以实现动态图标效果
                //go2Btn.setImageDrawable(getResources().getDrawable((R.drawable.btn_up)));
                luckPanLayout.rotate(-1,1000);
            }
        }, 1000);
    }
    public void showMap(View view){
        //luckPanLayout.rotate(-1,100);
        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, com.bestyou.gaode.MainActivity.class);
        startActivity(intent);
    }
    public void showWord(View view){
        //luckPanLayout.rotate(-1,100);
        //Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, com.bestyou.timeline3.MainActivity.class);
        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, com.bestyou.word.activity.MainActivity.class);
        startActivity(intent);
    }
    public void showHistory(View view){
        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, com.bestyou.timeline3.MainActivity.class);
        startActivity(intent);
    }
    public void showRed(View view){//绿植界面
        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, com.ltb.laer.waterview.MainActivity.class);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "功能模块待添加",
////                Toast.LENGTH_SHORT).show();

    }
    public void showRun(View view){

        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, cn.bluemobi.dylan.step.activity.MainActivity.class);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "功能模块待添加",
////                Toast.LENGTH_SHORT).show();

    }
    public void showMainTop(View view){
//
//        Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, cn.bluemobi.dylan.step.activity.MainActivity.class);
//        startActivity(intent);

        Toast toast=Toast.makeText(getApplicationContext(), "风里雨里，单词与你@单词训练营", 3000);
        toast.setGravity(Gravity.CENTER, 0, 0);
        //创建图片视图对象
        ImageView imageView= new ImageView(getApplicationContext());
        //设置图片
        imageView.setImageResource(R.mipmap.ic_launcher);
        //获得toast的布局
        LinearLayout toastView = (LinearLayout) toast.getView();
        //设置此布局为横向的
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        //将ImageView在加入到此布局中的第一个位置
        toastView.addView(imageView, 0);
        toast.show();

    }

    @Override
    //转盘结束转动时触发
    public void endAnimation(int position) {
        Toast.makeText(this,"Position = "+position+","+strs[position],Toast.LENGTH_SHORT).show();
        energy = energy-10;
        if(position==0 || position==2 || position==4){
            showPopWindows(ibtn_show, "20", true);
        }else if(position==1){
            showWordPopWindows(ibtn_show, "Geo-Robot", "测量机器人",true);
        }else if(position==3){
            showWordPopWindows(ibtn_show, "IERS", "国际地球自转服务",true);
        }else if(position==5){
            showWordPopWindows(ibtn_show, "Control Survey", "控制测量",true);
        }else if(position==6){
            showWordPopWindows(ibtn_show, "Adjustment", "平差",true);
        }else if(position==7){
            showWordPopWindows(ibtn_show, "Mine Survey", "矿山测量学",true);
        }else{
            showWordPopWindows(ibtn_show, "Marine Survey", "海洋测量",true);
        }
//        }else if(position==6){
//
//        }else if(position==7){
//            showWordPopWindows(ibtn_show, "Adjustment", "平差",true);
//        }

    }

    //跳转地图
    @OnClick({R.id.ibtn_map})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_show:
               /* myPopupWindow.startAnomation(ibtn_show,true).showAtLocation(v, Gravity.CENTER, 0, 0);*/
                Intent intent = new Intent(com.hr.nipuream.luckpan.MainActivity.this, MapActivity.class);
                startActivity(intent);
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        flakeView.resume();
    }

    private PopupWindow pop;
    //弹出单词币效果
    private PopupWindow showPopWindows(View v, String moneyStr, boolean show) {
        View view = this.getLayoutInflater().inflate(R.layout.view_login_reward, null);
        TextView tvTips = (TextView) view.findViewById(R.id.tv_tip);
        TextView money = (TextView) view.findViewById(R.id.tv_money);
        tvTips.setText("恭喜您获得单词财富 ￥" + moneyStr + " ");
        money.setText(moneyStr);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        container.removeAllViews();
        //将flakeView 添加到布局中
        container.addView(flakeView);
        //设置背景
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //设置同时出现在屏幕上的金币数量  建议64以内 过多会引起卡顿
        flakeView.addFlakes(8);
        /**
         * 绘制的类型
         * @see View.LAYER_TYPE_HARDWARE
         * @see View.LAYER_TYPE_SOFTWARE
         * @see View.LAYER_TYPE_NONE
         */
        flakeView.setLayerType(View.LAYER_TYPE_NONE, null);
        iv_onclick = (BofangView) view.findViewById(R.id.iv_onclick);
        // iv_onclick.setBackgroundResource(R.drawable.open_red_animation_drawable);
        iv_onclick.startAnation();
        iv_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (container!=null){
                    container.removeAllViews();
                }
                pop.dismiss();
                GetToast.useString(getBaseContext(),"恭喜您，抢到红包");

            }
        });
        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.half_color));
        pop.setBackgroundDrawable(dw);
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.showAtLocation(v, Gravity.CENTER, 0, 0);

        /**
         * 移除动画
         */
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //设置2秒后
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            container.removeAllViews();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        if (!show)
            thread.start();
        //ivOpen指的是需要播放动画的ImageView控件
//        AnimationDrawable animationDrawable = (AnimationDrawable)iv_onclick.getBackground();
//        animationDrawable.start();//启动动画
        MediaPlayer player = MediaPlayer.create(this, R.raw.shake);
        player.start();

       // showWordPopWindows(ibtn_show, "20", true);    //会闪退

        return pop;

    }
    //弹出词卡效果
    private PopupWindow showWordPopWindows(View v, String wordStr,String meanStr, boolean show) {
        View view = this.getLayoutInflater().inflate(R.layout.view_word, null);
        TextView tvWord = (TextView) view.findViewById(R.id.tv_word);
        TextView tvMean = (TextView) view.findViewById(R.id.tv_mean);
        tvWord.setText(wordStr);
        tvMean.setText(meanStr);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.container_word);
        container.removeAllViews();

        //设置背景
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        /**
         * 绘制的类型
         * @see View.LAYER_TYPE_HARDWARE
         * @see View.LAYER_TYPE_SOFTWARE
         * @see View.LAYER_TYPE_NONE
         */

        iv_continue = (ImageView) view.findViewById(R.id.iv_continue);
        iv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (container!=null){
                    container.removeAllViews();
                }
                pop.dismiss();
                GetToast.useString(getBaseContext(),"恭喜您，抢到红包");
            }
        });
        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.half_color));
        pop.setBackgroundDrawable(dw);
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.showAtLocation(v, Gravity.CENTER, 0, 0);


        MediaPlayer player = MediaPlayer.create(this, R.raw.shake);
        player.start();
        return pop;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        if(locationClient!=null){
            locationClient.stopLocation();
            destroyLocation();
        }
        if(null!=mTimer){
            mTimer.cancel();
        }


        super.onDestroy();

    }
    public static void doWaggleAnimation(View view) {
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("Rotation", 0f, -20f, 20f, -20f, 20f, -20f, 20f, -20f, 0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("ScaleX", 0.8f, 0.85f, 0.9f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("ScaleY", 0.8f, 0.85f, 0.9f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, rotation, scaleX, scaleY);
        animator.setDuration(2000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
    public static void doTreeWaggleAnimation(View view) {
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("Rotation", 0f, -20f, 20f, -20f, 20f, -20f, 20f, -20f, 0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("ScaleX", 0.8f, 0.85f, 0.9f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("ScaleY", 0.8f, 0.85f, 0.9f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, rotation, scaleX, scaleY);
        animator.setDuration(4000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
    //抢红包end

    //初始化菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //初始化沉浸式界面效果
    private void initTopView() {
        mToolbar.setTitle("Title");
        setSupportActionBar(mToolbar);

        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionMe1:
                        Toast.makeText(getApplicationContext(), "actionMe", Toast.LENGTH_SHORT).show();
                        button_hint_pop.performClick();
                        break;
                }
                return false;
            }
        });

        if (mToolbar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    //社会化分享模块
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("WordPlay");

        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://wordplay.cf");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("风里雨里，单词与你！");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath(R.id.actionMe1);//确保SDcard下面存在此张图片
        oks.setImageUrl("https://goss1.vcg.com/creative/vcg/800/version23/VCG21gic19773202.jpg");

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://wordplay.cf");

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论文本(调试时使用)");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://wordplay.cf");

        // 启动分享GUI
        oks.show(this);
    }

    //防止用户误触返回键，设为按两次返回键退出程序
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void initLocation(){
        if(locationClient==null){
            //初始化client
            locationClient = new AMapLocationClient(this.getApplicationContext());
        }

        //初始化定位参数
        initLocationOption();

        //设置定位参数

        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    private void initLocationOption() {

        if (null == locationOption) {
            locationOption = new AMapLocationClientOption();
        }
        //定位精度:高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位缓存策略
        locationOption.setLocationCacheEnable(false);
        //gps定位优先
        locationOption.setGpsFirst(false);
        //设置定位间隔
//        locationOption.setInterval(3000);
        locationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        locationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        locationOption.setOnceLocationLatest(true);//true表示获取最近3s内精度最高的一次定位结果；false表示使用默认的连续定位策略。
        locationOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        //AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP


    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {//定位成功

//              Toast.makeText(getApplicationContext(), loc.getLatitude()+"---"+loc.getLongitude(),0).show();
                Log.i(TAG,"定位成功");
                Log.i(TAG,"定位的经度位置是："+loc.getLatitude());
                Log.i(TAG,"定位的纬度位置是："+loc.getLongitude());
                Date date = new Date(loc.getTime());
                //android.util.Log.i(TAG,"用户是："+ DBUtil.getUserMessage().getPhone());
//                if(isCurrentInTimeScope()) {
//                    upPoint(loc.getLatitude(),loc.getLongitude(),date);
//                }
                upPoint(loc.getLatitude(),loc.getLongitude(),date);
            } else {//定位失败
                Log.i(TAG,"定位失败");
            }

            locationClient.stopLocation();
            destroyLocation();

        }
    };

    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    public void startAlarm(){
        /**
         首先获得系统服务
         */
        AlarmManager am = (AlarmManager)
                getSystemService(Context.ALARM_SERVICE);

        /** 设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传*/
        Intent intent = new Intent(this, LocationService.class);
        PendingIntent pendSender = PendingIntent.getService(this, 0, intent, 0);
        am.cancel(pendSender);

        /**AlarmManager.RTC_WAKEUP 这个参数表示系统会唤醒进程；我设置的间隔时间是10分钟 */
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10*60*1000, pendSender);
    }



}
