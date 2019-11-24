package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button addstu;
    Button addclass;
    Button score;
    Button progress;
    Button beginsign;
    Button stuinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addstu=findViewById(R.id.button_addstu);
        addclass=findViewById(R.id.button_addclass);
        score=findViewById(R.id.button_score);
        progress=findViewById(R.id.button_progress);
        beginsign=findViewById(R.id.button_begin);
        stuinfo=findViewById(R.id.button_suInfo);
        addstu.setOnClickListener(this);
        addclass.setOnClickListener(this);
        score.setOnClickListener(this);
        progress.setOnClickListener(this);
        beginsign.setOnClickListener(this);
        stuinfo.setOnClickListener(this);
        SQLiteDatabase db=new MySQLiteAccess(this,1).getReadableDatabase();//创建数据库，若已经创建则跳过
    }
    public void onClick(View view)
    {
        Intent intent=new Intent();
        switch (view.getId())
        {
            case R.id.button_addstu:
                intent.setClass(this,AddStudentActivity.class);
                startActivity(intent);
                break;
            case R.id.button_addclass:
                intent.setClass(this,AddClassActivity.class);
                startActivity(intent);
                break;
            case R.id.button_score:
                intent.setClass(this,ScoreActivity.class);
                startActivity(intent);
                break;
            case R.id.button_progress:
                intent.setClass(this,ProgressActivity.class);
                startActivity(intent);
                break;
            case R.id.button_begin:
                intent.setClass(this,BeginsignActivity.class);
                startActivity(intent);
                break;
            case R.id.button_suInfo:
                intent.setClass(this,StudentInfo.class);
                startActivity(intent);
                break;
        }

    }
}

