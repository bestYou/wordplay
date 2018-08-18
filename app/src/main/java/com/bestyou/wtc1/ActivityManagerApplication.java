package com.bestyou.wtc1;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/*
*   android在一个activity中finish掉另外一个activity
说明：
   在A创建的时候，调用 add方法把当前的A添加进去。
   当需要结束的时候，在B中调用 destoryActivity方法，指定添加A时的Key值来finish 掉A
* */


public class ActivityManagerApplication extends Application {

    private static Map<String,Activity> destoryMap = new HashMap<>();

    private ActivityManagerApplication() {
    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity,String activityName) {
        destoryMap.put(activityName,activity);
    }
    /**
     *销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }
}
