package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        init()
    }

    private fun init() {
        this.title = "使用帮助"


        text.text = "使用注意：\n 1、导入学生信息使用文件导入时：\n      需要注意的是所选文件类型是.xls即2010版Excel，并且表格第一行必须有“学号”“姓名”两列，否则会导入失败，一次性导入学生数量需少于100个\n\n" +
                "2、  在使用过程中如有任何问题请联系开发人员，联系方式QQ : 321168813.\n\n" +
                "声明：\n1、    人脸识别技术由虹软公司（ArcSoft）提供\n2、    表格库提供\n" +
                "https://github.com/huangyanbin/smartTable\n" +
                "\n" +
                "Copyright 2017 Huangyanbin."


    }
}
