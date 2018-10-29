package com.demo.cjh.signin

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import com.demo.cjh.signin.obj.User
import com.demo.cjh.signin.util.FaceDB
import com.demo.cjh.signin.util.MyDatabaseOpenHelper
import com.demo.cjh.signin.util.database

/**
 * Created by CJH
 * on 2018/8/2
 */
class App : Application() {
    val TAG = "App"

    companion object {


        public var app: App? = null
        internal var mFaceDB = FaceDB()

        var mImage: Uri? = null

        fun setCaptureImage(uri: Uri?) {
            mImage = uri
        }

        fun getCaptureImage(): Uri {
            return mImage!!
        }

    }

    var sp: SharedPreferences? = null
    var dsp: SharedPreferences? = null
    var db: MyDatabaseOpenHelper? = null
    var ip: String? = null
    var user = User()


    override fun onCreate() {
        super.onCreate()
        app = this@App
        sp = applicationContext.getSharedPreferences("config", Context.MODE_PRIVATE)
        dsp = PreferenceManager.getDefaultSharedPreferences(this)
        db = database
        ip = resources.getString(R.string.ip)


    }
}