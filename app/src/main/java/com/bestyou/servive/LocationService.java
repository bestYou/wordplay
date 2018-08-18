package com.bestyou.servive;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.Time;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.bestyou.wtc1.GeoWord;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

public class LocationService extends Service {
    public LocationService() {
    }

    public final String TAG="LocationService";
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private Timer mTimer;

    private SharedPreferences sp;
    private String username;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onCreate() {
        android.app.Notification  noti = new android.app.Notification();
        noti.flags = android.app.Notification.FLAG_NO_CLEAR|android.app.Notification.FLAG_ONGOING_EVENT;
        startForeground(1, noti);




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mTimer = new java.util.Timer();
        TimerTask  task = new java.util.TimerTask(){
            @Override
            public void run() {

                //增加同步块
                synchronized (LocationService.class) {
                    initLocation();
                    locationClient.startLocation();
                }

            }};
        mTimer.scheduleAtFixedRate(task, 0, 10*1000);
//        flags = START_STICKY;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(getApplicationContext(), "onDestroy", 0).show();
        if(locationClient!=null){
            locationClient.stopLocation();
            destroyLocation();
        }
        if(null!=mTimer){
            mTimer.cancel();
        }


        super.onDestroy();
    }

    private void initLocation(){
        if(locationClient==null){

            //增加同步块
            synchronized (LocationService.class) {

                //初始化client
                locationClient = new AMapLocationClient(this.getApplicationContext());

            }

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
                android.util.Log.i(TAG,"定位成功");
                android.util.Log.i(TAG,"定位的经度位置是："+loc.getLatitude());
                android.util.Log.i(TAG,"定位的纬度位置是："+loc.getLongitude());
                Date date = new Date(loc.getTime());
                //android.util.Log.i(TAG,"用户是："+ DBUtil.getUserMessage().getPhone());
//                if(isCurrentInTimeScope()) {
//                    UpDate(loc,date);
//                }
                UpDate(loc,date);
            } else {//定位失败
                android.util.Log.i(TAG,"定位失败");
            }

            locationClient.stopLocation();
            destroyLocation();

        }
    };

    String e1;

    /**上传更新*/
    private void UpDate(AMapLocation loc,Date date) {

        String user_phone;

        //用于自动登录,这里用于获取用户id
        sp=getSharedPreferences("setting", 0);
        username =sp.getString("username",null);

        if(username != null){
            user_phone = username;
        }
        else {
            user_phone="0000";
        }

        BmobGeoPoint point = new BmobGeoPoint(loc.getLongitude(), loc.getLatitude());
        BmobDate bmobDate = new cn.bmob.v3.datatype.BmobDate(date);


        GeoWord p2 = new GeoWord();
        p2.setUserName(user_phone);
        p2.setGpsAdd(point);
        p2.setTimeDingwei(bmobDate);

        //增加同步块
        synchronized (LocationService.class) {

            p2.save(new SaveListener<String>() {
                @Override
                public void done(String objectId,cn.bmob.v3.exception.BmobException e) {
                    if(e==null){
//                    Toast.makeText(getApplicationContext(), "添加数据成功，返回objectId为："+objectId,
//                            Toast.LENGTH_SHORT);
                        android.util.Log.i("LocationService","添加数据成功，返回objectId为："+objectId);
                    }else{
//                    Toast.makeText(getApplicationContext(), "创建数据失败："+ e.getMessage(),
//                            Toast.LENGTH_SHORT);
                        e1 = "Message : "+e.getMessage()+"Error code : "+e.getErrorCode();
                        android.util.Log.i("LocationService","创建数据失败："+ e.getMessage());
                    }
                }
            });

        }





    }


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

    /**
     * 判断两个点之间的距离大于规定的距离
     * */
    private boolean IsDistanceMoreOneMile(LatLng last,LatLng news){
        if(last==null){
            android.util.Log.i(TAG,"第一次为空");
            return true;
        }
        float mi=com.amap.api.maps.AMapUtils.calculateLineDistance(last, news);
        android.util.Log.i(TAG,"两次的间隔为："+mi);
        if(mi>2.0f){
            return true;
        }
        else
            return false;
    }

    /***
     * 判断当前时间是否在规定的时间段范围内
     * */
    public static boolean isCurrentInTimeScope() {
        int beginHour=5;
        int beginMin=0;
        int endHour=23;
        int endMin=0;
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();
        Time now = new android.text.format.Time();
        now.set(currentTimeMillis);
        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;
        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;
        if (!startTime.before(endTime)) {
        // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
        // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        android.util.Log.i("LocationService","是否在时间间隔中"+result);
        return result;
    }


}
