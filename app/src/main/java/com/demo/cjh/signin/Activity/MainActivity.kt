package com.demo.cjh.signin.Activity

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import com.bumptech.glide.Glide
import com.demo.cjh.signin.App
import com.demo.cjh.signin.Fragment.FileFragment
import com.demo.cjh.signin.Fragment.MenuFragment
import com.demo.cjh.signin.Fragment.MyFragment
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.Http
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_my_item.*
import org.jetbrains.anko.*
import org.json.JSONObject
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }


    private fun init() {
        //val ft = fragmentManager.beginTransaction()

        var bundle = Bundle()
        bundle.putString("tag",TAG)
        tabhost.setup(this,supportFragmentManager,R.id.realtabcontent)

        tabhost.addTab(getTabView(R.string.tab_first,R.drawable.tab1_1),MenuFragment::class.java,bundle)
        tabhost.addTab(getTabView(R.string.tab_second,R.drawable.tab2_1),FileFragment::class.java,bundle)
        tabhost.addTab(getTabView(R.string.tab_third,R.drawable.tab3_1),MyFragment::class.java,bundle)
        tabhost.tabWidget.showDividers = LinearLayout.SHOW_DIVIDER_NONE
        // 初始化按钮
        check(0)
        tabhost.tabWidget.getChildTabViewAt(0).setOnClickListener { check(0) }
        tabhost.tabWidget.getChildTabViewAt(1).setOnClickListener { check(1) }
        tabhost.tabWidget.getChildTabViewAt(2).setOnClickListener { check(2) }


        // 自动登陆
        initLogin()

    }

    private fun initLogin() {

        val sp = App.app!!.sp!!
        val userid = sp.getString("userid","")
        var userToken = sp.getString("userToken","")
        if (userid.isNullOrEmpty() || userToken.isNullOrEmpty()){
            // 空

            return
        }
        doAsync {
            try {
                val result = Http.login_by_token(userid, userToken)
                var jsonObject = JSONObject(result)
                var status = jsonObject.getInt("status")
                when (status) {
                    1 -> {
                        // 登陆成功
                        var data = jsonObject.getString("data")
                        var jsonObject = JSONObject(data)
                        var name = jsonObject.getString("name")
                        var userToken = jsonObject.getString("userToken")
                        var imgUrl = jsonObject.getString("imgUrl")
                        var sp = App.app!!.sp!!
                        sp.edit().apply {
                            putString("name", name)
                            putString("userToken", userToken)
                            putString("imgUrl", imgUrl)
                            putBoolean("isLogin", true)
                            apply()
                        }
                        id_my_text.text = name
                        Glide.with(applicationContext).load(imgUrl).into(id_my_icon)

                    }

                    0 ->{

                        // 失败，过期
                        var sp = App.app!!.sp!!
                        sp.edit().apply {
                            putString("name", "")
                            putString("userToken", "")
                            putString("imgUrl", "")
                            putString("pwd","")
                            putBoolean("isLogin", false)
                            apply()
                        }

                        runOnUiThread {
                            toast("登陆过期，请重新登陆！")
                        }
                    }
                }
            }catch (e: SocketTimeoutException){
                "timeOut"
            }


        }
    }

    fun getTabView(textId: Int,imgId: Int): TabHost.TabSpec{
        var text = resources.getString(textId)
        var tab_item = layoutInflater.inflate(R.layout.tab_item_layout,null)
        var item_text = tab_item.find<TextView>(R.id.tab_text)
        var item_img = tab_item.find<ImageView>(R.id.tab_img)

        item_text.text = text
        item_img.imageResource = imgId
        var spec = tabhost.newTabSpec(text).setIndicator(tab_item)
        return spec
    }

    fun check(po: Int){
        tabhost.currentTab = po
        for(i in (0..(tabhost.tabWidget.childCount-1))){
            var text = tabhost.tabWidget.getChildAt(i).find<TextView>(R.id.tab_text)
            var img = tabhost.tabWidget.getChildAt(i).find<ImageView>(R.id.tab_img)
            if(po == i){
                text.setTextColor(Color.parseColor("#007FFF"))
                when(i){
                    0 -> img.imageResource = R.drawable.tab1_2
                    1 -> img.imageResource = R.drawable.tab2_2
                    2 -> img.imageResource = R.drawable.tab3_2
                }
            }else{
                text.textColorResource = R.color.defult
                when(i){
                    0 -> img.imageResource = R.drawable.tab1_1
                    1 -> img.imageResource = R.drawable.tab2_1
                    2 -> img.imageResource = R.drawable.tab3_1
                }
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
