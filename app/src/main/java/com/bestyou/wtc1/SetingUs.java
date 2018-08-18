package com.bestyou.wtc1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bestyou.widget.PullScrollView;

/**
 * 自定义ScrollView
 *SetingUs
 */
public class SetingUs extends Activity implements PullScrollView.OnTurnListener {
    private PullScrollView mScrollView;
    private ImageView mHeadImg;

    private RelativeLayout relativeLayout_personal;
    private RelativeLayout relativeLayout_artribute;
    private RelativeLayout relativeLayout_develop;
    private RelativeLayout relativeLayout_aboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_us);

        initView();

        ActivityManagerApplication.addDestoryActivity(this,"SetingUs");
    }

//    public void findViews() {
//
//        relativeLayout_personal = (RelativeLayout)findViewById(R.id.scroll_view_head);
//        relativeLayout_personal.setOnClickListener(this);
//        relativeLayout_artribute = (RelativeLayout)findViewById(R.id.layout_mine_artribute);
//        relativeLayout_artribute.setOnClickListener(this);
//        relativeLayout_develop = (RelativeLayout) mView.findViewById(R.id.layout_mine_develop);
//        relativeLayout_develop.setOnClickListener(this);
//        relativeLayout_aboutApp = (RelativeLayout) mView.findViewById(R.id.layout_mine_aboutApp);
//        relativeLayout_aboutApp.setOnClickListener(this);
//    }

    protected void initView() {
        mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
        mHeadImg = (ImageView) findViewById(R.id.background_img);


        mScrollView.setHeader(mHeadImg);
        mScrollView.setOnTurnListener(this);
    }

    public void showAboutPerson(View view){
        Intent intent = new Intent(SetingUs.this, PersonalActivity.class);
        startActivity(intent);
    }
    public void showAboutArtribute(View view){
        Toast.makeText(SetingUs.this, "界面待添加", Toast.LENGTH_SHORT).show();
    }
    public void showAboutDevelop(View view){
        Toast.makeText(SetingUs.this, "界面待添加", Toast.LENGTH_SHORT).show();
    }
    public void showAboutApp(View view){
        Toast.makeText(SetingUs.this, "界面待添加", Toast.LENGTH_SHORT).show();
    }









//    public void showTable() {
//        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
//                TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER;
//        layoutParams.leftMargin = 30;
//        layoutParams.bottomMargin = 10;
//        layoutParams.topMargin = 10;
//
//        for (int i = 0; i < 5; i++) {
//            TableRow tableRow = new TableRow(this);
//            TextView textView = new TextView(this);
//            textView.setText("Test pull down scroll view " + i);
//            textView.setTextSize(20);
//            textView.setPadding(15, 15, 15, 15);
//
//            tableRow.addView(textView, layoutParams);
//            if (i % 2 != 0) {
//                tableRow.setBackgroundColor(Color.LTGRAY);
//            } else {
//                tableRow.setBackgroundColor(Color.WHITE);
//            }
//
//            final int n = i;
//            tableRow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(SetingUs.this, "Click item " + n, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            mMainLayout.addView(tableRow);
//        }
//    }

    @Override
    public void onTurn() {

    }

}
