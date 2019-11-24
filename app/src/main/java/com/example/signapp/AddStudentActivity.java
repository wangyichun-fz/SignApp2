package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity  implements View.OnClickListener{
    TextView et_id;
    TextView et_name;
    Spinner et_sex;
    Spinner et_class;
    Button save;
    ImageView image;
    Student student=new Student();
    List<String> classes;
    private static final int CAMERA_REQUEST_CODE = 547;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        et_id=findViewById(R.id.et_id);
        et_name=findViewById(R.id.et_name);
        et_sex=findViewById(R.id.et_sex);
        image=findViewById(R.id.imageView2);
        et_class=findViewById(R.id.et_class);
        save=findViewById(R.id.button_save);
        save.setOnClickListener(this);
        image.setOnClickListener(this);
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
        Cursor cursor=db.rawQuery("select *from stuclass",null);
        classes=new ArrayList<>();
        if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                classes.add(cursor.getString(0));
            }
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,classes);
        et_class.setAdapter(arrayAdapter);
    }
    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.button_save:
                SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
                if(student.bp==null||et_id.getText().toString().length()==0||et_name.getText().toString().length()==0
                        ||et_sex.getSelectedItem().toString().length()==0) {
                    Toast.makeText(AddStudentActivity.this,"信息输入及照片不能为空!",Toast.LENGTH_SHORT).show();
                    return;
                }
                student.name=et_name.getText().toString().trim();
                student.stuclass=et_class.getSelectedItem().toString().trim();
                student.id=et_id.getText().toString().trim();
                student.sex=et_sex.getSelectedItem().toString().trim();
                long flag=-1;
                ContentValues values=new ContentValues();
                ByteArrayOutputStream os=new ByteArrayOutputStream();
                student.bp.compress(Bitmap.CompressFormat.PNG,100,os);
                values.put("sno",student.id);
                values.put("sname",student.name);
                values.put("ssex",student.sex);
                values.put("sclass",student.stuclass);
                values.put("photo",os.toByteArray());
                values.put("score",student.score);
                values.put("num_jiafen",student.num_jiafen);
                values.put("num_taoke",student.num_taoke);
                values.put("num_zaotui",student.num_zaotui);
                values.put("num_qingjia",student.num_qingjia);
                values.put("num_chidao",student.num_chidao);
                db.insert("student",null,values);
                Toast.makeText(AddStudentActivity.this,"保存成功!",Toast.LENGTH_LONG).show();
                db.close();
                break;
            case R.id.imageView2:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("请选择头像来源");
                final String []source={"本地图库","启动摄像头拍照"};
                builder.setItems(source, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, 1);
                        }
                        else{
                            if(hasCamera()) {
                                if(Build.VERSION.SDK_INT>=23){
                                    ActivityCompat.requestPermissions(AddStudentActivity.this,new String[]{
                                            Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                }
                                else{
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, 2);
                                }
                            } else{
                                Toast.makeText(AddStudentActivity.this, "该设备没有摄像装备", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.show();
                break;
        }
    }
    public static boolean hasCamera() {
        return hasBackFacingCamera() || hasFrontFacingCamera();
    }
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
    private static boolean checkCameraFacing(final int facing) {
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    //申请相机权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }
                else{
                    Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            try
            {
                Uri uri=data.getData();
                ContentResolver cr = this.getContentResolver();
                student.bp=getBitmapFormUri(this,  uri);
                image.setImageBitmap(student.bp);
            }
            catch (Exception e) { }
        }
        if(requestCode==2) {
            try {
                student.bp=compressImage((Bitmap)data.getExtras().get("data"));
                image.setImageBitmap(student.bp);
            }
            catch (Exception e) {
            }
        }
    }

    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return compressImage(bitmap);//再进行质量压缩
    }
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

}
