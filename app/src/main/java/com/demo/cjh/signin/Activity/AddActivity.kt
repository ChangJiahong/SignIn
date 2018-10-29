package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.demo.cjh.signin.R
import com.demo.cjh.signin.obj.StudentInfo
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    val TAG = "AddActivity"
    var data = ArrayList<StudentInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var classId = intent.getStringExtra("classId")

        add_btn.setOnClickListener {

            if (inputId.text.isEmpty()){
                inputId.error = "学号不为空！"
                inputId.requestFocus()
            }else if (inputName.text.isEmpty()){
                inputName.error = "姓名不为空"
                inputName.requestFocus()
            }else {
                var index = count.text.toString().toInt()
                var id = inputId.text.toString()
                var name = inputName.text.toString()
                // 如果没有和id相同的返回true
                if(data.none{it.stuId == id}){

                }


                Log.v(TAG,"数据大小："+data.size.toString())
            }

        }

        // 上一个
        last_btn.setOnClickListener {
            var index = count.text.toString().toInt()
            if(index > 0){
                index--
                count.text = index.toString()
                inputId.setText(data[index].stuId)
                inputName.setText(data[index].name)
                //inputName.text = data[index].name
            }
        }

    }
}
