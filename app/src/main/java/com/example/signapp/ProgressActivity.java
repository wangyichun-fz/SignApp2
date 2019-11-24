package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {
    Spinner spclass;
    ListView stulist;
    List<Map<String,Object>> students;
    List<String> classes;
    Cursor cursor;
    SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        spclass=findViewById(R.id.sp_class);
        stulist=findViewById(R.id.stulist1);
        spclass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                readstudent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        readclass();
    }
    public void readclass()
    {
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
        Cursor cursor=db.rawQuery("select *from stuclass",null);
        classes=new ArrayList<>();
        if(cursor!=null&&cursor.getCount()>0) {
            while(cursor.moveToNext()) {
                classes.add(cursor.getString(0));
            }
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,classes);
        spclass.setAdapter(arrayAdapter);
        db.close();
    }
    public void readstudent() {
        students=new ArrayList<Map<String,Object>>();
        String stuclass=spclass.getSelectedItem().toString().trim();
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
        cursor=db.rawQuery("select * from student where sclass=?",new String[]{stuclass});
        if(cursor!=null&&cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex("photo"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                map.put("bp", bitmap);
                String id = cursor.getString(0);
                map.put("id", id);
                String name = cursor.getString(2);
                map.put("name", name);
                int num_jiafen = cursor.getInt(5);
                map.put("num_jiafen", num_jiafen);
                int num_taoke=cursor.getInt(6);
                map.put("num_taoke",num_taoke);
                int num_zaotui=cursor.getInt(7);
                map.put("num_zaotui",num_zaotui);
                int num_chidao=cursor.getInt(9);
                map.put("num_chidao",num_chidao);
                int num_qingjia=cursor.getInt(8);
                map.put("num_qingjia",num_qingjia);
                int score=cursor.getInt(10);
                map.put("score",score);
                students.add(map);
            }
        }
        String []from={"bp","id","name","num_jiafen","num_taoke","num_zaotui","num_chidao","num_qingjia","score"};
        int []to={R.id.photo_item2,R.id.id_item2,R.id.name_item2,R.id.add_item,R.id.taoke_item,R.id.zaotui_item,
        R.id.chidao_item,R.id.qingjia_item,R.id.score_item};
        simpleAdapter=new SimpleAdapter(ProgressActivity.this,students,R.layout.listview_item2,from,to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            //simpleAdapter不支持bitmap或是 imageview  默认的只是支持ImageView的id
            //这里添加支持
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv=(ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                }else{
                    return false;
                }
            }
        });
        stulist.setAdapter(simpleAdapter);
        db.close();
    }
}
