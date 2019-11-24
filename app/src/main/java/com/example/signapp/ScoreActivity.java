package com.example.signapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner sp_jiafen;
    Spinner sp_taoke;
    Spinner sp_zaotui;
    Spinner sp_chidao;
    Spinner sp_qingjia;
    Button button;
    ArrayAdapter arrayAdapter;
    List lists=new ArrayList<String>();
    public static int jiafen=0;
    public static int taoke=0;
    public static int zaotui=0;
    public static int chidao=0;
    public static int qingjia=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lists.add("0");
        lists.add("1");
        lists.add("2");
        lists.add("3");
        lists.add("4");
        lists.add("5");
        arrayAdapter=new ArrayAdapter<String>(this,android.
                R.layout.simple_dropdown_item_1line,lists);
        sp_jiafen.setAdapter(arrayAdapter);
        sp_chidao.setAdapter(arrayAdapter);
        sp_qingjia.setAdapter(arrayAdapter);
        sp_zaotui.setAdapter(arrayAdapter);
        sp_taoke.setAdapter(arrayAdapter);
        button=findViewById(R.id.button);
        button.setOnClickListener(this); setContentView(R.layout.activity_score);
        sp_jiafen=findViewById(R.id.sp_huida);
        sp_taoke=findViewById(R.id.sp_taoke);
        sp_zaotui=findViewById(R.id.sp_zaotui);
        sp_chidao=findViewById(R.id.sp_chidao);
        sp_qingjia=findViewById(R.id.sp_qingjia);
        sp_jiafen.setSelection(ScoreActivity.jiafen);
        sp_taoke.setSelection(ScoreActivity.taoke);
        sp_zaotui.setSelection(ScoreActivity.zaotui);
        sp_chidao.setSelection(ScoreActivity.chidao);
        sp_qingjia.setSelection(ScoreActivity.qingjia);
    }
    @Override
    public void onClick(View view)
    {
        jiafen=Integer.parseInt(sp_jiafen.getSelectedItem().toString());
        taoke=Integer.parseInt(sp_taoke.getSelectedItem().toString());
        zaotui=Integer.parseInt(sp_zaotui.getSelectedItem().toString());
        chidao=Integer.parseInt(sp_chidao.getSelectedItem().toString());
        qingjia=Integer.parseInt(sp_qingjia.getSelectedItem().toString());
        Toast.makeText(ScoreActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
    }
}
