package com.demo.cjh.signin.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.util.FileUtil
import com.demo.cjh.signin.R
import org.jetbrains.anko.startActivity
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.demo.cjh.signin.App
import com.demo.cjh.signin.util.HttpHelper
import com.demo.cjh.signin.util.doHttp
import com.demo.cjh.signin.util.getVersion
import com.google.gson.internal.LinkedTreeMap
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import java.util.ArrayList


class IndexActivity : AppCompatActivity() {

    var isp = -1

    var versionOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        initPermission()  // 申请权限

        val sp = App.app.sp


        doHttp {
            url = "https://cjh.pythong.top/ip/"
            requestMethod = "GET"
            doOutput = false

            error {
                val nVersion = sp.getString("nVersion",getVersion(this@IndexActivity))
                if (nVersion != getVersion(this@IndexActivity)){
                    alert {
                        title="提示"
                        message = "检测到该应用版本太低，请至应用商店更新到最新版本后使用。"

                        positiveButton("确定"){
                            finish()
                        }
                    }.show()

                }else {
                    versionOk = true
                }
                Log.d("index", "错误")
            }

            success { status, msg, data ->
                if (status == 200){
                    val js = data as LinkedTreeMap<String, String>
                    val ip = js["ip"]
                    val v = js["version"]

                    Log.d("index","ip : ${ip}")
                    Log.d("index","v:$v")

                    if (getVersion(this@IndexActivity) != v){

                        val edit = sp.edit()
                        edit.putString("nVersion",v)
                        edit.apply()

                        alert {
                            title="提示"
                            message = "检测到该应用版本太低，请至应用商店更新到最新版本后使用。"
                            positiveButton("确定") {
                                System.exit(0)
                            }
                        }.show()
                    } else {
                        versionOk = true
                    }

                    HttpHelper.IP = data.toString()
                    App.app.ip = data.toString()


                }
            }
        }.start()







        object :Thread(){
            override fun run() {
                // 初始化app
                FileUtil.initFile(this@IndexActivity)

                sleep(3000)
                //startActivity<LoginActivity>()
                while (isp!=0){}

                while (!versionOk){

                }

                startActivity<MainActivity>()
                finish()
            }
        }.start()
    }





    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE)

        val toApplyList = ArrayList<String>()

        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm)
                // 进入到这里代表没有权限.
                Log.v("Index",perm+"未获取权限")
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        isp = toApplyList.size
        Log.v("权限注册",""+isp)
        if (!toApplyList.isEmpty()) {

            ActivityCompat.requestPermissions(this, toApplyList.toTypedArray(), 123)
            Log.v("Index","请求权限")
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        when (requestCode) {
            123 -> {
                for (i in 0 until grantResults.size) {

                    val grantResult = grantResults[i]
                    val s = permissions[i]
                    if (grantResult == PackageManager.PERMISSION_GRANTED) { //这个是权限拒绝
                        Log.v("Index", s + "权限申请成功")

                        //Toast.makeText(this, s + "权限申请成功", Toast.LENGTH_SHORT).show()
                    } else { //授权成功了
                        //do Something
                        Log.v("Index", s + "权限被拒绝了")
                        //Toast.makeText(this, s + "权限被拒绝了", Toast.LENGTH_SHORT).show()
                    }
                    isp -= 1
                    Log.v("权限",""+isp)
                }
            }
        }

    }





}
