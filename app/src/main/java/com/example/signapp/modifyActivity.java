package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class modifyActivity extends AppCompatActivity implements View.OnClickListener{
    TextView et_id;
    TextView et_name;
    Spinner et_sex;
    Spinner et_class;
    Button save;
    ImageView image;
    Student student=new Student();
    Cursor cursor;
    List<String>classes;
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
        Bundle bundle=getIntent().getExtras();
        String sno=bundle.getString("sno");
        System.out.println(sno);
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
        cursor=db.rawQuery("select * from student where sno=?",new String[]{sno});
        //cursor=db.query(context.getString(R.string.database_name_info), new String[]{"photo","sno","sname","ssex","sclass"},null,null,null,null,null);
        Map<String, Object> map = new HashMap<String, Object>();
        if(cursor!=null&&cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex("photo"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                map.put("photo", bitmap);
                String no = cursor.getString(0);
                map.put("sno", no);
                String cla = cursor.getString(1);
                map.put("classes", cla);
                String sex = cursor.getString(3);
                map.put("sex", sex);
                String name = cursor.getString(2);
                map.put("name", name);
            }
        }
        cursor=db.rawQuery("select *from stuclass",null);
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

        image.setImageBitmap((Bitmap) map.get("photo"));
        et_id.setText((String)map.get("sno"));
        et_name.setText((String)map.get("name"));
        String sex=(String)map.get("sex");
        if(sex=="男")
        {
            et_sex.setSelection(0);
        }
        else
            et_sex.setSelection(1);
        et_class.setSelection(classes.indexOf((String)map.get("classes")));
    }
    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.button_save:
                SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
                if(student.bp==null||et_id.getText().toString().length()==0||et_name.getText().toString().length()==0
                        ||et_sex.getSelectedItem().toString().length()==0)
                {
                    Toast.makeText(modifyActivity.this,"信息输入及照片不能为空!",Toast.LENGTH_SHORT).show();
                    return;
                }
                student.name=et_name.getText().toString().trim();
                student.stuclass=et_class.getSelectedItem().toString().trim();
                student.id=et_id.getText().toString().trim();
                student.sex=et_sex.getSelectedItem().toString().trim();
                ByteArrayOutputStream os=new ByteArrayOutputStream();
                student.bp.compress(Bitmap.CompressFormat.PNG,100,os);
                db.execSQL("update student set sname=?,ssex=?,sclass=?,photo=? where sno=?",new Object[]{student.name,student.sex,student.stuclass,os.toByteArray(),student.id});
                Toast.makeText(modifyActivity.this,"保存成功!",Toast.LENGTH_LONG).show();
                db.close();
                break;
            case R.id.imageView2:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("请选择头像来源");
                final String []source={"本地图库","启动摄像头拍照"};
                builder.setItems(source, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)//选择的是本地图库
                        {
                            Intent intent = new Intent();
                            /* 开启Pictures画面Type设定为image */
                            intent.setType("image/*");
                            /* 使用Intent.ACTION_GET_CONTENT这个Action */
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra("return-data", true);  //是否要返回值。 一般都要
                            /* 取得相片后返回本画面 */
                            startActivityForResult(intent, 1);
                        }
                        else{//启动摄像头拍摄
                            if(hasCamera())
                            {
                                if(Build.VERSION.SDK_INT>=23)//安卓6.0以后需要动态申请权限
                                {
                                    ActivityCompat.requestPermissions(modifyActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                }
                                else{
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, 2);
                                }
                            }
                            else
                            {
                                Toast.makeText(modifyActivity.this, "该设备没有摄像装备", Toast.LENGTH_SHORT).show();
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
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
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
        if(requestCode==1)
        {
            try
            {
                Uri uri=data.getData();
                ContentResolver cr = this.getContentResolver();
                //getThumbnail(uri,500 , cr.openInputStream(uri));
                //student.bp= BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
                //iv_photo.setImageBitmap(student.bp);
                student.bp=getBitmapFormUri(this,  uri);
                image.setImageBitmap(student.bp);
            }
            catch (Exception e)
            {
                //Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==2)
        {
            try
            {
                student.bp=compressImage((Bitmap)data.getExtras().get("data"));
                image.setImageBitmap(student.bp);
            }
            catch (Exception e)
            {
                //Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
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
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
