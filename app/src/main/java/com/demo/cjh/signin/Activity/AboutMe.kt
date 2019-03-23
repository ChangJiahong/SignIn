package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.getVersion
import kotlinx.android.synthetic.main.activity_about_me.*

class AboutMe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)
        title = "关于我"

        version.text = "version:"+getVersion(this)
    }
}
