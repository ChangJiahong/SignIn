package com.demo.cjh.signin.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.demo.cjh.signin.FileUtil
import com.demo.cjh.signin.R
import org.jetbrains.anko.startActivity
import java.util.ArrayList
import android.widget.Toast
import com.demo.cjh.signin.App
import com.demo.cjh.signin.util.Http
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.SocketTimeoutException
import android.os.StrictMode




class IndexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        object :Thread(){
            override fun run() {
                // 初始化app
                FileUtil.initFile()
                initPermission()  // 申请权限


                sleep(3000)
                //startActivity<LoginActivity>()
                // TODO:自动登录
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
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toTypedArray(), 123)
            Log.v("Index","请求权限")
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        when(requestCode){
            123 ->{
                for (i in 0 until grantResults.size) {

                    val grantResult = grantResults[i]
                    val s = permissions[i]
                    if (grantResult == PackageManager.PERMISSION_GRANTED) { //这个是权限拒绝
                        Log.v("Index",s + "权限申请成功")
                        //Toast.makeText(this, s + "权限申请成功", Toast.LENGTH_SHORT).show()
                    } else { //授权成功了
                        //do Something
                        Log.v("Index",s + "权限被拒绝了")
                        //Toast.makeText(this, s + "权限被拒绝了", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }



    }

}
