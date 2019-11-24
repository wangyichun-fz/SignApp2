package com.example.signapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentInfo extends Activity {

    int count_student;//学生数目
    String sname;
    String sno;
    String ssex;
    String sclass;
    Map<String,String> map_call_role;
    Cursor cursor_student;
    ListView listView;
    Bitmap photo;
    SimpleAdapter simpleAdapter;
    List<Map<String,Object>> list_student;
    List<Map<String,Object>> list_student_class;//代表相同班级的学生
    String []from={"photo","sno","sname","sex","sclass"};
    int []to={R.id.photo_item,R.id.id_item,R.id.name_item,R.id.sex_item,R.id.class_item};
    List<String> list_class=new ArrayList<String>(); //Spinner 的item  list
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    Cursor cursor;//查询结果
    ImageView view;
    int student_position;//代表当前点击的点名的学生的位置
    Button bu_modify;
    Button bu_del;

    LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        Intent intent=this.getIntent();
        sno=intent.getStringExtra("sno");
        intent.putExtra("flag","no");
        setResult(1,intent);
        listView=findViewById(R.id.stulist1);
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
                simpleAdapter=new SimpleAdapter(StudentInfo.this,list_student_class,R.layout.listview_item,from,to);
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
                        if(convertView==null) {
                            inflater= LayoutInflater.from(StudentInfo.this);
                            v=inflater.inflate(R.layout.listview_item,null);
                        }else{
                            v=convertView;
                        }
                        final Map<String,Object> map=(Map<String,Object>)getItem(position);
                        bu_modify=v.findViewById(R.id.button_modify);
                        bu_del=v.findViewById(R.id.button_del);
                        bu_modify.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent();
                                intent.setClass(StudentInfo.this,modifyActivity.class);
                                String id=(String)map.get("sno");
                                intent.putExtra("sno",id);
                                startActivity(intent);
                            }
                        });
                        bu_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String id=(String)map.get("sno");
                                final AlertDialog.Builder builder=new AlertDialog.Builder(StudentInfo.this);
                                builder.setTitle("确定删除该学生信息？");
                                builder.setNegativeButton("取消",null);
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delInfo(StudentInfo.this,id);
                                        readDataBase(StudentInfo.this);
                                        arrayAdapter.notifyDataSetChanged();
                                        Toast.makeText(StudentInfo.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();
                                //readDataBase(StudentInfo.this);
                            }
                        });
                        ImageView iv=v.findViewById(R.id.photo_item);
                        iv.setImageBitmap((Bitmap) map.get("photo"));

                        TextView tv_sno=v.findViewById(R.id.id_item);
                        tv_sno.setText((String)map.get("sno"));

                        TextView tv_sname=v.findViewById(R.id.name_item);
                        tv_sname.setText((String)map.get("name"));

                        TextView tv_sex=v.findViewById(R.id.sex_item);
                        tv_sex.setText((String)map.get("sex"));

                        TextView tv_class=v.findViewById(R.id.class_item);
                        tv_class.setText((String)map.get("classes"));
                        return v;
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        view=findViewById(R.id.photo_item);
    }

    void readDataBase(Context context)
    {
        map_call_role=new HashMap<String,String>();
        list_student=new ArrayList<Map<String,Object>>();
        SQLiteDatabase db=new MySQLiteAccess(context,1).getReadableDatabase();
        cursor=db.query(context.getString(R.string.database_name_info), new String[]{"photo","sno","sname","ssex","sclass"},null,null,null,null,null);
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

                String cla=cursor.getString(4);
                if(!list_class.contains(cla))
                {
                    list_class.add(cla);
                }

                String sex=cursor.getString(3);
                map.put("sex",sex);

                map.put("classes",cla);
                String name=cursor.getString(2);

                map.put("name",name);
                list_student.add(map);
            }
        }
        count_student=list_student.size();
        list_student_class=list_student;
    }
    void delInfo(Context context,String id)//根据学号删除学生信息
    {
        SQLiteDatabase db=new MySQLiteAccess(context,1).getReadableDatabase();
        db.execSQL("delete from student where sno=?",new String[]{id});
        db.close();
    }
}

