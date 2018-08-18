package com.bestyou.wtc1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class PersonalActivity extends BaseActivity implements
        View.OnClickListener {

    private CircleImageView cImageView;
    private ImageView bImageView;

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private File tempFile;

    private EditText editTextUserID;
    private EditText editTextUserName;

    private SharedPreferences sp;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        editTextUserID = (EditText)findViewById(R.id.user_id);
        editTextUserName = (EditText)findViewById(R.id.user_name);

        //用于自动登录,这里用于获取用户id
        sp=getSharedPreferences("setting", 0);
        username =sp.getString("username",null);

        if(username != null){
            editTextUserID.setText(username);
        }
        else {
            editTextUserID.setText("11012011911");
        }

        init();
    }

    /**
     * 初始化
     */
    public void init() {

        bImageView = (ImageView) findViewById(R.id.imageview_person_back);
        bImageView.setOnClickListener(this);

        cImageView = (CircleImageView) findViewById(R.id.circleimageview_personal_photo);
        cImageView.setOnClickListener(this);

    }

    public void loginOut(View view){
        //Toast.makeText(SetingUs.this, "界面待添加", Toast.LENGTH_SHORT).show();
        SharedPreferences spout =getSharedPreferences("setting", 0);
        SharedPreferences.Editor ed =spout.edit();
        ed.clear();
        ed.apply();
        startActivity(new Intent(PersonalActivity.this,SplashActivity.class));

//        finish();
//        ActivityManagerApplication.destoryActivity("SetingUs");
//        ActivityManagerApplication.destoryActivity("LuckPan");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imageview_person_back:

                finish();
                break;
            case R.id.circleimageview_personal_photo:

                new ActionSheetDialog(PersonalActivity.this)
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .addSheetItem("打开相册", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {

                                    @Override
                                    public void onClick(int which) {

                                        gallery();
                                    }
                                })
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {

                                    @Override
                                    public void onClick(int which) {

                                        camera();
                                    }
                                }).show();
                break;
            default:
                break;
        }

    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CANCELED) {

            return;
        }

        if (requestCode == PHOTO_REQUEST_GALLERY) {

            if (data != null) {

                Uri uri = data.getData();
                try {

                    crop(uri);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

        } else if (requestCode == PHOTO_REQUEST_CAMERA) {

            if (hasSdcard()) {

                tempFile = new File(Environment.getExternalStorageDirectory(),
                        IMAGE_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            } else {

                Toast.makeText(PersonalActivity.this, "未找到存储卡，无法存储照片！",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {

            if (data != null) {

                Bitmap bitmap = data.getParcelableExtra("data");
                cImageView.setImageBitmap(bitmap);

                tempFile = new File(Environment.getExternalStorageDirectory(),
                        IMAGE_FILE_NAME);
                if (tempFile.exists()) {

                    tempFile.delete();
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }
}
