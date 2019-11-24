package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddClassActivity extends AppCompatActivity implements View.OnClickListener {
    Button addclass;
    Button delclass;
    ListView classlist;
    EditText et_class;
    List<String> classes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        addclass=findViewById(R.id.class_add);
        delclass=findViewById(R.id.class_del);
        classlist=findViewById(R.id.classlist);
        et_class=findViewById(R.id.et_class);
        addclass.setOnClickListener(this);
        delclass.setOnClickListener(this);
        readDataBase();
    }
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.class_add:
                if(et_class.getText().toString().length()==0)
                {
                    Toast.makeText(this, "班级名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (String s:classes) {
                    if(s.equals(et_class.getText().toString()))
                    {
                        Toast.makeText(this, "班级名称已经存在", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                SQLiteDatabase db=new MySQLiteAccess(this,1).getWritableDatabase();
                db.execSQL("insert into stuclass(classname) values('"+et_class.getText().toString().trim()+"')");
                Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
                et_class.setText("");
                readDataBase();
                break;
            case R.id.class_del:
                if(classes.size()<=0) {
                    Toast.makeText(this, "还没有班级，不能删除", Toast.LENGTH_SHORT).show();
                    return; }
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("删除班级");
                final String [] strings=new String[classes.size()];
                int i=0;
                for (String s:classes) {
                    strings[i]=classes.get(i);
                    i++; }
                final List<String> del=new ArrayList<>();
                builder.setMultiChoiceItems(strings, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked==true) {
                            del.add(strings[which]);
                        }
                        if(isChecked==false) {
                            del.remove(strings[which]);
                        } }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder b=new AlertDialog.Builder(AddClassActivity.this);
                        b.setTitle("提醒");
                        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (String s:del) {
                                    SQLiteDatabase db=new MySQLiteAccess(AddClassActivity.this,1).getWritableDatabase();
                                    db.execSQL("delete from stuclass where classname='"+s+"'");
                                    readDataBase(); }
                                Toast.makeText(AddClassActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        b.setNegativeButton("取消",null);
                        b.show();
                    }
                });
                builder.setNeutralButton("取消",null);
                builder.show();
                break;

        }
    }
    public void readDataBase()
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
        classlist.setAdapter(arrayAdapter);
        db.close();
    }
}
