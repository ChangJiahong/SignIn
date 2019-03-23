package com.demo.cjh.signin.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.KeyEvent
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
import com.demo.cjh.signin.pojo.Tables
import com.demo.cjh.signin.service.IClassesService
import com.demo.cjh.signin.service.IUpdateService
import com.demo.cjh.signin.service.impl.ClassesServiceImpl
import com.demo.cjh.signin.service.impl.UpdateServiceImpl
import com.demo.cjh.signin.util.Http
import com.demo.cjh.signin.util.HttpHelper
import com.demo.cjh.signin.util.doHttp
import com.demo.cjh.signin.util.doService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_stu_info.*
import kotlinx.android.synthetic.main.list_my_item.*
import org.jetbrains.anko.*
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var updateService: IUpdateService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }


    private fun init() {

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



        updateService = UpdateServiceImpl(this)

        // 同步数据
        updateData()

        updateFaceData()
        val uId = "user"

        // 自动登陆
        //initLogin()

    }

    /**
     * 数据同步
     */
    private fun updateData() {
        val sp = App.app.sp
        // 判断是否是登录状态
        if (sp.getBoolean("isLogin",false)){
            // 是就请求服务器同步数据
            val uId = sp.getString("userId","")
            doHttp {
                // 请求链接
                url = HttpHelper.upTables
                type = "application/json"
                //token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMzI3MDg1MTU0QHFxLmNvbSIsImNyZWF0ZWQiOjE1NDUyOTEyNTE0MDEsInJvbGVzIjpudWxsLCJpZCI6IjEiLCJleHAiOjE1NDU4OTYwNTEsInVzZXIiOnsiaWQiOiIxIiwiZW1haWwiOiIyMzI3MDg1MTU0QHFxLmNvbSIsIm5hbWUiOiJBZG1pbiIsInB3ZCI6IjEyMzQ1NiIsInNleCI6IueUtyIsImltZyI6bnVsbCwicGhvbmVOdW0iOm51bGwsInJvbGVzIjpudWxsfX0.MG2NLYn0hw7T4qJW4A4rL8-nfSeDa-0lhIpfRaa6HUQ"
                before {
                    // 加载数据

                    val tables = updateService.getAllStatusLessThan9(uId)
                    content = Gson().toJson(tables)
                    Log.d(TAG,"tables: $content")

                }
                success { status, msg, data ->
                    if (status != 200){
                        toast(msg)
                        return@success
                    }
                    val tables = Gson().fromJson(JSONObject(data as LinkedTreeMap<*,*>).toString(),Tables::class.java)
                    Log.d(TAG,"大小："+tables.getuId())
                    // 请求成功的数据更新到数据库
                    if (tables.getuId() == uId) {
                        doService {
                            run {
                                updateService.upTables(tables)
                            }
                            success {
                                // 上行同步成功
                                // 开启下行同步
                                doHttp {
                                    url = HttpHelper.downTables
                                    type = "application/json"
                                    //token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMzI3MDg1MTU0QHFxLmNvbSIsImNyZWF0ZWQiOjE1NDUyOTEyNTE0MDEsInJvbGVzIjpudWxsLCJpZCI6IjEiLCJleHAiOjE1NDU4OTYwNTEsInVzZXIiOnsiaWQiOiIxIiwiZW1haWwiOiIyMzI3MDg1MTU0QHFxLmNvbSIsIm5hbWUiOiJBZG1pbiIsInB3ZCI6IjEyMzQ1NiIsInNleCI6IueUtyIsImltZyI6bnVsbCwicGhvbmVOdW0iOm51bGwsInJvbGVzIjpudWxsfX0.MG2NLYn0hw7T4qJW4A4rL8-nfSeDa-0lhIpfRaa6HUQ"
                                    before {
                                        val maxAnchor = updateService.getAllMaxAnchor(uId)
                                        content = Gson().toJson(maxAnchor)
                                        Log.d(TAG,"tables: $content")
                                    }
                                    success { status, msg, data ->
                                        if (status != 200){
                                            toast(msg)
                                            return@success
                                        }
                                        var da = data as LinkedTreeMap<*, *>

                                        val table = Gson().fromJson(JSONObject(da).toString(), Tables::class.java)
                                        Log.d(TAG,"down："+table.getuId())
                                        // 请求成功的数据更新到数据库
                                        if (table.getuId() == uId) {
                                            doService {
                                                run {
                                                    updateService.downTables(table)
                                                }
                                                success {
                                                    // 完全同步成功
                                                    toast("同步成功")
                                                }
                                            }.start()
                                        }
                                    }
                                }.start()
                            }
                        }.start()
                    }
                }
            }.start()

        }
        // 否则什么都不做


    }


    private fun updateFaceData(){
        val sp = App.app.sp
        // 判断是否是登录状态
        if (sp.getBoolean("isLogin",false)) {
            // 是就请求服务器同步数据
            val uId = sp.getString("userId", "")


            doHttp {
                url = HttpHelper.upUserImg

                before {
                    val faceFs = updateService.getFaceFileStatusLessThan9()
                    val imgPs = ArrayList<String>()
                    faceFs.forEach {
                        imgPs.add(it.path)
                    }
                    files = imgPs
                }
                success { status, msg, data ->
                    if (status == 200) {
                        Log.v(TAG,"上传faceFile 成功")
                    } else {
                        Log.v(TAG,"上传faceFile 失败")
                    }
                }
            }.upload()
        }


    }
    private fun initLogin() {

        val sp = App.app.sp
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
                        var sp = App.app.sp
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

    override fun finish() {
        // 退出更新
        updateData()
        updateFaceData()
        super.finish()
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
