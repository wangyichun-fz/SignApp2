package com.example.signapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZJH on 2016-10-22-0022.
 */

public class BeginsignActivity extends Activity {

    int count_student;//学生数目

    Map<String,String> map_call_role;

    ListView listView;
    SimpleAdapter simpleAdapter;
    List<Map<String,Object>> list_student;
    List<Map<String,Object>> list_student_class;//代表相同班级的学生
    String []from={"photo","sno","sname","sclass"};
    int []to={R.id.photo_item3,R.id.id_item3,R.id.name_item3,R.id.class_item3};
    List<String> list_class=new ArrayList<String>(); //Spinner 的item  list
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    Cursor cursor;//查询结果
    ImageView view;
    TextView savetext;
    int student_position;//代表当前点击的点名的学生的位置

    LayoutInflater inflater;
    RadioGroup radioGroup;
    RadioButton radioButton_arrive;
    RadioButton radioButton_late;
    RadioButton radioButton_skip;
    RadioButton radioButton_early;
    RadioButton radioButton_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginsign);

        listView=findViewById(R.id.stulist1);
        savetext=findViewById(R.id.tv_save);
        savetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveclick();
            }
        });
        spinner=findViewById(R.id.sp_class);
        readDataBase(this); //打开数据库  查询数据
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_class);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择班级代码
                String classes=list_class.get(position);//得到spinner当前选择的班级
                list_student_class=new ArrayList<Map<String, Object>>();
                for (Map<String,Object> map: list_student) {
                    String cla=(String)map.get("classes");
                    if(cla.equals(classes))
                    {
                        list_student_class.add(map);
                    }
                }
                count_student=list_student_class.size();
                simpleAdapter=new SimpleAdapter(BeginsignActivity.this,list_student_class,R.layout.listview_item3,from,to);
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
                //listView.setAdapter(simpleAdapter);
                listView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return count_student;
                    }

                    @Override
                    public Object getItem(int position) {
                        return list_student_class.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final View v;
                        if(convertView==null)
                        {
                            inflater= LayoutInflater.from(BeginsignActivity.this);
                            v=inflater.inflate(R.layout.listview_item3,null);
                        }else{
                            v=convertView;
                        }
                        final Map<String,Object> map=(Map<String,Object>)getItem(position);
                        radioGroup=v.findViewById(R.id.radioGroup);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton rb=v.findViewById(group.getCheckedRadioButtonId());
                                try{
                                    map_call_role.remove((String) map.get("sno"));
                                }
                                catch (Exception e){}
                                map_call_role.put((String) map.get("sno"),rb.getText().toString());
                            }
                        });
                        radioButton_arrive=v.findViewById(R.id.bu_chuqin);
                        radioButton_late=v.findViewById(R.id.bu_chidao);
                        radioButton_skip=v.findViewById(R.id.bu_taoke);
                        radioButton_early=v.findViewById(R.id.bu_zaotui);
                        radioButton_off=v.findViewById(R.id.bu_qingjia);


                        ImageView iv=v.findViewById(R.id.photo_item3);
                        iv.setImageBitmap((Bitmap) map.get("photo"));

                        TextView tv_sno=v.findViewById(R.id.id_item3);
                        tv_sno.setText((String)map.get("sno"));

                        TextView tv_sname=v.findViewById(R.id.name_item3);
                        tv_sname.setText((String)map.get("name"));
                        if(map_call_role.get(map.get("sno")).equals("出勤"))
                        {
                            radioButton_arrive.setChecked(true);
                        }
                        else if(map_call_role.get(map.get("sno")).equals("迟到"))
                        {
                            radioButton_late.setChecked(true);
                        }
                        else if(map_call_role.get(map.get("sno")).equals("逃课"))
                        {
                            radioButton_skip.setChecked(true);
                        }
                        else if(map_call_role.get(map.get("sno")).equals("请假"))
                        {
                            radioButton_off.setChecked(true);
                        }
                        else if(map_call_role.get(map.get("sno")).equals("早退"))
                        {
                            radioButton_early.setChecked(true);
                        }
                        TextView tv_class=v.findViewById(R.id.class_item3);
                        tv_class.setText((String)map.get("classes"));
                        return v;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        view=findViewById(R.id.photo_item3);
    }

    void readDataBase(Context context)
    {
        map_call_role=new HashMap<String,String>();
        list_student=new ArrayList<Map<String,Object>>();
        SQLiteDatabase db=new MySQLiteAccess(context,1).getReadableDatabase();
        cursor=db.query(context.getString(R.string.database_name_info), new String[]{"photo","sno","sname","sclass"},null,null,null,null,null);
        if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                Map<String,Object> map=new HashMap<String,Object>();
                byte []bytes=cursor.getBlob(cursor.getColumnIndex("photo"));
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                map.put("photo",bitmap);
                String no=cursor.getString(1);
                map.put("sno",no);
                map_call_role.put(no,"出勤");
                String cla=cursor.getString(3);
                if(!list_class.contains(cla))
                {
                    list_class.add(cla);
                }
                map.put("classes",cla);
                String name=cursor.getString(2);
                map.put("name",name);
                list_student.add(map);
            }
        }
        count_student=list_student.size();
        list_student_class=list_student;
    }
    public void saveclick()
    {
        Cursor cursor;
        int score;
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();
        for (Map<String,Object> map:list_student) {
            String snum=(String) map.get("sno");
            String sql="update student set ";
            if(map_call_role.get(snum).equals("迟到")) {
                sql=sql+" num_chidao=num_chidao+1 where sno="+snum;
                db.execSQL(sql);
                cursor=db.rawQuery("select score from student where sno=?",new String[]{snum});
                if(cursor!=null&&cursor.getCount()>0) {
                    while (cursor.moveToNext()) {
                        score = cursor.getInt(0);
                        score = score - ScoreActivity.chidao;
                        db.execSQL("update student set score="+score+" where sno=?", new String[]{snum}); } } }
            else if(map_call_role.get(snum).equals("早退")) {
                sql=sql+" num_zaotui=num_zaotui+1 where sno="+snum;
                db.execSQL(sql);
                cursor=db.rawQuery("select score from student where sno=?",new String[]{snum});
                if(cursor!=null&&cursor.getCount()>0) {
                    while (cursor.moveToNext()) {
                        score = cursor.getInt(0);
                        score = score - ScoreActivity.zaotui;
                        db.execSQL("update student set score="+score+" where sno=?", new String[]{snum});
                    }
                }
            }
            else if(map_call_role.get(snum).equals("逃课"))
            {
                sql=sql+" num_taoke=num_taoke+1 where sno="+snum;
                db.execSQL(sql);
                cursor=db.rawQuery("select score from student where sno=?",new String[]{snum});
                if(cursor!=null&&cursor.getCount()>0) {
                    while (cursor.moveToNext()) {
                        score = cursor.getInt(0);
                        score = score - ScoreActivity.taoke;
                        db.execSQL("update student set score="+score+" where sno=?", new String[]{snum});
                    }
                }
            }
            else if(map_call_role.get(snum).equals("请假"))
            {
                sql=sql+" num_qingjia=num_qingjia+1 where sno="+snum;
                db.execSQL(sql);
                cursor=db.rawQuery("select score from student where sno=?",new String[]{snum});
                if(cursor!=null&&cursor.getCount()>0) {
                    while (cursor.moveToNext()) {
                        score = cursor.getInt(0);
                        score = score - ScoreActivity.qingjia;
                        db.execSQL("update student set score="+score+" where sno=?", new String[]{snum});
                    }
                }
            }
        }
        db.close();
        Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
    }
}
