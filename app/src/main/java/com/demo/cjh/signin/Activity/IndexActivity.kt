package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.R
import org.jetbrains.anko.startActivity

class IndexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        object :Thread(){
            override fun run() {
                Thread.sleep(3000)
                //startActivity<LoginActivity>()
                // TODO:自动登录
                startActivity<MainActivity>()
                finish()
            }
        }.start()
    }
}
