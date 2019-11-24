package com.example.signapp;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Student implements Serializable {
    public Bitmap bp;//学生照片
    public String id;//学号
    public String stuclass;//班级
    public String name;//姓名
    public String sex;//性别
    public int score;//分数
    public int num_jiafen;//加分次数
    public int num_taoke;//逃课次数
    public int num_chidao;//迟到次数
    public int num_zaotui;//早退次数
    public int num_qingjia;//请假次数
    public Student()
    {
        stuclass=" ";
        id=" ";
        sex="";
        name=" ";
        score=100;
        num_jiafen=0;
        num_taoke=0;
        num_chidao=0;
        num_zaotui=0;
        num_qingjia=0;
    }
    public String getid()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getSex()
    {
        return sex;
    }
    public String getStuclass()
    {
        return stuclass;
    }
    public Bitmap getBp(){return bp;}
    public int getScore()
    {
        return score;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public void setid(String id)
    {
        this.id=id;
    }
    public void setSex(String sex)
    {
        this.sex=sex;
    }
    public void setStuclass(String stuclass)
    {
        this.stuclass=stuclass;
    }
    public void setBp(Bitmap bp){this.bp=bp;}
    public void setScore(int score) {
        this.score = score;
    }
}
