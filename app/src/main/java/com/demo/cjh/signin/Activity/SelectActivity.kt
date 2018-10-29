package com.demo.cjh.signin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.activity_select.*

/**
 * 功能选择页面
 */
class SelectActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var classId: String
    private lateinit var className: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")

        title = className

        kaoQin.setOnClickListener(this)
        jiLu.setOnClickListener(this)
        shiYan.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.kaoQin ->{
                goTo(SignOldActivity::class.java)
            }
            R.id.jiLu ->{
                goTo(GListActivity::class.java)
            }
            R.id.shiYan ->{
                goTo(TestListActivity::class.java)
            }
        }
    }

    fun goTo(activity: Class<*>){
        val intent = Intent(this,activity)
        intent.putExtra("classId",classId)
        intent.putExtra("className",className)
        startActivity(intent)
    }
}
