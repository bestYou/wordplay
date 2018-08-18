package com.bestyou.wtc1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bestyou.word.utils.Const;
import com.bestyou.word.utils.DBOpenHelper;
import com.bestyou.word.utils.FileUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {


    private SharedPreferences sp;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //用于自动登录
        sp=getSharedPreferences("setting", 0);
        username =sp.getString("username",null);

        //初始化单词数据库
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=*****");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exist = sharedPreferences.getBoolean(Const.SP_KEY, false);
        if (!exist) {
            sharedPreferences.edit().putBoolean(Const.SP_KEY, true).apply();
            new com.bestyou.wtc1.SplashActivity.FileTask().execute();
        } else {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //startMain();
                    //nextPage();
                    //startVerification();

                    if(username != null){
                        //talk.setText(username + "自动登录成功");
                        //Toast.makeText(SplashActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                        //startMain();
                        Intent intent = new Intent(SplashActivity.this, com.hr.nipuream.luckpan.MainActivity.class);
                        //Intent intent = new Intent(com.bestyou.wtc1.SplashActivity.this, com.bestyou.wtc1.MainActivity.class);
                        startActivity(intent);
                        //检测系统是否打开开启了地理定位权限
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    else {
                        //startVerification();
                        Intent intent = new Intent(SplashActivity.this, Verification.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                }
            };
            Timer timer = new Timer();
            timer.schedule(task, Const.DELAY_TIME);
        }

    }

    private void nextPage(){

        if(username != null){
            //talk.setText(username + "自动登录成功");
            Toast.makeText(this, "自动登录成功", Toast.LENGTH_SHORT).show();
            //startMain();
            Intent intent = new Intent(SplashActivity.this, com.hr.nipuream.luckpan.MainActivity.class);
            //Intent intent = new Intent(com.bestyou.wtc1.SplashActivity.this, com.bestyou.wtc1.MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else {
            //startVerification();
            Intent intent = new Intent(SplashActivity.this, Verification.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void startMain() {
        Intent intent = new Intent(com.bestyou.wtc1.SplashActivity.this, com.hr.nipuream.luckpan.MainActivity.class);
        //Intent intent = new Intent(com.bestyou.wtc1.SplashActivity.this, com.bestyou.wtc1.MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startVerification() {
        //Intent intent = new Intent(com.bestyou.wtc1.SplashActivity.this, com.hr.nipuream.luckpan.MainActivity.class);
        Intent intent = new Intent(SplashActivity.this, Verification.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initTable() {
        DBOpenHelper dbOpenHelper = DBOpenHelper.getInstance(this);
        SQLiteDatabase database = dbOpenHelper.getDatabase();
        database.execSQL("create table if not exists TABLE_UNIT (" +
                "Unit_Key integer not null," +
                "Unit_Time integer not null default 0," +
                "Cate_Key text references TABLE_META(Meta_Key)" +
                ");");
        for (String metaKey : Const.META_KEYS) {
            Cursor cursor = database.rawQuery("select Meta_UnitCount from TABLE_META where Meta_Key=?;"
                    , new String[]{metaKey});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("Meta_UnitCount"));
                for (int i = 1; i <= count; i++) {
                    database.execSQL("insert into TABLE_UNIT (Unit_Key,Unit_Time,Cate_Key) " +
                            "values(?,?,?);", new Object[]{i, 0, metaKey});
                }
            }
            cursor.close();
        }
    }

    private class FileTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            FileUtils.writeData(com.bestyou.wtc1.SplashActivity.this);
            initTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startMain();
        }
    }

}